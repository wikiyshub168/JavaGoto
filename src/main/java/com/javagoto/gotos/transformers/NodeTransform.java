/*
 * NodeTransform.java
 *
 * Created on June 27, 2007, 7:38 PM
 * Copyright footloosejava.
 * Email: footloosejava@gmail.com
 */
package com.javagoto.gotos.transformers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import com.javagoto.gotos.Goto;
import static com.javagoto.gotos.transformers.Op.*;

/**
 *
 * @author footloosejava
 */
public class NodeTransform {

    static final boolean debug = false;
    // the class that actually does the gotos impl.
    static final String GOTO_IMPL_CLASS = Goto.class.getName().replace('.', '/');
    static final String OBJECT_CLASS = Object.class.getName().replace('.', '/');
    // method names for these fuctions
    static final String MARK_JUMP = "jump";
    static final String MARK_LABEL = "label";

    public synchronized static boolean makeChanges(final ClassNode cn) throws LabelNotFound, DuplicateLabel {


        // we will put all nodes found where markers for labels and nodes are present
        final List<AbstractInsnNode> labels = new ArrayList<AbstractInsnNode>();
        final List<AbstractInsnNode> jumps = new ArrayList<AbstractInsnNode>();

        final Map<Integer, LabelNode> labelRefs = new HashMap<Integer, LabelNode>();

        boolean hasJumps = false;

        for (Object o : cn.methods) {

            MethodNode mn = (MethodNode) o;


            extractJumpData(labels, cn, jumps, mn);
            // process

            // do we have any jumps to process???
            // if not, exit routine and ignore class
            if (jumps.size() > 0) {
                hasJumps = true;

                if (debug) {
                    System.out.println("************ Process Jumps *************");
                }

                insertLabelNodes(labelRefs, labels, mn, cn);

                // connect to which lables!
                makeJumpsToLabels(jumps, mn, labelRefs, cn);
            }

            jumps.clear();
            labels.clear();
            labelRefs.clear();

        }
        return hasJumps;
    }

    private static void makeJumpsToLabels(final List<AbstractInsnNode> jumps,
            final MethodNode mn, final Map<Integer, LabelNode> myRefs,
            final ClassNode cn) throws LabelNotFound {


        // join labels to jump data
        if (debug) {
            System.out.println("Inserting Jump Sequences:");
        }

        for (AbstractInsnNode node : jumps) {
            AbstractInsnNode pNode = (AbstractInsnNode) node.getPrevious();


            int labelNumber = getOperand(cn.name, pNode);

            if (debug) {
                System.out.println("\tGoto target: " + labelNumber);
            }

            if (!myRefs.containsKey(labelNumber)) {
                throw new LabelNotFound("Class: " + cn.name + ".  Label not found for goto (jump) to: " + labelNumber);
            }

            LabelNode labelNode = myRefs.get(labelNumber);
            
            if (labelNode != null) {
                JumpInsnNode jumpNode = new JumpInsnNode(Op_goto, labelNode);
                mn.instructions.insert(node, jumpNode);
            }
        }
    }

    private static void insertLabelNodes(
            final Map<Integer, LabelNode> labelRefs,
            final List<AbstractInsnNode> labels,
            final MethodNode mn,
            final ClassNode cn)
            throws DuplicateLabel {


        // insert labels
        if (debug) {
            System.out.println("Inserting label refs.");
        }

        // map to keep all labelNode objects inserted before label refs in.
        labelRefs.clear();

        for (AbstractInsnNode node : labels) {
            AbstractInsnNode operandNode = (AbstractInsnNode) node.getPrevious();
            int labelNumber = getOperand(cn.name, operandNode);

            if (debug) {
                System.out.println("\tAdded labelNumber: " + labelNumber);
            }


            if (labelRefs.containsKey(labelNumber)) {
                throw new DuplicateLabel("Class " + cn.name + " has duplicate "
                        + "line numbers labeled: " + labelNumber);
            }


            AbstractInsnNode loadANode = operandNode.getPrevious(); // we need to back up one more to before the push instruction

            LabelNode labelNode = new LabelNode();
            labelRefs.put(labelNumber, labelNode);

            if (debug) {
                System.out.println("NODE BEFORE LABEL INSERT: " + loadANode + "," + loadANode.getType() + "," + loadANode.getOpcode());
            }
            mn.instructions.insert(loadANode.getPrevious(), labelNode);
            // mn.instructions.insertBefore(labelNode,new JumpInsnNode(167,labelNode));
        }
    }

    private static void extractJumpData(final List<AbstractInsnNode> labels, final ClassNode cn, final List<AbstractInsnNode> jumps, final MethodNode mn) {

        final String workingClassName = cn.name.replace('.', '/');

        InsnList ins = mn.instructions;

        if (debug) {
            System.out.println("Now trying to find multiJump...");
        }

        ListIterator iterator = mn.instructions.iterator();

        while (iterator.hasNext()) {
            AbstractInsnNode WHILE_NODE = (AbstractInsnNode) iterator.next();

            //if(debug) System.out.println("Look - Node: " + WHILE_NODE);

            if (WHILE_NODE.getType() == WHILE_NODE.METHOD_INSN) {
                MethodInsnNode min = (MethodInsnNode) WHILE_NODE;

                if (debug) {
                    System.out.println("Desc: " + min.desc);
                }

                if (min.owner.equals(workingClassName)) {
                    if (debug) {
                        System.out.println(" > owner: " + min.owner + ". name: " + min.name + " ");
                    }
                    if (min.name.equals("multiJump")) {
                        // wow!
                        MultiJump.makeMultiJump(jumps, min, workingClassName, ins, WHILE_NODE);
                    }
                }
            }
        }

        if (debug) {
            System.out.println("Now trying to find jump and label...");
        }
        iterator = mn.instructions.iterator();

        findLoop:
        while (iterator.hasNext()) {


            AbstractInsnNode WHILE_NODE = (AbstractInsnNode) iterator.next();

            // if(debug) System.out.println("Look - Node: " + WHILE_NODE);


            if (WHILE_NODE.getType() == WHILE_NODE.METHOD_INSN) {

                MethodInsnNode min = (MethodInsnNode) WHILE_NODE;

                if (min.owner.equals(workingClassName)) {
                    if (debug) {
                        System.out.println("Desc: " + min.desc
                                + " > owner: " + min.owner + ". name: " + min.name + " ");
                    }

                    if (min.name.equals(MARK_JUMP)) {

                        jumps.add(min);

                        AbstractInsnNode pNode = (AbstractInsnNode) min.getPrevious();
                        int labelNumber = getOperand(cn.name, pNode);
                        if (debug) {
                            System.out.println("******* FOUND JUMP : " + labelNumber);
                        }
                        qualifyLabelNumber(labelNumber, cn.name);

                    } else if (min.name.equals(MARK_LABEL)) {

                        labels.add(min);
                        AbstractInsnNode pNode = (AbstractInsnNode) min.getPrevious();

                        // if(debug)System.out.println("\tpNode: " + pNode );
                        // if(debug)System.out.println("\tpNode Opcode: " + pNode.getOpcode());

                        int labelNumber = getOperand(cn.name, pNode);
                        qualifyLabelNumber(labelNumber, cn.name);
                        if (debug) {
                            System.out.println("******* FOUND LABEL: " + labelNumber);
                        }

                    }
                }
            }

        }
    }
}

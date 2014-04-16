/*
 * Visitor.java
 *
 * Created on June 27, 2007, 5:06 PM
 * Copyright footloosejava.
 * Email: footloosejava@gmail.com
 */

package com.javagoto.gotos.transformers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import com.javagoto.gotos.Goto;
import com.javagoto.gotos.testing.NewClass;
import com.javagoto.gotos.interfaces.GotoTransformed;

/**
 *
 * @author footloosejava
 */
public final class Visitor implements ClassVisitor {
    
    public static boolean debug = false;
    
    private static final Map<String,Boolean> classesEnabled = new HashMap<String,Boolean>();
    private static final Map<String,Boolean> classesTransformed = new HashMap<String,Boolean>();
    
    private String name = "";
    private boolean gotoEnabled = false;
    private boolean gotoTransformed = false;
    
    private boolean hasArtifactsInCode = false;
    
    // the marker that a class has gotos
    public final String GOTO_MARKER = Goto.class.getName().replace('.','/');
    
    public final String OBJECT_MARKER = Object.class.getName().replace('.','/');
    
    // the marker that a class has already been transformed
    public final String GOTO_TRANSFORMED_MARKER = GotoTransformed.class.getName().replace('.','/');
    
    private final List<String> interfacesList = new ArrayList<String>();
    
    
    class VisitEscape extends RuntimeException{
    };
    
    private final VisitEscape ve = new VisitEscape();
    
    
    // so this should make other code cleaner
    public void enter(ClassReader cr){
        try {
            cr.accept(this, cr.SKIP_DEBUG);
        } catch (VisitEscape ve){
            // do nothing! the throw was just to catch escape exception
        }
    }
    
    public void visit(int version, int access, String name, String signature,
            String superName, String[] interfaces) throws VisitEscape{
        
        
        if(debug){
            System.out.println(this + " Name: " + name);
            System.out.println(this + " SuperName: " + superName);
            System.out.println(this + " Interfaces: " + Arrays.toString(interfaces));
        }
        
        // reset all fields
        reset();
        
        // set name we parsed
        this.name = name;
        
        checkIfEnabled(name, superName);
        checkIfTransformed(interfaces);
        
        setEnabled(name,gotoEnabled);
        setTransformed(name,gotoTransformed);
        
        if(debug) System.out.println(this + " Escaping Parse!");
        throw ve;
    }
    
    /** Do any interfaces have our marking?
     */
    private void checkIfTransformed(final String[] interfaces) {
        
        if( interfaces!=null){
            for(String s : interfaces){
                if(s.equals(GOTO_TRANSFORMED_MARKER)){
                    gotoTransformed = true;
                    return;
                }
            }
        }
        
        gotoTransformed = false;
        return;
    }
    
    
    private void checkIfEnabled(final String name, final String superName) throws RuntimeException {
        
        if( name.equals(GOTO_MARKER) || name.equals(GOTO_TRANSFORMED_MARKER)){
            
            gotoEnabled = true;
            
        }else if( superName!=null){
            
            if( superName.equals(OBJECT_MARKER)){
                gotoEnabled = false;
                
            }else if( superName.equals(GOTO_MARKER)){
                gotoEnabled = true;
                
            }else if( !classesEnabled.containsKey(superName)){
                try {
                    // we need to check the supernames supername!
                    Visitor vs = new Visitor();
                    ClassReader cr = new ClassReader(superName);
                    vs.enter(cr);
                    gotoEnabled = vs.isGotoEnabled();
                } catch (IOException ex) {
                    System.out.println("Crashed while visiting super class!");
                    throw new RuntimeException(ex);
                }
                
            }else if(superName!=null){
                gotoEnabled = classesEnabled.get(superName);
                
            }
        }
        
        if( GotoLoader.isSunJavaLanguageClass(name)){
            throw new RuntimeException("The class name cannot appear to be java. " +
                    "or javax. or sun. as these will not be loadable by " +
                    "goto transformer! > " + name);
        }
    }
    
    
    
    public void visitSource(String source, String debug){
    }
    
    public void visitOuterClass(String owner, String name, String desc){
    }
    
    public AnnotationVisitor visitAnnotation(String desc, boolean visible){
        return null;
    }
    
    public void visitAttribute(Attribute attr){
    }
    
    public void visitInnerClass(String name, String outerName, String innerName,
            int access){
    }
    
    public FieldVisitor visitField(int access, String name, String desc,
            String signature, Object value){
        return null;
    }
    
    public MethodVisitor visitMethod(int access, String name, String desc,
            String signature, String[] exceptions){
        return null;
    }
    
    public void visitEnd(){
        
    }
    
    public void reset(){
        interfacesList.clear();
        gotoEnabled = false;
        gotoTransformed = false;
        name = "";
    }
    
    public boolean isGotoEnabled() {
        return gotoEnabled;
    }
    
    /** True if both gotoEnabled and GotoTransformed
     */
    public boolean isGotoTransformed() {
        return gotoEnabled && gotoTransformed;
    }
    
    class GotoTransformException extends Exception{
        GotoTransformException(String message){
            super(message);
        }
    }
    
////// STATIC ////////
    
    private static synchronized void setTransformed(String name, boolean flag){
        classesTransformed.put(name,flag);
    }
    
    private static synchronized void setEnabled(String name, boolean flag){
        classesEnabled.put(name,flag);
    }
    
    public static synchronized Boolean isTransformed(String name){
        return classesTransformed.get(name);
    }
    
    public static synchronized Boolean isEnabled(String name){
        return classesEnabled.get(name);
    }
    
//////////////
    
    private static final Visitor v = new Visitor();
    
    public static void main(String[] args) throws IOException{
        ClassReader cr = new ClassReader(NewClass.class.getName());
        Visitor visitor = new Visitor();
        visitor.enter(cr);
        
        boolean ready = visitor.isGotoEnabled() && !visitor.isGotoTransformed();
        System.out.println("Is ready to be transformed: " + ready);
    }
    
    public String getName() {
        return name;
    }
    
}
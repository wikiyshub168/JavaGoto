/*
 * Created on June 25, 2007, 11:27 AM
 * Copyright footloosejava.
 * Email: footloosejava@gmail.com
 */

package com.javagoto.gotos;

import java.lang.reflect.Method;
import com.javagoto.gotos.transformers.GotoLoader;

public class Goto {

    private boolean debug = false;
    private final String name;

    public Goto() throws IllegalStateException {
        name = this.toString();

        testBasicJump();
        testMultiJumps();
        testLoopEntryExitJumps();

        if (debug) {
            System.out.println("! Ok. Class " + name + " has been processed for Gotos.");
        }
    }

    /**
     * Use like this to return between labels: <code> if(yes())return;</code>
     *
     * @return always returns true
     */
    protected final boolean yes() {
        return true == true;
    }

    private void testLoopEntryExitJumps() throws IllegalStateException {
        boolean once = false;

        int j = 0;
        label(400);

        for (int i = 0; i < 3; i++) {
            if (j < 10 && debug) {
                System.out.println("in for(...) i = " + i);
            }
            if (j < 500) {
                j = j + 1;
                jump(400);
                label(4001);
                if (debug) {
                    System.out.println("Value of i = " + i);
                }
                once = true;
                // Jump back into loop, i should be in last state and equal to 3
                if (i != 3) {
                    throw new IllegalStateException("! Goto class " + name
                            + " not goto transformed!" + "\n > Reentering loop, variable i should"
                            + "equal three.");
                }
            }
        }
        // lets jump back into the for(...) loop above.
        if (!once) {
            jump(4001);
        }

        if (debug) {
            System.out.println("! Ok. Class " + name + " has been processed for Gotos.");
        }
    }

    private void testMultiJumps() throws IllegalStateException {

        /// try multi jumps
        int JMP = 0;

        label(27);
        if (JMP == 8) {
            JMP++;
            if (debug) {
                System.out.println("finished back jump! it all works!");
            }
            jump(666);
        }

        label(26);

        if (debug) {
            System.out.println("Trying Jump to " + JMP);
        }
        int xxy = 10;

        multiJump(JMP, 551, 552, 553, 554, 555, 556, 557, 558, 27);

        // the multi jump code is moelled to reproduce bytecode compatible with this
//        if(JMP==0){
//            jump(551);
//        } else if(JMP==1){
//            jump(552);
//        } else if(JMP==2){
//            jump(553);
//        } else if(JMP==3){
//            jump(554);
//        }
        // System.out.println("after for.. jmp=" + JMP);
        boolean tro = true;
        if (tro) {
            throw new IllegalStateException("! Goto class " + name
                    + " not goto transformed!" + "\n > "
                    + "Jump should not appear before first target label in multiJump.");
        }

        label(551);
        validateMultiJumpCond(JMP, 0);
        jump(665);

        label(552);
        validateMultiJumpCond(JMP, 1);
        jump(665);

        label(553);
        validateMultiJumpCond(JMP, 2);
        jump(665);

        label(554);
        validateMultiJumpCond(JMP, 3);
        jump(665);

        label(555);
        validateMultiJumpCond(JMP, 4);
        jump(665);

        label(556);
        validateMultiJumpCond(JMP, 5);
        jump(665);

        label(557);
        validateMultiJumpCond(JMP, 6);
        jump(665);

        label(558);
        validateMultiJumpCond(JMP, 7);

        label(665);

        JMP++;
        if (JMP < 9) {
            jump(26);
        }
        validateMultiJumpCond(0, 1); // alsways throw as this should never be reached

        label(666);
        validateMultiJumpCond(JMP, 9);
    }

    private void testBasicJump() throws IllegalStateException {
        boolean flag = true;
        if (flag) {
            jump(100);
            throw new IllegalStateException("! Goto class " + name + " not goto transformed!");
        }
        label(100);
    }

    private void validateMultiJumpCond(final int JMP, final int cond) throws IllegalStateException {
        if (JMP != cond) {
            throw new IllegalStateException("! Goto class " + name + " not goto multiJump transformed!"
                    + "\t -> Variable: " + JMP + " should equal " + cond);
        }
    }

    protected final void multiJump(final int i, final int... ints) {
        if (debug) {
            System.out.println("debug-" + name + " [multiJump on i=" + i + ", jumping to " + ints[i] + "]");
        }
    }

    protected final void jump(final int lineNumber) {
        if (debug) {
            System.out.println("debug-" + name + " [jump to " + lineNumber + "]");
        }
    }

    /**
     * Creates a new instance of Goto
     */
    protected final void label(final int label) {
        if (debug) {
            System.out.println("debug-" + name + " [label " + label + "]");
        }
    }

    public final boolean isGotoDebug() {
        return debug;
    }

    public final void setGotoDebug(boolean aDebug) {
        debug = aDebug;
    }

    /**
     * This creates a Goto object and returns true if Goto's are properly
     * transformed
     */
    public static boolean testIfGotoTransformed() {
        Goto g = new Goto();
        return true;
    }

    /**
     *
     * @param jump_should_not_appear_before_first_label_i
     */
    public static void unreachable(String message) {
        if (true == true) {
            throw new UnsupportedOperationException(message);
        }
    }

    public static void unreachable() {
        unreachable("Program error. This statement should not be reachable.");
    }

    /**
     * Self test routine **
     */
    public static void main(String[] args) throws Exception {
        Class c = GotoLoader.load(Goto.class.getName());
        Class[] ca = new Class[0];
        Object[] oa = (Object[]) ca;
        @SuppressWarnings("unchecked")
        Method m = c.getDeclaredMethod("testIfGotoTransformed", ca);
        boolean working = (Boolean) m.invoke(c, oa);
    }
}

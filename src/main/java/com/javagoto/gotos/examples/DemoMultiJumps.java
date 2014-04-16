/*
 * DemoMultiJumps.java
 *
 * Created on June 25, 2007, 11:27 AM
 * Copyright footloosejava.
 * Email: footloosejava@gmail.com
 */
package com.javagoto.gotos.examples;

import com.javagoto.gotos.Goto;

public class DemoMultiJumps extends Goto {

    /**
     * This shows how to do jumps based on an index variable.
     *
     * JMP is the index variable here and must be the first argument in the
     * multi jump.
     */
    public void run() {

        int JMP = 0;

        label(27);

        // we will jump back and exit here
        if (JMP == 8) {
            return;
        }

        System.out.println("Trying Jump to " + JMP);

        // this will jump to the label indicated by the index of JMP variable
        // labels can be in any order, but thats the order they will be addressed as
        multiJump(JMP, 551, 552, 553, 554, 555, 556, 557, 558, 27);


        // since goto does not fit into existing language, code sections may not be reachable
        // and therefore need to add 'if(flag) throw ...' tor call the convenient 'unreachable' method 
        // to not get compiler error that the remaining code is unreachable
        unreachable("Jump should not appear before first label in multiJump.");

        label(551);
        label(552);
        label(553);
        label(554);
        label(555);
        label(556);
        label(557);
        label(558);

        JMP++;
        if (JMP < 9) {
            jump(27);
        }

        System.out.println("All done!  Stopping");
    }
}
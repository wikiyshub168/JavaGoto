/*
 * Created on June 25, 2007, 11:27 AM
 * Copyright footloosejava.
 * Email: footloosejava@gmail.com
 */

package com.javagoto.gotos.examples;

import com.javagoto.gotos.Goto;

public class DemoSimpleForwardBackwardJumps extends Goto {

    /**
     * This shows simple forward and backward jumps.
     * 
     * Labels can be in any order and are any integer number >= ZERO.
     */
    public void run() {

        setGotoDebug(true);

        int x = 0;
        label(0);

        System.out.println("X=" + x);

        if (x > 0) {
            System.out.println("we are now jumped back!");
            jump(200);
        }

        label(1);

        jump(100);

        x++;

        // this should never be reached
        System.out.println("Should not see this line!!  After Goto 100 command. X=" + x);

        label(100);

        x++;
        System.out.println("Here after label 100.  X = " + x);

        if (x == 1) {
            System.out.println("It worked!  We passed over the stuff between jump and label 100!");
        } else {
            unreachable("Goto not functioning!");
        }

        // lets go back to start at label 0
        jump(0);

        // this should never be reached
        System.out.println("After Goto 0 command");

        label(200);
    }

}
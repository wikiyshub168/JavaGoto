/*
 * Created on June 25, 2007, 11:27 AM
 * Copyright footloosejava.
 * Email: footloosejava@gmail.com
 */
package com.javagoto.gotos.testing;

import com.javagoto.gotos.Goto;

public class Test extends Goto implements Runnable {

    public void run() {

        setGotoDebug(true);

        int x = 0;


        label(0);


        System.out.println("X=" + x);

        if (x > 0) {
            System.out.println("we are now jumped back!");
            jump(200);
        }

        int JJ = 2563;


        label(1);
        label(2563);
        label(6);

        jump(100);

        x++;
        unreachable("Should not see this line!!  After Goto 100 command. X=" + x);

        label(100);

        x++;
        System.out.println("Here after line 100");

        System.out.println("X=" + x);
        if (x == 1) {
            System.out.println("It worked!  We passed over the stuff between jump and label 100!");
        } else {
            unreachable("Goto not functioning!");
        }

        jump(0);

        // this should never be reached
        System.out.println("After Goto 0 command");

        label(200);
        System.out.println("Now lets try some loops...");

        int j = 0;

        label(300);
        j = j + 1;
        System.out.println("\tLoop " + j);
        if (j < 4) {
            jump(300);
        }

        System.out.println("Now calling run2()");
        try {
            run2();
        } catch (RuntimeException ex) {
            System.out.println("Do nothing!");
        }


        System.out.println("Looping out of for()");

        setGotoDebug(false);

        boolean once = false;

        // jump in and out of loop!
        j = 0;
        label(400);
        for (int i = 0; i < 3; i++) {
            if (j < 10) {
                System.out.println("in for(...) i = " + i);
            }
            if (j < 30000) {
                j = j + 1;
                jump(400);
                label(4001);
                System.out.println("Value of i = " + i);
                once = true;
            }
        }


        setGotoDebug(true);
        if (!once) {
            jump(4001);
        }

        int JMP = 0;

        label(27);
        if (JMP == 8) {
            System.out.println("finished back jump! it all works!");
            jump(666);
        }

        
        label(26);
        System.out.println("Trying Jump to " + JMP);
        int xxy = 10;

        // this will jump to the label indicated by the index of JMP variable
        // 0 will goto 551, 1 to 552 etc.
        multiJump(JMP, 551, 552, 553, 554, 555, 556, 557, 558, 27);

        unreachable("No jump in multijump lands here!");

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
            jump(26);
        }

        label(666);
        System.out.println("All done!  Stopping");
    }

    private void run2() {
        label(1);
        jump(100);
        System.out.println("After jump(100)");

        label(100);
        System.out.println("After 100");
        System.out.println("Finished run2()");
        throw new RuntimeException();
    }
}
/*
 * Created on June 25, 2007, 11:27 AM
 * Copyright footloosejava.
 * Email: footloosejava@gmail.com
 *
 * Just some sample code. not showing anything in particular
 * This shows how to do a goto class file transformation.
 */
package com.javagoto.gotos.examples;

import com.javagoto.gotos.Goto;
import com.javagoto.gotos.transformers.GotoLoader;

public class Example3 extends Goto implements Runnable {

    public void run() {
        int x = 0;
        label(0);

        if (x > 0) {
            System.out.println("We jumped back to label 0 before reaching this code!");
            jump(1);
        }


        x++;
        jump(0);

        unreachable();

        if (yes()) {

            label(1);
            System.out.println("We reached label 1");
            // do some code then return
            if (yes()) {
                return;
            }

            label(2);
            System.out.println("We reached label 2");
            // do some other code then return
            if (yes()) {
                return;
            }
            
        }
        unreachable();
    }

    public static void main(String args[]) throws Exception {
        Runnable demo = (Runnable) GotoLoader.newInstance(Example3.class.getName());
        demo.run();
    }
}
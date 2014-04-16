/*
 * Created on June 25, 2007, 11:27 AM
 * Copyright footloosejava.
 * Email: footloosejava@gmail.com
 *
 * This shows how to do a goto class file transformation.
 */
package com.javagoto.gotos.examples;

import com.javagoto.gotos.Goto;
import com.javagoto.gotos.interfaces.ProgramStarter;
import com.javagoto.gotos.transformers.GotoLoader;

public class Example2 extends Goto implements ProgramStarter {

    @Override
    public void start(String[] args) {
        DemoProject.main(args);
    }

    public static void main(String args[]) throws Exception {
        ProgramStarter starter = (ProgramStarter) GotoLoader.newInstance(Example2.class.getName());
        starter.start(args);
    }
}
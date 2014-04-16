JavaGoto
========

A library that supports the use of goto in Java programming with some advanced goto uses and full debugging support.

Thanks to some bytecode modification, which can be done to on-the-fly, you can use simple and advanced goto constructs in Java. (Not that you should ... [evil grin] ... but now you can!

Using JavaGoto, substitute these statements for traditional gotos use:
----------------------------------------------------------------------

* Gotos            -- Instead of **goto [number]** , use **jump(number);**.
* Labels           -- Instead of **label [number]**, use **label(number);**.

Labels and jumps must have set integer values, such as: label(12), jump(12), etc.

Advanced use:
-------------

* Computed gotos   -- Use **multijump(n,10,20,30 …)** where 'n' is the index that can change at runtime.

* Multiple returns -- the use of a label followed by an **if(yes()){return;}** will allow code to have many multiple returns after each label reached, (similar to goto:EOF after a DOS goto or a standard return after a fortran goto.



Four Easy Steps to Using Gotos in your Java program
===================================================

1. Make sure your class extends the Goto interface.

2. Use jump(n) instead of goto and label(n) as the target. Labels and jumps must have set integer values, such as: label(12), jump(12), etc.

3. Computed gotos use multijump(n,10,20,30 …), where n is the index of label and where n is a value you change at runtime.

4. Use GotoLoader.newInstance(class) or GotoLoader.load(classname)  to load the main class (or instance) or only the classes (and instances) you want gotos in.

Debuggers precisely follow the flow of all jumps and labels making flow analysis convenient and easy.

You are done!


DEMO CODE
=========

        import com.javagotos.gotos.Goto;
        import com.javagotos.gotos.transformers.GotoLoader;
        
        public class Example1 extends Goto implements Runnable {
        
            public void run() {
                int x = 0;
                label(0);
        
                if (x > 0) {
                    System.out.println("We jumped back to label 0 before reaching this code!");
                    jump(1);
                }
        
                x++;
                jump(0);
                label(1);
            }
        
            public static void main(String args[]) throws Exception  {
                Runnable demo = (Runnable) GotoLoader.newInstance(Example1.class.getName());
                demo.run();
            }
        }


For-loop and do-while loop behavior
===================================

When jumping in and out of loops, the last known state of the loop-initialized variables is retained. You are free to leave and re-renter other language structures at-will.

        // demo to see jumping into and out of loops
        System.out.println("Now lets try some loops...");

        int j = 0;
        boolean once = false;
        label(400);

        for (int i = 0; i < 3; i++) {
            if (j < 30000) {
                j = j + 1;
                jump(400);
                label(4001);
                once = true;
            }
        }

        if (!once) {
             // lets jump back into the for(...) loop above.
            jump(4001);
        }


Feature – Unreachable Assertion
========================================
If a label appears in a region of unreachable code, simply insert ‘unreachable()’ or ‘unreachable(reason)’ to assert that a code region should not be crossed (although it can be jumped across using the goto):

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
        
        
Feature – Multiple Return Points
=========================================

If a label appears in a region of code after a return, the previous return will not allow the compiler to process the code as the next block of code will be unreachable. To workaround this, use the semantic ‘ if(  yes()  ) return’

The yes() method is a protected final method of the Goto class and will efficiently compute the boolean return value at runtime. This allows for multiple labels whose scope would normally prevent the placement of returns after them:

            label(1);
            System.out.println("We reached label 1");
            // do some code then return
            if (yes()) {
                return;
            }

            label(2);
            System.out.println("We reached label 2");
            // do some code then return
            if (yes()) {
                return;
            }
            
Fail-Safe features of Goto.class
================================
All classes extending Goto will fail on instantiation in the event that they were not loaded through the GotoLoader.


Debugging On/Off Setting
========================
The debugging mode can be turned on and off for any instance using setGotoDebug(true). The debugging output clearly identifies the jumps and labels by class, object hashcode, and program flow:

            // will emit branching debugging code
            setGotoDebug(true);

            // debugging output will look like something this
            debug-com.javagoto.gotos.testing.Test@3a439b [multiJump on i=5, jumping to 556]
            debug-com.javagoto.gotos.testing.Test@3a439b [jump to 556]
            debug-com.javagoto.gotos.testing.Test@3a439b [label 556]
            debug-com.javagoto.gotos.testing.Test@3a439b [label 557]
            debug-com.javagoto.gotos.testing.Test@3a439b [label 558]
            debug-com.javagoto.gotos.testing.Test@3a439b [jump to 26]
            debug-com.javagoto.gotos.testing.Test@3a439b [label 26]


Gotchas
=======

1) Load the project main class, or a root class that all ther classes with Gotos share a classloader with, via GotoLoader.load(class) or GotoLoader.newInstance(class).

I suggest Runnable, Callable or the ProgramStarter interface provide in the library. All other child classes referenced used in the transformed main class can be use as is:


            public interface ProgramStarter {
            
                public void start(String[] args);
            }


            public class Example2 extends Goto implements ProgramStarter {
            
                @Override
                public void start(String[] args) {
                    // also make sure Myproject extends Goto class if it uses Gotos. 
                    MyProject.main(args);
                }
            
                public static void main(String args[]) throws Exception {
                    ProgramStarter starter = (ProgramStarter) GotoLoader.newInstance(Example2.class.getName());
                    starter.start(args);
                }
            }


2) Gotos and Labels work in instance methods. Your goto enabled classes can have static methods, just none that have goto’s or labels in them.

3) A goto cannot direct flow out of a block synchronized using java’s ‘synchronized’ keyword. The example below will result in an IllegalMonitorState exception. The solution is to use Java locks instead of ‘synchronized’.


            final Object mutex = new Object();
            
            synchronized (mutex){
                jump(1);
            }
            
            label(1);


4) A goto may be used to direct flow out of a ‘try-finally’ block. In this instance, the code in ‘finally’ will be skipped. However, all code normally exiting the ‘try’ will have it’s ‘finally’ clause run. This example will not invoke the code in a finally clause:

            try{
            
                jump(1);
            
            }finally{
                // this code will be skipped by jump in try
                // statement that is going to outside label 1
            }
            
            label(1);
            
            

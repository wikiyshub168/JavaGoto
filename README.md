JavaGoto
========

A library that supports the use of goto in Java programming with some advanced goto uses.

Thanks to some bytecode modification, which can be done on-the-fly wothout special compilation, you can use simple and advanced goto constructs in Java.

Not that you should! (evil grin) But now you can!

In JavaGoto, these statements are equivalent to traditional gotos use:

* Gotos            -- Instead of **goto [number]** , use **jump(number);**.
* Labels           -- Instead of **label [number]**, use **label(number);*.

Labels and jumps must have set integer values, such as: label(12), jump(12), etc.

Advanced use:
* Computed gotos   -- Use **multijump(n,10,20,30 …)** where 'n' is the index that can change at runtime.

* Multiple returns -- the use of a label followed by an 'if(yes()){return;}' will allow code to have many multiple returns after each label reached, (similar to goto:EOF after a DOS goto or a standard return after a fortran goto.



Four Easy Steps to Using Gotos in your Java program
----------------------------------------------------

1. Make sure your class extends the Goto interface.

2. Use jump(n) instead of goto and label(n) as the target. Labels and jumps must have set integer values, such as: label(12), jump(12), etc.

3. Computed gotos use multijump(n,10,20,30 …), where n is the index of label and where n is a value you change at runtime.

4. Use GotoLoader.newInstance(class) or GotoLoader.load(classname)  to load the main class (or instance) or only the classes (and instances) you want gotos in.

Debuggers precisely follow the flow of all jumps and labels making flow analysis convenient and easy.

You are done!

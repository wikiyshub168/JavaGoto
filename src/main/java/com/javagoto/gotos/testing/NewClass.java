/*
 * NewClass.java
 *
 * Created on June 29, 2007, 5:20 PM
 * Copyright footloosejava.
 * Email: footloosejava@gmail.com
 */

package com.javagoto.gotos.testing;

import com.javagoto.gotos.Goto;

public class NewClass extends Goto{
    
    /** Creates a new instance of NewClass */
    public NewClass() {
        System.out.println("Created new " + NewClass.class.getName());
        // super(NewClass.class.getName());
        Test testImpl = new Test();
    }
}

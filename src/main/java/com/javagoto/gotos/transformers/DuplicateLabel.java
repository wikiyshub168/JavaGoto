/*
 * Created on June 25, 2007, 11:27 AM
 * Copyright footloosejava.
 * Email: footloosejava@gmail.com
 */

package com.javagoto.gotos.transformers;

/**
 *
 * @author footloosejava
 */
public class DuplicateLabel extends java.lang.Exception {
    
    /**
     * Creates a new instance of <code>DuplicateLabel</code> without detail message.
     */
    public DuplicateLabel() {
    }
    
    
    /**
     * Constructs an instance of <code>DuplicateLabel</code> with the specified detail message.
     * @param msg the detail message.
     */
    public DuplicateLabel(String msg) {
        super(msg);
    }
}

/*
 * Created on June 25, 2007, 11:27 AM
 * Copyright footloosejava.
 * Email: footloosejava@gmail.com
 *
 */

package com.javagoto.gotos.transformers;

/**
 *
 * @author footloosejava
 */
public class LabelNotFound extends java.lang.Exception {
    
    /**
     * Creates a new instance of <code>LabelNotFound</code> without detail message.
     */
    public LabelNotFound() {
    }
    
    
    /**
     * Constructs an instance of <code>LabelNotFound</code> with the specified detail message.
     * @param msg the detail message.
     */
    public LabelNotFound(String msg) {
        super(msg);
    }
}

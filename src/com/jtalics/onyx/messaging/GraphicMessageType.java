package com.jtalics.onyx.messaging;

import java.io.Serializable;


/**
 * The Workflow Message Types
 * 
 */
public enum GraphicMessageType implements Serializable {
    
	/** Tree Add */
	TREE_ADD,
	
	/** Tree Delete */
	TREE_DELETE,
	
    /** Tree Node Selection */
    TREE_NODE_SELECTION,

    /** INITIAL_DATA */
    ALL_DATA,
    
    /** Applet UPDATE */
    APPLET_UPDATE,
    
    /** Applet Node Selection */
    APPLET_NODE_SELECTION
}

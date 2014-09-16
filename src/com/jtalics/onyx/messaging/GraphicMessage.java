package com.jtalics.onyx.messaging;

import java.io.Serializable;

/**
 * Data needed for the Workflow Applet.
 *
 */
public class GraphicMessage implements Serializable {
    
    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
    
    /** The message type. */
    private GraphicMessageType messageType = null;
    
    /** The Zoom Factor */
    private String lastZoom = "1.0";
    
    /**
     * The Message Data
     */
    private Object data = null;
    
    /**
     * Instantiates a new workflow message.
     *
     * @param type the type
     */
    public GraphicMessage(GraphicMessageType type) {
        messageType = type;
    }
    
    /**
     * Gets the message type.
     *
     * @return the message type
     */
    public GraphicMessageType getMessageType() {
        return messageType;
    }

    /**
     * Get the Last Zoom
     * @return the last zoom
     */
    public String getLastZoom() {
        return lastZoom;
    }

    /**
     * Set the Last Zoom
     * @param lastZoom - the last zoom to set
     */
    public void setLastZoom(String zoom) {
        this.lastZoom = zoom;
    }

    /**
     * Get the Message Data
     */
	public Object getData() {
		return data;
	}

	/**
	 * Set the Message Data
	 * @param data
	 */
	public void setData(Object data) {
		this.data = data;
	}

}

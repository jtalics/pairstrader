package com.jtalics.onyx.visual.dnd;

import java.awt.Color;


/**
 * The Visual Drag N Drop Type
 */
public enum VisualDragNDropType {
    Import         ("  Import  ", new Color(0xffc0ff)),
    IPSubnet       ("IP Subnet ", new Color(0xc0c0ff)),
    Radio          ("  Radio   ", new Color(0x40ff40)),
    PresetTemplate ("  Preset  ", new Color(0xfbe9dd)),
    VoiceCallBook  ("   VCB    ", new Color(0xe5ead4)),
    IPAddress      ("IP Address", new Color(0xafd2f3)),
    Group          ("  Group   ", new Color(0xf7cb77)),
    Icon           ("  Icon    ", new Color(0xf88081)),
    Export         ("  Export  ", new Color(0x80ffff)),
    Trash          ("  Trash   ", new Color(0xffc0ff));
    
    // The String value of the enum
    private String value;
    // The Color for this DND Feature
    private Color color;
    
    // The private constructor
    private VisualDragNDropType(final String value, Color color) {
        // Set the String value
        this.value = value;
        if(color != null) {
            // Set the color
            this.color = color;
        } else {
            ; // leave it as it was
        }
    }
   
    /**
     * Returns the String value of the enum.
     * 
     * @return the String value of the enum
     */
    public String stringValue() {
        return value;
    }

    /**
     * Returns the color of the enum.
     * 
     * @return the color of the enum
     */
    public Color getColor() {
        return color;
    }
    
    /**
     * Returns the enum constant corresponding to the String value.
     * 
     * @param value
     *            the String value to match
     * @return the enum constant
     */
    public static VisualDragNDropType getEnumConstant(String value) {
        // the return type
        VisualDragNDropType retVal = null;
        // get the array of types
        VisualDragNDropType[] values = VisualDragNDropType.values();
        // loop for each type
        for (VisualDragNDropType val : values) {
            // continue if we find a match
            if (val.stringValue().equals(value)) {
                // found a match - end loop
                retVal = val;
                break;
            } else {
                ; // The value did not match
            }
        }
        return retVal;
    }
    
}

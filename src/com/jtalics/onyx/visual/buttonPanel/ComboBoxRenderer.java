package com.jtalics.onyx.visual.buttonPanel;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * The Class ComboBoxRenderer.
 */
public class ComboBoxRenderer extends GlassLabel implements ListCellRenderer {

    /** Default Serial Version UID. */
    private static final long serialVersionUID = 1L;

    /**
     * Instantiates a new combo box renderer.
     */
    public ComboBoxRenderer() {
        setOpaque(false);
        setHorizontalAlignment(CENTER);
        setVerticalAlignment(CENTER);
    }

    /*
    * This method finds the image and text corresponding
    * to the selected value and returns the label, set up
    * to display the text and image.
    */
    /* (non-Javadoc)
     * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
     */
    public Component getListCellRendererComponent(
                    JList list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {
        if(isSelected) {
            setColorTheme(Theme.GLASS_LIGHTBLUE_THEME);
        } else {
            setColorTheme(Theme.DEFAULT_THEME);
        }
        setText(value.toString());
        return this;
    }
}

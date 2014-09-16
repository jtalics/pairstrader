package com.jtalics.onyx.visual.buttonPanel;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import javax.swing.JPanel;

/**
 * The Class GlassPanel.
 */
public class GradientPanel extends JPanel {
    
    /** Default Serial Version UID. */
    private static final long serialVersionUID = 1L;
    
    /** The color theme. */
    private int colorTheme = Theme.GRADIENT_SKYBLUE_THEME;
    
    /** The shape type. */
    private String shapeType = ShapeType.RECTANGULAR;
    
    /** The color. */
    private GradientPaint color = null;

    /* (non-Javadoc)
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        int h = getHeight();
        int w = getWidth();
        drawShape(g2d, w, h);
        g2d.dispose();
    }

    /**
     * Draws the shape.
     * 
     * @param g2d
     *            2D Graphics object.
     * @param w
     *            width of the button
     * @param h
     *            height of the Button
     */
    private void drawShape(Graphics2D g2d, int w, int h) {
        color = ColorUtils.getInStance().getGradientColor(this.colorTheme, getHeight(), null);
        if (shapeType.equals(ShapeType.ROUNDED_RECTANGULAR)) {

            g2d.setPaint(color);
            g2d.fillRoundRect(0, 0, w, h, 10, 10);
            g2d.setPaint(new Color(100, 100, 100, 100));
            g2d.drawRoundRect(0, 0, w - 1, h - 1, 10, 10);
            g2d.setPaint(new Color(255, 255, 255, 50));
            g2d.drawRoundRect(1, 1, w - 3, h - 3, 10, 10);
        } else if (shapeType.equals(ShapeType.RECTANGULAR)) {
            g2d.setPaint(color);
            g2d.fillRect(1, 1, w - 2, h - 2);
            g2d.setPaint(new Color(100, 100, 100, 100));
            g2d.drawRect(0, 0, w - 1, h - 1);
        } else if (shapeType.equals(ShapeType.OVAL)) {
            g2d.setPaint(color);
            g2d.fillOval(1, 1, w - 20, h - 2);
            g2d.setPaint(new Color(100, 100, 100, 100));
            g2d.drawOval(0, 0, w - 20, h - 1);
        } else if (shapeType.equals(ShapeType.ELLIPSE)) {
            g2d.setPaint(color);
            Shape shape = new Ellipse2D.Double(1, 1, w - 2, h - 2);
            g2d.fill(shape);
            g2d.setPaint(new Color(100, 100, 100, 100));
            shape = new Ellipse2D.Double(0, 0, w - 1, h - 1);
            g2d.draw(shape);
        } else if (shapeType.equals(ShapeType.CIRCULAR)) {
            int size = Math.min(getWidth(), getHeight() - 2);
            g2d.setPaint(color);
            g2d.fillOval(2, 2, (size - 2 * 2), (size - 2 * 2));
            g2d.setStroke(new BasicStroke(2));
            g2d.setColor(new Color(100, 100, 100, 100));
            g2d.drawOval(2, 2, (size - 2 * 2), (size - 2 * 2));
        } else if (shapeType.equals(ShapeType.ROUNDED)) {
            g2d.setPaint(color);
            g2d.fillRoundRect(1, 1, w - 2, h - 2, h - 5, h - 5);
            g2d.setPaint(new Color(100, 100, 100, 100));
            g2d.drawRoundRect(0, 0, w - 1, h - 1, h - 3, h - 3);
        } else {
            ; // nothing to do
        }
    }

    /**
     * Returns the Selected Theme.
     * 
     * @return selected theme
     */
    public int getSelectedTheme() {
        return colorTheme;
    }

    /**
     * Sets the selected theme.
     * 
     * @param selectedTheme
     *            theme when the button is selected
     */
    public void setSelectedTheme(int selectedTheme) {
        this.colorTheme = selectedTheme;
    }
}

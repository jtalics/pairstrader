package com.jtalics.onyx.visual.buttonPanel;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JLabel;

/**
 * The Class GlassLabel.
 */
public class GlassLabel extends JLabel {

	/** Default Serial Version UID. */
    private static final long serialVersionUID = 1L;
    
    /** The color theme. */
    private int colorTheme = Theme.DEFAULT_THEME; // Default Glossy Blue theme
	
	/** The shape type. */
	private String shapeType = ShapeType.RECTANGULAR;
	
	/** The glossy colors. */
	private GradientPaint[] glossyColors = new GradientPaint[2];
	
	/** The glossy bg color. */
	private GradientPaint glossyBgColor;
	
	/** The glossy fg color. */
	private GradientPaint glossyFgColor;
	
	/**
     * Instantiates a new glass label.
     */
	public GlassLabel() {
	}

	/**
     * Instantiates a new glass label.
     * 
     * @param text
     *            the text
     */
	public GlassLabel(String text) {
	    super(text);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		int h = getHeight();
		int w = getWidth();
		int height = getHeight();

		glossyColors = ColorUtils.getInStance().getGlassColor(colorTheme,height, this);
		glossyBgColor = glossyColors[1];
		glossyFgColor = glossyColors[0];
		drawShape(g2d, w, h);
		g2d.dispose();
		super.paintComponent(g);

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
        if (getShapeType().equals(ShapeType.ROUNDED_RECTANGULAR)) {
            RoundRectangle2D.Float r2d = new RoundRectangle2D.Float(0, 0,
                    w - 1, h - 1, 8, 8);
            Shape clip = g2d.getClip();
            g2d.clip(r2d);
            g2d.setPaint(glossyBgColor);
            g2d.fillRoundRect(0, 0, w, h, 8, 8);
            g2d.setClip(clip);
            g2d.setPaint(glossyFgColor);
            g2d.fillRoundRect(2, 2, w - 4, h / 2, 5, 5);

            g2d.setColor(new Color(50, 50, 50, 200));
            g2d.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);
            g2d.setColor(new Color(255, 255, 255, 100));
            g2d.drawRoundRect(1, 1, w - 3, h - 3, 8, 8);
        } else if (getShapeType().equals(ShapeType.RECTANGULAR)) {

            g2d.setPaint(glossyColors[1]);
            g2d.fillRect(0, 0, w, h);

            g2d.setPaint(glossyColors[0]);
            g2d.fillRect(2, 2, w - 4, h / 2);

            g2d.setColor(new Color(0, 0, 0, 100));
            g2d.drawRect(0, 0, w - 1, h - 1);
            g2d.setColor(new Color(255, 255, 255, 100));
            g2d.drawRect(1, 1, w - 3, h - 3);

        } else if (getShapeType().equals(ShapeType.ROUNDED)) {

            RoundRectangle2D.Float r2d = new RoundRectangle2D.Float(0, 0,
                    w - 1, h - 1, 8, 8);
            Shape clip = g2d.getClip();
            g2d.clip(r2d);

            g2d.setPaint(glossyBgColor);
            g2d.fillRoundRect(0, 0, w, h, h - 3, h - 3);
            g2d.setClip(clip);

            g2d.setPaint(glossyFgColor);
            g2d.fillRoundRect(2, 2, w - 4, h / 2, h - 5, h - 5);

            g2d.setColor(new Color(100, 100, 100));
            g2d.drawRoundRect(0, 0, w - 1, h - 1, h - 3, h - 3);
            g2d.setColor(new Color(255, 255, 255, 100));
            g2d.drawRoundRect(1, 1, w - 3, h - 3, h - 3, h - 3);

        } else {
            ; // nothing to do
        }
	}

	/**
	 * Returns color Theme.
	 * 
	 * @return color theme
	 */
	public int getColorTheme() {
		return colorTheme;
	}

	/**
     * Sets color theme.
     * 
     * @param colorTheme
     *            the new color theme
     */
	public void setColorTheme(int colorTheme) {
		this.colorTheme = colorTheme;
	}

    /**
     * @return the shapeType
     */
    public String getShapeType() {
        return shapeType;
    }

    /**
     * @param shapeType the shapeType to set
     */
    public void setShapeType(String shapeType) {
        this.shapeType = shapeType;
    }

}

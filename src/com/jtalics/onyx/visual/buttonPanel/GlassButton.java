package com.jtalics.onyx.visual.buttonPanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;

import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * The Class GlassButton.
 */
public class GlassButton extends JButton {

	/** Default Serial Version UID. */
    private static final long serialVersionUID = 1L;
    
    /** The color theme. */
    private int colorTheme = Theme.GRADIENT_BLACK_THEME;
	
	/** The roll over color theme. */
	private int rollOverColorTheme = Theme.GLASS_SILVER_THEME;
	
	/** The selected color theme. */
	private int selectedColorTheme = Theme.GLASS_RED_THEME;;
	
	/** The shape type. */
	private String shapeType = ShapeType.ROUNDED_RECTANGULAR;
	
	/** The glass colors. */
	private GradientPaint[] glassColors = new GradientPaint[2];
	
	/** The glass bg color. */
	private GradientPaint glassBgColor;
	
	/** The glass fg color. */
	private GradientPaint glassFgColor;

	/**
	 * Constructor which sets label of the button.
	 * 
	 * @param text
	 *            label on the button
	 */
	public GlassButton(String text) {
		super(text);
		init();
	}

    /**
     * Constructor which sets label of the button.
     * 
     * @param icon
     *            the icon
     */
    public GlassButton(Icon icon) {
        super(icon);
        init();
    }
	
	/**
	 * Constructor which sets the label and theme for the button.
	 * 
	 * @param text
	 *            label on the button
	 * @param buttonTheme
	 *            button theme.
	 */
	public GlassButton(String text, int buttonTheme) {
		super(text);
		this.colorTheme = buttonTheme;
		init();
	}

	/**
	 * Constructor which sets the label and type for the button.
	 * 
	 * @param text
	 *            label on the button
	 * @param shapeType
	 *            shape of the button
	 */
	public GlassButton(String text, String shapeType) {
		super(text);
		this.shapeType = shapeType;
		init();
	}

	/**
	 * Constructor which sets the label,type and theme for the button.
	 * 
	 * @param text
	 *            label on the button
	 * @param buttonTheme
	 *            theme of the button
	 * @param shapeType
	 *            shape of the button
	 */
	public GlassButton(String text, int buttonTheme, String shapeType) {
		super(text);
		this.colorTheme = buttonTheme;
		this.shapeType = shapeType;
		init();
	}

	/**
	 * Constructor which sets the label,type,theme and roll-over theme for the
	 * button.
	 * 
	 * @param text
	 *            label on the button
	 * @param shapeType
	 *            shape of the button
	 * @param buttonTheme
	 *            theme of the button
	 * @param rolloverTheme
	 *            roll-over theme
	 */
	public GlassButton(String text, String shapeType, int buttonTheme,
			int rolloverTheme) {
		super(text);
		this.shapeType = shapeType;
		this.colorTheme = buttonTheme;
		this.rollOverColorTheme = rolloverTheme;
		init();
	}

	/**
	 * Constructor which sets the label,type,theme ,roll-over and selected theme
	 * for the button.
	 * 
	 * @param text
	 *            label on the button
	 * @param shapeType
	 *            shape of the button
	 * @param buttonTheme
	 *            theme of the button
	 * @param rolloverTheme
	 *            roll-over theme
	 * @param selectedTheme
	 *            selected theme
	 */
	public GlassButton(String text, String shapeType, int buttonTheme,
			int rolloverTheme, int selectedTheme) {
		super(text);
		this.colorTheme = buttonTheme;
		this.shapeType = shapeType;
		this.rollOverColorTheme = rolloverTheme;
		this.selectedColorTheme = selectedTheme;
		init();
	}

	/**
     * Initializes.
     */
	private void init() {
		setFont(new Font("Thoma", Font.BOLD, 12));
		setContentAreaFilled(false);
		setBorderPainted(false);
		setFocusPainted(false);
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

		ButtonModel model = getModel();
		if (model.isRollover()) {
			glassColors = ColorUtils.getInStance().getGlassColor(
					rollOverColorTheme, height, this);
		} else {
			glassColors = ColorUtils.getInStance().getGlassColor(colorTheme,
					height, this);

		}
		if (model.isSelected() || model.isPressed()) {
			glassColors = ColorUtils.getInStance().getGlassColor(
					selectedColorTheme, height, this);

		} else {
            ; // nothing to do
        }
		glassBgColor = glassColors[1];
		glassFgColor = glassColors[0];
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
		if (shapeType.equals(ShapeType.ROUNDED_RECTANGULAR)) {
			RoundRectangle2D.Float r2d = new RoundRectangle2D.Float(0, 0,
					w - 1, h - 1, 8, 8);
			Shape clip = g2d.getClip();
			g2d.clip(r2d);
			g2d.setPaint(glassBgColor);
			g2d.fillRoundRect(0, 0, w, h, 8, 8);
			g2d.setClip(clip);
			g2d.setPaint(glassFgColor);
			g2d.fillRoundRect(2, 2, w - 4, h / 2, 5, 5);

	        ButtonModel model = getModel();
	        if(model.isPressed()) {
                g2d.setColor(new Color(0, 0, 0, 255));
                g2d.drawRoundRect(0, 0, w-1, h-1, 8, 8);
                g2d.setColor(new Color(255, 255, 255, 160));
                g2d.drawRoundRect(1, 1, w - 3, h - 3, 8, 8);
			} else {
	            g2d.setColor(new Color(50, 50, 50, 200));
	            g2d.drawRoundRect(0, 0, w - 1, h - 1, 8, 8);
	            g2d.setColor(new Color(255, 255, 255, 100));
	            g2d.drawRoundRect(1, 1, w - 3, h - 3, 8, 8);
			}

		} else if (shapeType.equals(ShapeType.RECTANGULAR)) {

			g2d.setPaint(glassColors[1]);
			g2d.fillRect(0, 0, w, h);

			g2d.setPaint(glassColors[0]);
			g2d.fillRect(2, 2, w - 4, h / 2);

			g2d.setColor(new Color(0, 0, 0, 100));
			g2d.drawRect(0, 0, w - 1, h - 1);
			g2d.setColor(new Color(255, 255, 255, 100));
			g2d.drawRect(1, 1, w - 3, h - 3);

		} else if (shapeType.equals(ShapeType.ROUNDED)) {

			RoundRectangle2D.Float r2d = new RoundRectangle2D.Float(0, 0,
					w - 1, h - 1, 8, 8);
			Shape clip = g2d.getClip();
			g2d.clip(r2d);

			g2d.setPaint(glassBgColor);
			g2d.fillRoundRect(0, 0, w, h, h - 3, h - 3);
			g2d.setClip(clip);

			g2d.setPaint(glassFgColor);
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
	 * Returns Color Theme.
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
     *            the new button theme
     */
	public void setColorTheme(int colorTheme) {
		this.colorTheme = colorTheme;
	}

	/**
     * Returns roll-over Color theme.
     * 
     * @return roll over color theme.
     */
	public int getRollOverColorTheme() {
		return rollOverColorTheme;
	}

	/**
     * Sets the roll over color theme.
     * 
     * @param rollOverColorTheme
     *            the new roll over theme
     */
	public void setRollOverColorTheme(int rollOverColorTheme) {
		this.rollOverColorTheme = rollOverColorTheme;
	}

	/**
	 * Returns the selected Color theme.
	 * 
	 * @return selected Color Theme.
	 */
	public int getSelectedColorTheme() {
		return selectedColorTheme;
	}

	/**
     * Sets the selected Color theme.
     * 
     * @param selectedColorTheme
     *            the new selected color theme
     */
	public void setSelectedColorTheme(int selectedColorTheme) {
		this.selectedColorTheme = selectedColorTheme;
	}

}

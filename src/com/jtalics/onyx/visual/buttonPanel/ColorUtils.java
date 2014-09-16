package com.jtalics.onyx.visual.buttonPanel;

import java.awt.Color;
import java.awt.GradientPaint;

import javax.swing.JComponent;

/**
 * The Class ColorUtils.
 */
public final class ColorUtils {

	/** The color utils. */
	private static final ColorUtils COLORUTILS = new ColorUtils();
	
	/**
     * Gets the in stance.
     * 
     * @return the in stance
     */
	public static ColorUtils getInStance() {
		return COLORUTILS;
	}

	/**
     * Gets the standard color.
     * 
     * @param theme
     *            the theme
     * @param height
     *            the height
     * @param component
     *            the component
     * @return the standard color
     */
	public GradientPaint getStandardColor(int theme, int height, JComponent component) {
	    /** The color. */
	    GradientPaint standardColor;
		switch (theme) {
		case Theme.STANDARD_DARKGREEN_THEME:
			standardColor = new GradientPaint(0, 0, new Color(0, 140, 0), 0,
					(float)(3 * height / 4.0), new Color(0, 85, 0));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.STANDARD_BLUEGREEN_THEME:
			standardColor = new GradientPaint(0, 0, new Color(31, 175, 114), 0,
					height, new Color(20, 113, 74));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.STANDARD_GREEN_THEME:
			standardColor = new GradientPaint(0, 0, new Color(102, 223, 36), 0,
					height, new Color(68, 154, 23));
			setComponentForegroundColor(component, Color.WHITE);
			break;
		case Theme.STANDARD_LIGHTGREEN_THEME:
			standardColor = new GradientPaint(0, 0, new Color(121, 232, 98), 0,
					(float)(3 * height / 4.0), new Color(61, 208, 31));
			setComponentForegroundColor(component, Color.WHITE);
			break;
		case Theme.STANDARD_OLIVEGREEN_THEME:
			standardColor = new GradientPaint(0, 0, new Color(117, 198, 6), 0,
					height, new Color(68, 116, 4));
			setComponentForegroundColor(component, Color.WHITE);
			break;

		case Theme.STANDARD_LIME_THEME:
			standardColor = new GradientPaint(0, 0, new Color(181, 223, 38), 0,
					(float)(3 * height / 4.0), new Color(137, 170, 26));
			setComponentForegroundColor(component, Color.WHITE);
			break;
		case Theme.STANDARD_RED_THEME:
			standardColor = new GradientPaint(0, 0, new Color(255, 100, 100), 0,
					height, new Color(255, 0, 0));
			setComponentForegroundColor(component, Color.WHITE);
			break;
		case Theme.STANDARD_DARKRED_THEME:
			standardColor = new GradientPaint(0, 0, new Color(255, 0, 6), 0,
					height, new Color(181, 0, 0));
			setComponentForegroundColor(component, Color.WHITE);
			break;
		case Theme.STANDARD_ORANGE_THEME:
			standardColor = new GradientPaint(0, 0, new Color(251, 139, 62), 0,
					height, new Color(255, 102, 0));
			setComponentForegroundColor(component, Color.WHITE);
			break;
		case Theme.STANDARD_LIGHTORANGE_THEME:
			standardColor = new GradientPaint(0, 0, new Color(247, 174, 24), 0,
					height, new Color(255, 133, 0));
			setComponentForegroundColor(component, Color.WHITE);
			break;
		case Theme.STANDARD_DARKYELLOW_THEME:
			standardColor = new GradientPaint(0, 0, new Color(185, 181, 0), 0,
					height, new Color(123, 120, 0));
			setComponentForegroundColor(component, Color.WHITE);
			break;

		case Theme.STANDARD_GREENYELLOW_THEME:
			standardColor = new GradientPaint(0, 0, new Color(253, 247, 11), 0,
					height, new Color(211, 204, 2));
			setComponentForegroundColor(component, Color.BLACK);
			break;

		case Theme.STANDARD_GOLD_THEME:
			standardColor = new GradientPaint(0, 0, new Color(255, 233, 18), 0,
					height, new Color(255, 213, 0));
			setComponentForegroundColor(component, Color.BLACK);
			break;
		case Theme.STANDARD_YELLOW_THEME:
			standardColor = new GradientPaint(0, 0, new Color(255, 255, 166), 0,
					height, new Color(255, 255, 56));
			setComponentForegroundColor(component, Color.BLACK);
			break;

		case Theme.STANDARD_BROWN_THEME:
			standardColor = new GradientPaint(0, 0, new Color(202, 62, 2), 0,
					(float)(3 * height / 4.0), new Color(118, 35, 1));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.STANDARD_LIGHTBROWN_THEME:
			standardColor = new GradientPaint(0, 0, new Color(232, 194, 125), 0,
					(float)(3 * height / 4.0), new Color(212, 151, 37));
			setComponentForegroundColor(component, Color.WHITE);
			break;
		case Theme.STANDARD_PALEBROWN_THEME:
			standardColor = new GradientPaint(0, 0, new Color(248, 234, 203), 0,
					(float)(3 * height / 4.0), new Color(236, 205, 132));
			setComponentForegroundColor(component, Color.BLACK);
			break;
		case Theme.STANDARD_NAVYBLUE_THEME:
			standardColor = new GradientPaint(0, 0, new Color(44, 105, 180), 0,
					height, new Color(5, 25, 114));
			setComponentForegroundColor(component, Color.WHITE);
			break;

		case Theme.STANDARD_INDIGO_THEME:
			standardColor = new GradientPaint(0, 0, new Color(49, 120, 206), 0,
					height, new Color(35, 84, 146));
			setComponentForegroundColor(component, Color.WHITE);

			break;

		case Theme.STANDARD_BLUE_THEME:
			standardColor = new GradientPaint(0, 0, new Color(58, 92, 252), 0,
					height, new Color(3, 37, 188));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.STANDARD_SKYBLUE_THEME:
			standardColor = new GradientPaint(0, 0, new Color(17, 136, 255), 0,
					height, new Color(0, 96, 194));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.STANDARD_LIGHTBLUE_THEME:
			standardColor = new GradientPaint(0, 0, new Color(51, 191, 238), 0,
					height, new Color(17, 160, 208));

			setComponentForegroundColor(component, Color.WHITE);

			break;

		case Theme.STANDARD_DARKPURPLE_THEME:
			standardColor = new GradientPaint(0, 0, new Color(82, 0, 164), 0,
					height, new Color(44, 0, 89));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.STANDARD_PURPLE_THEME:
			standardColor = new GradientPaint(0, 0, new Color(203, 64, 239), 0,
					height, new Color(186, 0, 255));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.STANDARD_LAVENDER_THEME:
			standardColor = new GradientPaint(0, 0, new Color(165, 117, 239), 0,
					height, new Color(107, 60, 173));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.STANDARD_DARKPINK_THEME:
			standardColor = new GradientPaint(0, 0, new Color(170, 0, 128), 0,
					height, new Color(115, 0, 85));
			setComponentForegroundColor(component, Color.WHITE);

			break;

		case Theme.STANDARD_PINK_THEME:
			standardColor = new GradientPaint(0, 0, new Color(238, 83, 133), 0,
					height, new Color(220, 22, 86));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.STANDARD_PALEPINK_THEME:
			standardColor = new GradientPaint(0, 0, new Color(255, 174, 235), 0,
					height, new Color(255, 128, 223));
			setComponentForegroundColor(component, Color.WHITE);

			break;

		case Theme.STANDARD_BLACK_THEME:
			standardColor = new GradientPaint(0, 0, new Color(90, 90, 90), 0,
					height, new Color(0, 0, 0));
			setComponentForegroundColor(component, Color.WHITE);

			break;

		case Theme.STANDARD_GRAY_THEME:
			standardColor = new GradientPaint(0, 0, new Color(90, 90, 90), 0,
					height, new Color(70, 70, 70));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.STANDARD_LIGHTGRAY_THEME:
			standardColor = new GradientPaint(0, 0, new Color(163, 163, 163), 0,
					height, new Color(128, 128, 128));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.STANDARD_METALLICGRAY_THEME:
			standardColor = new GradientPaint(0, 0, new Color(151, 164, 170), 0,
					(float)(3 * height / 4.0), new Color(120, 137, 145));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.STANDARD_BLUEGRAY_THEME:
			standardColor = new GradientPaint(0, 0, new Color(68, 113, 153), 0,
					height, new Color(32, 53, 72));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.STANDARD_VOILET_THEME:
			standardColor = new GradientPaint(0, 0, new Color(148, 148, 255), 0,
					height, new Color(98, 98, 255));
			setComponentForegroundColor(component, Color.WHITE);
			break;
		case Theme.STANDARD_SILVER_THEME:
			standardColor = new GradientPaint(0, 0, new Color(236, 241, 242), 0,
					height, new Color(206, 220, 223));
			setComponentForegroundColor(component, Color.BLACK);
			break;
		default:
			standardColor = new GradientPaint(0, 0, new Color(149, 159, 207), 0,
					height, new Color(85, 134, 194));
			setComponentForegroundColor(component, Color.BLACK);
			break;

		}
		;
		return standardColor;
	}

	/**
     * Returns Gradient Color.
     * 
     * @param theme
     *            theme
     * @param height
     *            height of the component
     * @param component
     *            component
     * @return the gradient color
     */
	public GradientPaint getGradientColor(int theme, int height, JComponent component) {
	    /** The gradient color. */
	    GradientPaint gradientColor;
	    
		switch (theme) {
		case Theme.GRADIENT_DARKGREEN_THEME:
			gradientColor = new GradientPaint(0, 0,
					new Color(136, 255, 136), 0, height, new Color(1, 54, 2));
			setComponentForegroundColor(component, Color.WHITE);
			break;
		case Theme.GRADIENT_BLUEGREEN_THEME:
			gradientColor = new GradientPaint(0, 0,
					new Color(170, 240, 210), 0, height, new Color(12, 69, 45));
			setComponentForegroundColor(component, Color.WHITE);
			break;
		case Theme.GRADIENT_GREEN_THEME:
			gradientColor = new GradientPaint(0, 0, new Color(73, 252, 7),
					0, height, new Color(0, 64, 0));
			setComponentForegroundColor(component, Color.WHITE);
			break;
		case Theme.GRADIENT_OLIVEGREEN_THEME:
			gradientColor = new GradientPaint(0, 0, new Color(185, 234, 36),
					0, height, new Color(68, 116, 4));
			setComponentForegroundColor(component, Color.WHITE);
			break;
		case Theme.GRADIENT_LIME_THEME:
			gradientColor = new GradientPaint(0, 0,
					new Color(217, 242, 138), 0, height,
					new Color(168, 216, 24));
			setComponentForegroundColor(component, Color.BLACK);
			break;

		case Theme.GRADIENT_LIGHTGREEN_THEME:
			gradientColor = new GradientPaint(0, 0,
					new Color(159, 255, 159), 0, height, new Color(61, 208, 31));
			setComponentForegroundColor(component, Color.BLACK);
			break;

		case Theme.GRADIENT_RED_THEME:
			gradientColor = new GradientPaint(0, 0, new Color(249, 200, 0),
					0, height, new Color(242, 40, 30));
			setComponentForegroundColor(component, Color.WHITE);
			break;
		case Theme.GRADIENT_DARKRED_THEME:
			gradientColor = new GradientPaint(0, 0, new Color(249, 200, 0),
					0, height, new Color(181, 0, 0));
			setComponentForegroundColor(component, Color.WHITE);
			break;

		case Theme.GRADIENT_ORANGE_THEME:
			gradientColor = new GradientPaint(0, 0, new Color(255, 197, 63),
					0, height, new Color(255, 102, 0));
			setComponentForegroundColor(component, Color.WHITE);
			break;
		case Theme.GRADIENT_LIGHTORANGE_THEME:
			gradientColor = new GradientPaint(0, 0,
					new Color(255, 255, 255), 0, height, new Color(255, 133, 0));
			setComponentForegroundColor(component, Color.WHITE);
			break;
		case Theme.GRADIENT_DARKYELLOW_THEME:
			gradientColor = new GradientPaint(0, 0,
					new Color(255, 255, 255), 0, height, new Color(123, 120, 0));
			setComponentForegroundColor(component, Color.BLACK);
			break;

		case Theme.GRADIENT_GREENYELLOW_THEME:
			gradientColor = new GradientPaint(0, 0,
					new Color(255, 255, 255), 0, height, new Color(211, 204, 2));
			setComponentForegroundColor(component, Color.BLACK);
			break;
		case Theme.GRADIENT_GOLD_THEME:
			gradientColor = new GradientPaint(0, 0,
					new Color(255, 255, 255), 0, height,
					new Color(255, 201, 14));
			setComponentForegroundColor(component, Color.BLACK);
			break;
		case Theme.GRADIENT_YELLOW_THEME:
			gradientColor = new GradientPaint(0, 0,
					new Color(255, 255, 255), 0, height,
					new Color(255, 255, 56));
			setComponentForegroundColor(component, Color.BLACK);
			break;

		case Theme.GRADIENT_NAVYBLUE_THEME:
			gradientColor = new GradientPaint(0, 0, new Color(71, 232, 252),
					0, height, new Color(5, 25, 114));
			setComponentForegroundColor(component, Color.WHITE);
			break;
		case Theme.GRADIENT_INDIGO_THEME:
			gradientColor = new GradientPaint(0, 0,
					new Color(255, 255, 255), 0, height, new Color(34, 85, 146));
			setComponentForegroundColor(component, Color.WHITE);
			break;

		case Theme.GRADIENT_BLUE_THEME:
			gradientColor = new GradientPaint(0, 0, new Color(71, 232, 252),
					0, height, new Color(3, 37, 188));
			setComponentForegroundColor(component, Color.WHITE);
			break;

		case Theme.GRADIENT_SKYBLUE_THEME:
			gradientColor = new GradientPaint(0, 0, new Color(71, 232, 252),
					0, height, new Color(6, 113, 196));
			setComponentForegroundColor(component, Color.WHITE);
			break;

		case Theme.GRADIENT_LIGHTBLUE_THEME:
			gradientColor = new GradientPaint(0, 0, new Color(71, 232, 252),
					0, height, new Color(17, 160, 208));
			setComponentForegroundColor(component, Color.WHITE);
			break;

		case Theme.GRADIENT_DARKPURPLE_THEME:
			gradientColor = new GradientPaint(0, 0, new Color(186, 0, 255),
					0, height, new Color(44, 0, 89));
			setComponentForegroundColor(component, Color.WHITE);
			break;
		case Theme.GRADIENT_VOILET_THEME:
			gradientColor = new GradientPaint(0, 0,
					new Color(170, 170, 255), 0, height, new Color(98, 98, 255));
			setComponentForegroundColor(component, Color.WHITE);
			break;
		case Theme.GRADIENT_PURPLE_THEME:
			gradientColor = new GradientPaint(0, 0,
					new Color(255, 255, 255), 0, height,
					new Color(186, 60, 255));
			setComponentForegroundColor(component, Color.WHITE);
			break;
		case Theme.GRADIENT_LAVENDER_THEME:
			gradientColor = new GradientPaint(0, 0,
					new Color(255, 255, 255), 0, height, new Color(192, 128,
							255));
			setComponentForegroundColor(component, Color.WHITE);
			break;

		case Theme.GRADIENT_DARKPINK_THEME:
			gradientColor = new GradientPaint(0, 0,
					new Color(255, 255, 255), 0, height, new Color(115, 0, 85));
			setComponentForegroundColor(component, Color.WHITE);
			break;

		case Theme.GRADIENT_PINK_THEME:
			gradientColor = new GradientPaint(0, 0,
					new Color(255, 255, 255), 0, height, new Color(220, 22, 86));
			setComponentForegroundColor(component, Color.WHITE);
			break;
		case Theme.GRADIENT_PALEPINK_THEME:
			gradientColor = new GradientPaint(0, 0,
					new Color(255, 255, 255), 0, height, new Color(255, 128,
							223));
			setComponentForegroundColor(component, Color.WHITE);
			break;
		case Theme.GRADIENT_BLACK_THEME:
			gradientColor = new GradientPaint(0, 0,
					new Color(150, 150, 150), 0, height, new Color(0, 0, 0));
			setComponentForegroundColor(component, Color.WHITE);
			break;
		case Theme.GRADIENT_SILVER_THEME:
			gradientColor = new GradientPaint(0, 0,
					new Color(218, 228, 231), 0, height, new Color(255, 0, 0));
			setComponentForegroundColor(component, Color.BLACK);
			break;

		case Theme.GRADIENT_BROWN_THEME:
			gradientColor = new GradientPaint(0, 0, new Color(202, 62, 2),
					0, height, new Color(118, 35, 1));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.GRADIENT_LIGHTBROWN_THEME:
			gradientColor = new GradientPaint(0, 0,
					new Color(232, 194, 125), 0, height,
					new Color(212, 151, 37));
			setComponentForegroundColor(component, Color.WHITE);
			break;
		case Theme.GRADIENT_PALEBROWN_THEME:
			gradientColor = new GradientPaint(0, 0,
					new Color(248, 234, 203), 0, height, new Color(236, 205,
							132));
			setComponentForegroundColor(component, Color.BLACK);
			break;

		case Theme.GRADIENT_GRAY_THEME:
			gradientColor = new GradientPaint(0, 0,
					new Color(200, 200, 200), 0, height, new Color(70, 70, 70));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.GRADIENT_LIGHTGRAY_THEME:
			gradientColor = new GradientPaint(0, 0,
					new Color(183, 183, 183), 0, height, new Color(128, 128,
							128));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.GRADIENT_METALLICGRAY_THEME:
			gradientColor = new GradientPaint(0, 0,
					new Color(205, 210, 214), 0, (float)(3 * height / 4.0), new Color(120,
							137, 145));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.GRADIENT_BLUEGRAY_THEME:
			gradientColor = new GradientPaint(0, 0,
					new Color(141, 175, 205), 0, height, new Color(32, 53, 72));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		default:
			gradientColor = new GradientPaint(0, 0,
					new Color(149, 159, 207), 0, height,
					new Color(85, 134, 194));
			setComponentForegroundColor(component, Color.BLACK);
			break;

		}
		;

		return gradientColor;
	}

	/**
     * Gets the glass color.
     * 
     * @param theme
     *            the theme
     * @param height
     *            the height
     * @param component
     *            the component
     * @return the glossy color
     */
	public GradientPaint[] getGlassColor(int theme, int height, JComponent component) {
	    /** The glass colors. */
	    GradientPaint[] glassColors = new GradientPaint[2];
        /** The glass top color. */
        GradientPaint glassTopColor;
        /** The glass color. */
        GradientPaint glassColor;
		switch (theme) {
		case Theme.GLASS_DARKGREEN_THEME:
			glassTopColor = new GradientPaint(0, 0, new Color(3, 167, 7),
					0, (float)(height / 2.0), new Color(2, 117, 5, 150));
			glassColor = new GradientPaint(0, height, new Color(1, 54, 2),
					0, height, new Color(1, 54, 2));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.GLASS_BLUEGREEN_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(72, 223, 159), 0, (float)(height / 2.0), new Color(41, 218,
							142, 100));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(20,
					113, 74), 0, height, new Color(20, 113, 74));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.GLASS_LIGHTGREEN_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(219, 255, 202), 0, (float)(height / 2.0), new Color(219,
							255, 187, 100));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(97,
					204, 0), 0, height, new Color(97, 204, 0));
			setComponentForegroundColor(component, Color.BLACK);

			break;

		case Theme.GLASS_GREEN_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(211, 237, 194), 0, (float)(height / 2.0), new Color(109,
							176, 71));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(68,
					154, 23), 0, height, new Color(68, 154, 23));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.GLASS_LIME_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(231, 247, 183), 0, (float)(height / 2.0), new Color(192,
							234, 68));
			glassColor = new GradientPaint(0, height, new Color(168, 216,
					24), 0, height, new Color(168, 216, 24));
			setComponentForegroundColor(component, Color.BLACK);

			break;
		case Theme.GLASS_OLIVEGREEN_THEME:
			glassTopColor = new GradientPaint(0, 0, new Color(138, 234, 9),
					0, (float)(height / 2.0), new Color(128, 216, 7, 100));
			glassColor = new GradientPaint(0, 0, new Color(68, 116, 4), 0,
					height, new Color(68, 116, 4));
			setComponentForegroundColor(component, Color.WHITE);

			break;

		case Theme.GLASS_RED_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(255, 233, 232), 0, (float)(height / 2.0), new Color(255,
							160, 160));
			glassColor = new GradientPaint(0, 0, new Color(255, 0, 0), 0,
					height, new Color(255, 0, 0));
			setComponentForegroundColor(component, Color.BLACK);

			break;
		case Theme.GLASS_DARKRED_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(255, 191, 191), 0, (float)(height / 2.0), new Color(255,
							174, 174, 150));
			glassColor = new GradientPaint(0, 0, new Color(181, 0, 0), 0,
					height, new Color(181, 0, 0));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.GLASS_ORANGE_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(240, 240, 240), 0, (float)(height / 2.0), new Color(246,
							147, 90, 200));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(255,
					102, 0), 0, height, new Color(255, 102, 0));

			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.GLASS_LIGHTORANGE_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(250, 250, 250), 0, (float)(height / 2.0), new Color(255,
							216, 176, 150));
			glassColor = new GradientPaint(0, height,
					new Color(255, 153, 0), 0, height, new Color(255, 153, 0));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.GLASS_GREENYELLOW_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(253, 247, 15), 0, (float)(height / 2.0), new Color(253, 247,
							15, 150));
			glassColor = new GradientPaint(0, height,
					new Color(211, 204, 2), 0, height, new Color(211, 204, 2));
			setComponentForegroundColor(component, Color.BLACK);

			break;

		case Theme.GLASS_DARKYELLOW_THEME:
			glassTopColor = new GradientPaint(0, 0, new Color(221, 216, 0),
					0, (float)(height / 2.0), new Color(187, 183, 0, 150));
			glassColor = new GradientPaint(0, height,
					new Color(123, 120, 0), 0, height, new Color(123, 120, 0));
			setComponentForegroundColor(component, Color.WHITE);

			break;

		case Theme.GLASS_GOLD_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(255, 255, 255), 0, (float)(height / 2.0), new Color(255,
							230, 108));
			glassColor = new GradientPaint(0, height,
					new Color(255, 213, 0), 0, height, new Color(255, 213, 0));
			setComponentForegroundColor(component, Color.BLACK);
			break;

		case Theme.GLASS_YELLOW_THEME:
			glassColor = new GradientPaint(0, 0, new Color(254, 188, 16),
					0, height, new Color(252, 201, 56));
			glassTopColor = new GradientPaint(0, 0,
					new Color(254, 239, 192), 0, (float)(height / 2.0), new Color(254,
							227, 147, 150));
			setComponentForegroundColor(component, Color.BLACK);
			break;
		case Theme.GLASS_BROWN_THEME:
			glassColor = new GradientPaint(0, 0, new Color(118, 35, 1), 0,
					height, new Color(118, 35, 1));
			glassTopColor = new GradientPaint(0, 0,
					new Color(254, 173, 139), 0, (float)(height / 2.0), new Color(253,
							115, 55, 100));

			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.GLASS_LIGHTBROWN_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(240, 215, 166), 0, (float)(height / 2.0), new Color(226,
							179, 88));

			glassColor = new GradientPaint(0, 0, new Color(212, 151, 37),
					0, height, new Color(212, 151, 37));
			setComponentForegroundColor(component, Color.WHITE);
			break;
		case Theme.GLASS_PALEBROWN_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(253, 250, 242), 0, (float)(height / 2.0), new Color(242,
							221, 170));

			glassColor = new GradientPaint(0, 0, new Color(236, 205, 132),
					0, height, new Color(236, 205, 132));
			setComponentForegroundColor(component, Color.BLACK);
			break;

		case Theme.GLASS_NAVYBLUE_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(188, 200, 252), 0, (float)(height / 2.0), new Color(188,
							200, 252, 100));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(5, 25,
					114), 0, height, new Color(5, 25, 114));
			setComponentForegroundColor(component, Color.WHITE);
			break;
		case Theme.GLASS_BLUE_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(121, 145, 223), 0, (float)(height / 2.0), new Color(121,
							145, 223, 150));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(3, 37,
					188), 0, height, new Color(3, 37, 188));
			setComponentForegroundColor(component, Color.WHITE);
			break;

		case Theme.GLASS_INDIGO_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(150, 177, 211), 0, (float)(height / 2.0), new Color(40, 91,
							149));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(0, 59,
					127), 0, height, new Color(34, 85, 146));
			setComponentForegroundColor(component, Color.WHITE);
			break;
		case Theme.GLASS_SKYBLUE_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(206, 231, 255), 0, (float)(height / 2.0), new Color(206,
							231, 255, 100));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(0, 96,
					194), 0, height, new Color(0, 96, 194));

			setComponentForegroundColor(component, Color.WHITE);
			break;
		case Theme.GLASS_LIGHTBLUE_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(167, 227, 248), 0, (float)(height / 2.0), new Color(167,
							227, 248, 100));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(17,
					160, 208), 0, height, new Color(17, 106, 208));

			setComponentForegroundColor(component, Color.WHITE);
			break;
		case Theme.GLASS_VOILET_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(206, 206, 255), 0, (float)(height / 2.0), new Color(170,
							170, 255, 100));
			glassColor = new GradientPaint(0, 0, new Color(108, 108, 255),
					0, height, new Color(108, 108, 255));
			setComponentForegroundColor(component, Color.WHITE);
			break;
		case Theme.GLASS_DARKPURPLE_THEME:
			/*
			 * glassTopBtnColor = new GradientPaint(0, 0, new
			 * Color(202,149,255), 0, (float)(height / 2.0), new Color(160, 66, 255,150));
			 */
			glassTopColor = new GradientPaint(0, 0,
					new Color(202, 149, 255), 0, (float)(height / 2.0), new Color(135, 15,
							255, 100));

			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(44, 0,
					89), 0, height, new Color(44, 0, 89));

			setComponentForegroundColor(component, Color.WHITE);
			break;

		case Theme.GLASS_PURPLE_THEME:
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(186, 0,
					255), 0, height, new Color(186, 0, 255));
			glassTopColor = new GradientPaint(0, 0,
					new Color(238, 200, 224), 0, (float)(height / 2.0), new Color(222,
							152, 198, 150));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.GLASS_LAVENDER_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(208, 190, 233), 0, (float)(height / 2.0), new Color(147,
							105, 203, 150));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(107,
					60, 173), 0, height, new Color(107, 60, 173));

			setComponentForegroundColor(component, Color.WHITE);

			break;

		case Theme.GLASS_DARKPINK_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(255, 191, 239), 0, (float)(height / 2.0), new Color(255,
							191, 239, 100));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(115, 0,
					85), 0, height, new Color(115, 0, 85));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.GLASS_PINK_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(251, 215, 226), 0, (float)(height / 2.0), new Color(251,
							215, 226, 100));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(220,
					22, 86), 0, height, new Color(220, 22, 86));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.GLASS_PALEPINK_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(255, 236, 251), 0, (float)(height / 2.0), new Color(255,
							236, 251, 100));
			glassColor = new GradientPaint(0, height, new Color(255, 128,
					223), 0, height, new Color(255, 128, 223));
			setComponentForegroundColor(component, Color.WHITE);

			break;

		case Theme.GLASS_SILVER_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(250, 251, 253), 0, (float)(height / 2.0), new Color(238,
							243, 248));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(209,
					223, 237), 0, height, new Color(191, 210, 228));
			setComponentForegroundColor(component, Color.BLACK);
			break;
		case Theme.GLASS_BLACK_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(170, 170, 170), 0, (float)(height / 2.0), new Color(150,
							130, 130, 130));
			glassColor = new GradientPaint(0, 0, new Color(0, 0, 0), 0,
					height, new Color(0, 0, 0));
			setComponentForegroundColor(component, Color.WHITE);

			break;

		case Theme.GLASS_GRAY_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(197, 197, 197), 0, (float)(height / 2.0), new Color(128,
							128, 128, 150));
			glassColor = new GradientPaint(0, height,
					new Color(91, 91, 91), 0, height, new Color(91, 91, 91));
			setComponentForegroundColor(component, Color.WHITE);

			break;

		case Theme.GLASS_LIGHTGRAY_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(215, 215, 215), 0, (float)(height / 2.0), new Color(215,
							215, 215, 100));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(159,
					159, 159), 0, height, new Color(159, 159, 159));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.GLASS_METALIC_GRAY_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(200, 205, 209), 0, (float)(height / 2.0), new Color(120,
							137, 145, 100));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(73, 92,
					105), 0, height, new Color(73, 92, 105));
			setComponentForegroundColor(component, Color.WHITE);
			break;
		case Theme.GLASS_BLUEGRAY_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(200, 205, 209), 0, (float)(height / 2.0), new Color(120,
							137, 145, 100));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(32, 53,
					72), 0, height, new Color(32, 53, 72));
			setComponentForegroundColor(component, Color.WHITE);
			break;
		case Theme.GLASS_METALIC_BLUE_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(255, 255, 255), 0, (float)(height / 2.0), new Color(85, 134,
							194));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(1, 31,
					99), 0, height, new Color(137, 255, 255));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.GLASS_ORANGERED_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(255, 197, 63), 0, (float)(height / 2.0), new Color(255, 197,
							63, 100));

			glassColor = new GradientPaint(0, (float)(height / 2.0),
					new Color(0, 0, 0), 0, height, new Color(0, 0, 0));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.GLASS_ORANGEBLACK_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(255, 197, 63), 0, (float)(height / 2.0), new Color(255, 0,
							0, 100));
			glassColor = new GradientPaint(0, (float)(height / 2.0),
					new Color(0, 0, 0), 0, height, new Color(0, 0, 0));
			setComponentForegroundColor(component, Color.WHITE);

			break;

		case Theme.GLASS_BLUEBLACK_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(251, 139, 62), 0, (float)(height / 2.0), new Color(255, 102,
							0, 100));
			glassColor = new GradientPaint(0, (float)(height / 2.0),
					new Color(0, 0, 0), 0, height, new Color(0, 0, 0));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.GLASS_GREENBLACK_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(192, 234, 68), 0, (float)(height / 2.0), new Color(168, 216,
							24, 100));
			glassColor = new GradientPaint(0, (float)(height / 2.0),
					new Color(0, 0, 0), 0, height, new Color(0, 0, 0));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.GLASS_GOLDBLACK_THEME:
			glassTopColor = new GradientPaint(0, 0, new Color(255, 213, 0),
					0, (float)(height / 2.0), new Color(255, 213, 0, 100));

			glassColor = new GradientPaint(0, (float)(height / 2.0),
					new Color(0, 0, 0), 0, height, new Color(0, 0, 0));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.GLASS_MULTIBLUE_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(71, 232, 252), 0, (float)(height / 2.0), new Color(71, 232,
							252, 50));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(3, 37,
					188), 0, height, new Color(3, 37, 188));
			setComponentForegroundColor(component, Color.WHITE);
			break;
		case Theme.GLASS_MULTIRED_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(255, 193, 193), 0, (float)(height / 2.0), new Color(255,
							102, 102));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(255, 0,
					0), 0, height, new Color(255, 233, 232));
			setComponentForegroundColor(component, Color.WHITE);
			break;
		case Theme.GLASS_MULTIDARKRED_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(255, 191, 191), 0, (float)(height / 2.0), new Color(255,
							174, 174, 150));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(181, 0,
					0), 0, height, new Color(255, 191, 191));
			setComponentForegroundColor(component, Color.WHITE);

			break;

		case Theme.GLASS_MULTIGREEN_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(184, 226, 156), 0, (float)(height / 2.0), new Color(109,
							176, 71));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(61,
					135, 20), 0, height, new Color(103, 223, 38));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.GLASS_MULTIDARKGREEN_THEME:
			glassTopColor = new GradientPaint(0, 0, new Color(0, 242, 0),
					0, (float)(height / 2.0), new Color(0, 155, 0));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(0, 64,
					0), 0, height, new Color(0, 242, 0));
			setComponentForegroundColor(component, Color.WHITE);

			break;

		case Theme.GLASS_MULTIBLUEGREEN_THEME:
			glassTopColor = new GradientPaint(0, 0, new Color(0, 240, 156),
					0, (float)(height / 2.0), new Color(0, 183, 119));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(20,
					113, 74), 0, height, new Color(0, 221, 143));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.GLASS_MULTILIGHTGREEN_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(219, 255, 202), 0, (float)(height / 2.0), new Color(219,
							255, 187, 100));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(97,
					204, 0), 0, height, new Color(201, 255, 151));
			setComponentForegroundColor(component, Color.BLACK);

			break;
		case Theme.GLASS_MULTILIME_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(224, 244, 162), 0, (float)(height / 2.0), new Color(181,
							231, 31));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(130,
					167, 18), 0, height, new Color(191, 234, 62));

			break;
		case Theme.GLASS_MULTIOLIVEGREEN_THEME:
			glassTopColor = new GradientPaint(0, 0, new Color(138, 234, 9),
					0, (float)(height / 2.0), new Color(128, 216, 7, 100));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(68,
					116, 4), 0, height, new Color(148, 247, 15));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.GLASS_MULTIORANGE_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(250, 194, 160), 0, (float)(height / 2.0), new Color(255,
							153, 85));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(255,
					102, 0), 0, height, new Color(255, 218, 193));
			break;
		case Theme.GLASS_MULTIGOLD_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(255, 248, 223), 0, (float)(height / 2.0), new Color(255,
							226, 91));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(223,
					184, 0), 0, height, new Color(255, 239, 164));
			break;
		case Theme.GLASS_MULTINAVYBLUE_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(188, 200, 252), 0, (float)(height / 2.0), new Color(188,
							200, 252, 130));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(5, 25,
					114), 0, height, new Color(188, 200, 252));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.GLASS_MULTIINDIGO_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(150, 177, 211), 0, (float)(height / 2.0), new Color(40, 91,
							149));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(0, 59,
					127), 0, height, new Color(150, 177, 211));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.GLASS_MULTISKYBLUE_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(206, 231, 255), 0, (float)(height / 2.0), new Color(206,
							231, 255, 100));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(0, 96,
					194), 0, height, new Color(206, 231, 255));
			break;
		case Theme.GLASS_MULTILIGHTBLUE_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(167, 227, 248), 0, (float)(height / 2.0), new Color(167,
							227, 248, 100));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(17,
					160, 208), 0, height, new Color(255, 255, 255));
			break;
		case Theme.GLASS_MULTIDARKPURPLE_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(202, 149, 255), 0, (float)(height / 2.0), new Color(135, 15,
							255, 150));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(44, 0,
					89), 0, height, new Color(202, 149, 255));
			setComponentForegroundColor(component, Color.WHITE);
			break;
		case Theme.GLASS_MULTIPURPLE_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(238, 200, 224), 0, (float)(height / 2.0), new Color(222,
							152, 198, 150));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(186, 0,
					255), 0, height, new Color(238, 200, 224));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.GLASS_MULTILAVENDER_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(208, 190, 233), 0, (float)(height / 2.0), new Color(147,
							105, 203, 200));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(107,
					60, 173), 0, height, new Color(208, 190, 233));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.GLASS_MULTIVOILET_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(206, 206, 255), 0, (float)(height / 2.0), new Color(170,
							170, 255, 100));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(108,
					108, 255), 0, height, new Color(206, 206, 255));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.GLASS_MULTIDARKPINK_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(255, 191, 239), 0, (float)(height / 2.0), new Color(255,
							191, 239, 100));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(115, 0,
					85), 0, height, new Color(255, 191, 239));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.GLASS_MULTIPINK_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(251, 215, 226), 0, (float)(height / 2.0), new Color(251,
							215, 226, 100));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(220,
					22, 86), 0, height, new Color(251, 215, 226));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.GLASS_MULTIPALEPINK_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(255, 236, 251), 0, (float)(height / 2.0), new Color(255,
							236, 251, 100));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(255,
					128, 223), 0, height, new Color(255, 236, 251));
			break;
		case Theme.GLASS_MULTIBROWN_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(254, 173, 139), 0, (float)(height / 2.0), new Color(253,
							115, 55, 100));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(118,
					35, 1), 0, 2 * (float)(height / 2.0), new Color(254, 173, 139));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.GLASS_MULTILIGHTBROWN_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(240, 215, 166), 0, (float)(height / 2.0), new Color(226,
							179, 88));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(212,
					151, 37), 0, height, new Color(240, 215, 166));
			break;
		case Theme.GLASS_MULTIBLUEGRAY_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(200, 205, 209), 0, (float)(height / 2.0), new Color(120,
							137, 145, 150));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(32, 53,
					72), 0, height, new Color(200, 205, 209));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.GLASS_MULTIGRAY_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(197, 197, 197), 0, (float)(height / 2.0), new Color(128,
							128, 128, 150));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(91, 91,
					91), 0, height, new Color(197, 197, 197));
			setComponentForegroundColor(component, Color.WHITE);

			break;
		case Theme.GLASS_MULTILIGHTGRAY_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(215, 215, 215), 0, (float)(height / 2.0), new Color(215,
							215, 215, 100));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(159,
					159, 159), 0, height, new Color(215, 215, 215));
			break;
		case Theme.GLASS_MULTIBLACK_THEME:
			glassTopColor = new GradientPaint(0, 0,
					new Color(130, 130, 130), 0, (float)(height / 2.0), new Color(100,
							100, 100, 100));
			glassColor = new GradientPaint(0, (float)(height / 2.0),
					new Color(0, 0, 0), 0, height, new Color(170, 170, 170));
			setComponentForegroundColor(component, Color.WHITE);
			break;
		case Theme.GLASS_MULTIBLUECOLOR_THEME:
			// Blue
			glassTopColor = new GradientPaint(0, 0,
					new Color(34, 144, 255), 0, (float)(height / 2.0), new Color(0, 101,
							202));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(0, 82,
					164), 0, height, new Color(206, 231, 255));
            setComponentForegroundColor(component, Color.WHITE);
			break;
		default:
			glassTopColor = new GradientPaint(0, 0,
					new Color(149, 159, 207), 0, (float)(height / 2.0), new Color(85, 134,
							194));
			glassColor = new GradientPaint(0, (float)(height / 2.0), new Color(1, 31,
					99), 0, height, new Color(17, 213, 255));
			setComponentForegroundColor(component, Color.WHITE);
			break;
		}
		;
		glassColors[0] = glassTopColor;
		glassColors[1] = glassColor;
		return glassColors;
	}
	
    /**
     * Sets the component text color.
     * 
     * @param component
     *            the component
     * @param color
     *            the color
     */
    public void setComponentForegroundColor(JComponent component, Color color) {
        if(component != null) {
            component.setForeground(color);
        } else {
            ; // nothing to do
        }
    }
	
}

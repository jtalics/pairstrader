package com.jtalics.onyx.visual.graph;

import java.awt.BasicStroke;

/**
 * This enumeration defines the possible edge types for a {@link CpdaveGraphEdge}
 */
public enum CpdaveEdgeType {

	THIN_SOLID       (new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL)),
	THIN_DASHED      (new BasicStroke(0.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0f, new float[]{8f, 4f}, 0f)),
	STANDARD_SOLID   (new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL)),
	STANDARD_DASHED  (new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0f, new float[]{10f, 5f}, 0f)),
	THICK_SOLID      (new BasicStroke(4.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL)),
	THICK_DASHED     (new BasicStroke(4.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0f, new float[]{14f, 7f}, 0f)),
	
	;
	
	/**
	 * The stroke that should be used when painting edges of this type.
	 */
	private final BasicStroke stroke;
	
	/**
	 * Creates the new {@link CpdaveEdgeType} object.
	 * 
	 * @param stroke
	 */
	private CpdaveEdgeType(BasicStroke stroke) {
		this.stroke = stroke;
	}
	
	/**
	 * Gets the stroke that should be used when painting edges of this type.
	 * 
	 * @return
	 */
	public BasicStroke getStroke() {
		return stroke;
	}
}

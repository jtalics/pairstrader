package com.jtalics.onyx.visual.graph;

/**
 * This enumeration defines the status values that can be assigned to a node.
 */
public enum CpdaveNodeStatus {
	NORMAL,
	WARNING,
	ERROR,
	
	;
	
	public static CpdaveNodeStatus getMax(CpdaveNodeStatus status1, CpdaveNodeStatus status2) {
		return status1.ordinal() > status2.ordinal() ? status1 : status2;
	}
}

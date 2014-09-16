package com.jtalics.onyx.visual.navigation;

/**
 * The LTI View Type for the Visual Editor
 * @author cpdave
 *
 */
public enum LtiViewType {
	
	Uto ("uto"),
	RadioNetwork ("rn");
	
    // The String value of the enum
    private String value;

    private LtiViewType(final String value) {
        // Set the String value
        this.value = value;
	}

    /**
     * Returns the String value of the enum.
     * 
     * @return the String value of the enum
     */
    public String stringValue() {
        return value;
    }
}

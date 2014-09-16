package com.jtalics.onyx.visual.navigation;

public class Navigate {
	// The Base URL
	private static final String URL = "http://localhost:8088/CPDAVE-Server/landing.xhtml?target=";
	// Ampersand
	private static final String AMPERSAND = "&";
	// Selected LTI Prefix - followed by the actual selected LTI Set
	private static final String SELECTED_LTI_PREFIX = "selectedLti";
	// Equals
	private static final String EQUALS = "=";
	// Active Tab Prefix - followed by the actual Active Tab to Select
	private static final String ACTIVE_TAB_PREFIX = "activeTab";
	// Nodes Prefix - followed by the nodes to be selected
	private static final String NODES_PREFIX = "nds";
	
	// This is mapped to the correct page in the WebContent/WEB-INF/config/navigation-config.xml
	private String target = "showLtiNds";
	// The LTI Set to select
	private String selectedLti = "test3";
	// The Tree Nodes to Select
	private String nodes = "Root,SINCGARS NET,SECRET,4BN27FA-FM NET FD1 (2)::Root,SINCGARS NET,SECRET,1BN6IN-FM NET A/L (7)";
	// UTO or Radio Network View
	private LtiViewType viewType = LtiViewType.Uto;

	/**
	 * The Default Constructor
	 * @param type - The View Type (UTO or Radio Network)
	 * @param target - The Target Page
	 * @param selectedLtiSet - The Selected LTI Set
	 * @param nodes - The Tree nodes to highlight
	 */
	public Navigate(LtiViewType type, String target, String selectedLtiSet, String nodes) {
		this.viewType = type;
		this.target = target;
		this.selectedLti = selectedLtiSet;
		this.nodes = nodes;
	}
	
	/**
	 * Get the URL created from the inputs 
	 * @return - The URL to be fed to the Browser to select the page and highlight the nodes
	 */
	public String getUrl() {
		return URL+target+AMPERSAND+SELECTED_LTI_PREFIX+EQUALS+selectedLti+AMPERSAND+
				ACTIVE_TAB_PREFIX+EQUALS+viewType.stringValue()+AMPERSAND+
				NODES_PREFIX+EQUALS+nodes;
	}
	
	/**
	 * Get the Target Page
	 * @return the target
	 */
	public String getTarget() {
		return target;
	}

	/**
	 * Set the Target page
	 * @param target the target to set
	 */
	public void setTarget(String target) {
		this.target = target;
	}

	/**
	 * Get the Selected LTI Set
	 * @return the selectedLti
	 */
	public String getSelectedLti() {
		return selectedLti;
	}

	/**
	 * Set the Selected LTI Set
	 * @param selectedLti the selectedLti to set
	 */
	public void setSelectedLti(String selectedLti) {
		this.selectedLti = selectedLti;
	}

	/**
	 * Get the nodes to highlight
	 * @return the nodes
	 */
	public String getNodes() {
		return nodes;
	}

	/**
	 * Set the Nodes to highlight
	 * Example Root,SINCGARS NET,SECRET,4BN27FA-FM NET FD1 (2)::Root,SINCGARS NET,SECRET,1BN6IN-FM NET A/L (7)
	 * Separated by ::
	 * @param nodes the nodes to set
	 */
	public void setNodes(String nodes) {
		this.nodes = nodes;
	}
}
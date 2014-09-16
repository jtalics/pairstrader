package com.jtalics.onyx.visual.graph;

/**
 * Helper class for {@link GraphView} to watch the graph's model for changes.
 */
class ModelObserver implements GraphChangeListener {

	/**
	 * The graph that we should report changes to.
	 */
	private final GraphView graph;
	
	/**
	 * Creates a new model observer to report model updates to the specified graph.
	 * 
	 * @param graph - The graph to report model updates to.
	 */
	ModelObserver(GraphView graph) {
		this.graph = graph;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void structureChanged(GraphModel model) {
		if (graph.getLayoutOnUpdate()) {
			graph.dragging = false;
			if (graph.panning) {
				graph.panning = false;
				if (graph.getInteractionType() == GraphInteractionType.PAN) {
					graph.graphView.setCursor(GraphView.HAND_OPEN);
				} else {
					graph.graphView.setCursor(null);
				}
			} else { /* We weren't panning when the structure changed. */ ; }
			
			graph.layoutStale = true;
			graph.repaint();
		} else { /* This graph is not configured to rebuild its layout. */ ; }
	}
}

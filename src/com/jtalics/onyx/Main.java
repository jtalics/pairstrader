package com.jtalics.onyx;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JApplet;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.SoftBevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.jtalics.onyx.messaging.GraphicMessage;
import com.jtalics.onyx.messaging.GraphicMessageType;
import com.jtalics.onyx.visual.graph.CpdaveEdgeType;
import com.jtalics.onyx.visual.graph.CpdaveGraph;
import com.jtalics.onyx.visual.graph.CpdaveGraphEdge;
import com.jtalics.onyx.visual.graph.CpdaveGraphModel;
import com.jtalics.onyx.visual.graph.CpdaveGraphNode;
import com.jtalics.onyx.visual.graph.GraphSelectionListener;
import com.jtalics.onyx.visual.graph.GraphView;

/**
 * The Class WorkflowApplet.
 */
public class Main extends JFrame implements
		ChangeListener, TreeSelectionListener,
		TreeExpansionListener, GraphSelectionListener {
	/* Are the scroll bars visible */
	private boolean scrollbarsVisible = true;
	/* The scroller. */
	private JScrollPane scroller = null;
	/* The area. */
	private Dimension area = new Dimension(1100, 820); // indicates area taken
														// up by graphics
	/* The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	/* The main panel. */
	private final MainPanel mainPanel = new MainPanel(this);
	// private ButtonPanel buttonPanel = null;
	private boolean isInitialized = false;
	private CpdaveGraphModel model = null;
	private CpdaveGraph graph = null;
	private Map<String, CpdaveGraphNode> selectedNodeMap = new HashMap<String, CpdaveGraphNode>();
	private final DefaultTreeModel jTreeModel = new DefaultTreeModel(null);
	private final JTree jtree = new JTree(jTreeModel);
	MyTreeNode myTreeRoot;
	private boolean listenToTreeSelection=true;


	/**
	 * Instantiates a new workflow applet.
	 */
	public Main() {
		setInitialized(true);
	}

	// @Override was an applet
	public void init() {

		getContentPane().setLayout(new BorderLayout());
		// Create a panel to hold the main panel
		JPanel borderPanel = new JPanel(new BorderLayout());
		borderPanel.setBorder(new SoftBevelBorder(SoftBevelBorder.RAISED));
		getContentPane().add(borderPanel, BorderLayout.CENTER);

		// Now build the JTree navigation panel 
		// TODO - hide for production
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(jtree);
		getContentPane().add(panel, BorderLayout.WEST);

		area = new Dimension(area);
		// Let the user scroll a big area
		// mainPanel.setPreferredSize(new Dimension(area));
		// create the scroll pane for the Main Panel
		scroller = new JScrollPane(mainPanel);
		scroller.setPreferredSize(new Dimension(area));
		scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroller.getViewport().addChangeListener(this);

		// Add the scroll pane
		borderPanel.add(mainPanel, BorderLayout.CENTER);
		mainPanel.setLayout(new BorderLayout());
		showGraphView();
		this.revalidate();
		this.validate();
		this.repaint();
		// Add the top button panel
		// buttonPanel = new ButtonPanel(this, mainPanel);
		// borderPanel.add(buttonPanel, BorderLayout.NORTH);
		// mainPanel.requestFocusInWindow();

	}

	private void copySubTree(MyTreeNode treeNode,
			DefaultMutableTreeNode swingTreeNode) {
		swingTreeNode.setUserObject(treeNode);

		for (MyTreeNode visualChild : treeNode.children) {
			DefaultMutableTreeNode swingChild = new DefaultMutableTreeNode();
			swingTreeNode.add(swingChild);
			copySubTree(visualChild, swingChild);
		}
	}

	private void showGraphView() {

		// First simulate a UTO tree - this will be changed to read from a database
		myTreeRoot = buildTestTree();

		// Convert to Swing tree model by depth-first traversal
		DefaultMutableTreeNode swingRootNode = new DefaultMutableTreeNode();
		copySubTree(myTreeRoot, swingRootNode);

		// Set up the jtree in a fully expanded state
		jTreeModel.setRoot(swingRootNode);
	    expandAll(jtree, new TreePath(swingRootNode));
	    jtree.addTreeExpansionListener(this);
	    jtree.addTreeSelectionListener(this);
	    
		model = new CpdaveGraphModel();
		graph = new CpdaveGraph(model);
		graph.addSelectionListener(this);
		
		// Now draw the tree
		layoutAsTree(myTreeRoot);

		// layoutDemo(); // draws a ring of nodes

		graph.setAutoscrolls(true);
		mainPanel.add(graph, BorderLayout.CENTER);
	}
	
	private void expandAll(JTree tree, TreePath parent) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent
				.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				DefaultMutableTreeNode n = (DefaultMutableTreeNode) e
						.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				expandAll(tree, path);
			}
		}
		tree.expandPath(parent);
		// tree.collapsePath(parent);
	}

	private void layoutAsCircle() {
		// Generate nodes and place them in a circular pattern.
		final int nodeCount = 11;
		final int layoutRadius = 300;
		for (int i = 0; i < nodeCount; i++) {
			CpdaveGraphNode node = new CpdaveGraphNode(
					Math.sin((i / (double) nodeCount) * 2 * Math.PI)
							* layoutRadius, -Math.cos((i / (double) nodeCount)
							* 2 * Math.PI)
							* layoutRadius);

			node.setColor(Color.getHSBColor(i / (float) nodeCount, 0.25f, 0.9f));
			model.addNode(node);
		}

		// Generate standard solid links between nodes jumping back and
		// forth across the circle.
		int lastNodeIndex;
		int currentNodeIndex = 0;
		int nodeJumpSize = nodeCount / 2;
		List<CpdaveGraphNode> nodes = model.getNodes();
		do {
			lastNodeIndex = currentNodeIndex;
			currentNodeIndex = (currentNodeIndex + nodeJumpSize) % nodeCount;
			CpdaveGraphEdge edge = model.linkNodes(nodes.get(lastNodeIndex),
					nodes.get(currentNodeIndex));
			edge.setColor(new Color(40, 60, 100));
			edge.setEdgeType(CpdaveEdgeType.STANDARD_SOLID);
		} while (currentNodeIndex != 0);

		// Generate thick dashed links between adjacent nodes.
		for (int i = 0; i < nodeCount; i++) {
			CpdaveGraphEdge edge = model.linkNodes(nodes.get(i),
					nodes.get((i + 1) % nodeCount));
			edge.setColor(nodes.get(i).getColor().darker());
			edge.setEdgeType(CpdaveEdgeType.THICK_DASHED);
		}
	}

	private void listSubtree(MyTreeNode pnode, List<MyTreeNode> nodeList) {
		nodeList.add(pnode);
		for (MyTreeNode node : pnode.children) {
			listSubtree(node, nodeList);
		}
	}

	private void layoutAsTree(MyTreeNode root) {

		model.clearAll();
			
		layoutSubTree(root,null,0,0);
		repaint();
	}

	private int layoutSubTree(MyTreeNode myTreeNode, CpdaveGraphNode grandparentGraphNode, int depth, int sibIndex) {
	
		if (!myTreeNode.visible) return sibIndex;
		int deltaX = 100; int deltaY=300;
		CpdaveGraphNode graphNode = new CpdaveGraphNode(sibIndex * deltaX, depth * deltaY);
		graphNode.setColor(Color.getHSBColor(depth/5.0f/*TODO - find max*/, 0.25f, 0.9f));
		graphNode.setNodeName(myTreeNode.toString());
		graphNode.setUserObject(myTreeNode);
		model.addNode(graphNode);
		if (grandparentGraphNode != null) model.linkNodes(grandparentGraphNode, graphNode);
		graph.setSelected(graphNode, myTreeNode.selected);
		if (myTreeNode.selected) {
			graph.setCenter(new Point((int)graphNode.getX(),(int)graphNode.getY()));
		}
		depth++; sibIndex++;
		for (MyTreeNode child : myTreeNode.children) {
			sibIndex = layoutSubTree(child, graphNode, depth,sibIndex);
		}
		
		// Move the parent on top of the siblings
		double avgX=0.0, avgY=0.0;
		List<CpdaveGraphNode> destNodes = graph.getModel().getDestNodes(graphNode);
		for (CpdaveGraphNode node : destNodes) {
			avgX += node.getLocation().getX();
			avgY += node.getLocation().getY();
		}
		int destNodesCount = destNodes.size();
		if (destNodesCount > 0) {
			avgX /= destNodesCount;
			avgY /= destNodesCount;
			double x = graphNode.getLocation().getX();
			double y = graphNode.getLocation().getY();
			graphNode.setLocation(avgX, y);
		}		
		return sibIndex + 1;
	}
	
	private void layoutAsCircle(MyTreeNode root) {

		model.clearAll();
		List<MyTreeNode> myTreeNodeList = new ArrayList<MyTreeNode>();
		listSubtree(root, myTreeNodeList);
		final int nodeCount = myTreeNodeList.size();
		final int layoutRadius = 150;
		for (int i = 0; i < nodeCount; i++) {
			MyTreeNode myTreeNode = myTreeNodeList.get(i);
			if (!myTreeNode.visible) continue;
			CpdaveGraphNode node = new CpdaveGraphNode(
					Math.sin((i / (double) nodeCount) * 2 * Math.PI)
							* layoutRadius, -Math.cos((i / (double) nodeCount)
							* 2 * Math.PI)
							* layoutRadius);
			node.setColor(Color.getHSBColor(i / (float) nodeCount, 0.25f, 0.9f));
			node.setNodeName(myTreeNode.toString());
			node.setUserObject(myTreeNode);
			model.addNode(node);
			graph.setSelected(node, myTreeNode.selected);
			if (myTreeNode.selected) {
				graph.setCenter(new Point((int)node.getX(),(int)node.getY()));
			}
		}
		repaint();
	}

// TODO: delete following method after using it as an example
	public void onMessage(GraphicMessage msg) {
		GraphicMessageType type = msg.getMessageType();
		Object msgData = msg.getData();
		switch (type) {
		case TREE_ADD:
			System.out.println("TREE ADD Message Received");
			break;
		case TREE_DELETE:
			System.out.println("TREE DELETE Message Received");
			break;
		case TREE_NODE_SELECTION:
			System.out.println("TREE NODE SELECTION Message Received");
			if (msgData instanceof Map) {
				Map<String, Object> map = (Map) msgData;
				String selected = map.get("SELECTED").toString();
				String nodeName = map.get("Org_short_Name").toString();
				if (nodeName.equalsIgnoreCase("EMPTY")) {
					selectedNodeMap.clear();
					List<CpdaveGraphNode> list1 = model.getNodes();
					for (CpdaveGraphNode node : list1) {
						node.setCollapsed(true);
						node.setNodeName(null);
						invalidate();
						validate();
						repaint();
					}
				} else if (selected != null) {
					// JOptionPane.showMessageDialog(this,
					// "TREE NODE SELECTED = " + map.get("Org_short_Name"));
					if (selected.equalsIgnoreCase("TRUE")) {
						// Selected Node
						List<CpdaveGraphNode> list1 = model.getNodes();
						int listSize = selectedNodeMap.size();
						if (selectedNodeMap.size() < 11) {
							CpdaveGraphNode node = list1.get(listSize);
							if (selectedNodeMap.get(nodeName) == null) {
								selectedNodeMap.put(nodeName, node);
								node.setNodeName(nodeName);
								node.setCollapsed(false);
								invalidate();
								validate();
								repaint();
							} else {
								; // map already contains the node
							}
						} else {
							; // we can currently only do 11 nodes
						}
					} else {
						// Unselected Node
						CpdaveGraphNode unselectedNode = selectedNodeMap
								.get(nodeName);
						if (unselectedNode != null) {
							unselectedNode.setCollapsed(true);
							unselectedNode.setNodeName(null);
							selectedNodeMap.remove(nodeName);
							invalidate();
							validate();
							repaint();
						} else {
							; // map does not contain this node
						}

					}
				} else {
					; // Bad data - selected == null
				}
			}
			break;
		case ALL_DATA:
			// JOptionPane.showMessageDialog(this, type);
			if (msgData instanceof List) {
				// List<Map<String, Object>> list = (List<Map<String,
				// Object>>)msgData;
				List dataList = (List) msgData;
				if (dataList != null && dataList.size() > 0) {
					for (int index = 0; index < dataList.size(); index++) {
						Object listObject = dataList.get(index);
						if (listObject instanceof Map) {
							Map map = (Map) listObject;
							Object mapObject = map.get("Org_short_Name");
						} else {
							; // we only handle the map
						}
					}
				} else {
					; // nothing to do if the list is empty
				}

			}
			break;
		case APPLET_NODE_SELECTION:
		case APPLET_UPDATE:
		default:
			// nothing to do
			break;
		}
	}

	/**
	 * Scrollbar State Change Listener Event
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		if (!scroller.getVerticalScrollBar().isVisible()
				&& !scroller.getHorizontalScrollBar().isVisible()) {
			setScrollbarsVisible(false);
		} else {
			setScrollbarsVisible(true);
		}

	}

	/**
	 * Get Scroll Bars Visible
	 * 
	 * @return the scrollbarsVisible
	 */
	public boolean isScrollbarsVisible() {
		return scrollbarsVisible;
	}

	/**
	 * Set Scroll Bars Visible
	 * 
	 * @param scrollbarsVisible
	 *            the scrollbarsVisible to set
	 */
	public void setScrollbarsVisible(boolean scrollbarsVisible) {
		this.scrollbarsVisible = scrollbarsVisible;
	}

	public boolean isInitialized() {
		return isInitialized;
	}

	public void setInitialized(boolean isInitialized) {
		this.isInitialized = isInitialized;
	}

	private class MyTreeNode {
		MyTreeNode parent;
		Object userObject;
		boolean visible=true, selected=false;
		public final List<MyTreeNode> children = new ArrayList<MyTreeNode>();
		public boolean focus;

		public MyTreeNode(MyTreeNode parent, Object userObject) {
			// if parent == null then it's root
			this.parent = parent;
			if (parent != null) {
				this.parent.addChild(this);
			}
			this.userObject = userObject;
		}

		public MyTreeNode addChild(MyTreeNode child) {
			children.add(child);
			return this; // allows chaining of method calls
		}

		@Override
		public String toString() {
			return userObject.toString();
		}
	}

	private MyTreeNode buildTestTree() {
		MyTreeNode earth = new MyTreeNode(null, "Earth");
		MyTreeNode continent0 = new MyTreeNode(earth, "continent 0");
		MyTreeNode continent1 = new MyTreeNode(earth, "continent 1");
		MyTreeNode nation1A = new MyTreeNode(continent1, "nation 1A");

		MyTreeNode state1A1 = new MyTreeNode(nation1A, "state 1A1");
		MyTreeNode city1A1A = new MyTreeNode(state1A1, "city1A1A");
		MyTreeNode family1A1A1 = new MyTreeNode(city1A1A, "family1A1A1");
		MyTreeNode person1A1A1A = new MyTreeNode(family1A1A1, "Person1A1A1A");

		MyTreeNode state1A2 = new MyTreeNode(nation1A, "state 1A2");
		MyTreeNode city1A2A = new MyTreeNode(state1A2, "city1A1A");
		MyTreeNode family1A2A1 = new MyTreeNode(city1A2A, "family1A1A1");
		MyTreeNode person1A2A1A = new MyTreeNode(family1A2A1, "Person1A1A1A");

		MyTreeNode nation1B = new MyTreeNode(continent1, "nation 1B");

		MyTreeNode state1B1 = new MyTreeNode(nation1A, "state 1A1");
		MyTreeNode city1B1A = new MyTreeNode(state1A1, "city1A1A");
		MyTreeNode family1B1A1 = new MyTreeNode(city1A1A, "family1A1A1");
		MyTreeNode person1B1A1A = new MyTreeNode(family1A1A1, "Person1A1A1A");

		MyTreeNode state1B2 = new MyTreeNode(nation1A, "state 1A2");
		MyTreeNode city1B2A = new MyTreeNode(state1A2, "city1A1A");
		MyTreeNode family1B2A1 = new MyTreeNode(city1A2A, "family1A1A1");
		MyTreeNode person1B2A1A = new MyTreeNode(family1A2A1, "Person1A1A1A");

		MyTreeNode continent2 = new MyTreeNode(earth, "continent 2");
		MyTreeNode nation2A = new MyTreeNode(continent2, "nation 2A");
		MyTreeNode nation2B = new MyTreeNode(continent2, "nation 2B");
		MyTreeNode nation2C = new MyTreeNode(continent2, "nation 2C");
		MyTreeNode nation2D = new MyTreeNode(continent2, "nation 2D");
		MyTreeNode continent3 = new MyTreeNode(earth, "continent 3");
		MyTreeNode continent4 = new MyTreeNode(earth, "continent 4");
		return earth;
	}

	/** UNUSED
	private void drawFocus() {

		MyTreeNode root = buildTestTree(); // assume this comes in as a message
											// from server --> draw it

		model = new CpdaveGraphModel();
		graph = new CpdaveGraph(model, webSocket);

		// assume "continent 2" in focus
		// TreeNode tn = root;
		// while (!tn.userObject.equals("continent 2")) {
		// TODO
		// }
		MyTreeNode focusTreeNode = continent2;
		MyTreeNode prevFocusSib = focusTreeNode.getPrevSib(focusTreeNode);
		MyTreeNode postFocusSib = focusTreeNode.getNextSib(focusTreeNode);
		MyTreeNode focusParentTreeNode = focusTreeNode.parent;
		MyTreeNode focusChild0 = focusTreeNode.getChildZero();
		MyTreeNode focusChild1 = focusTreeNode.getChildOne();
		MyTreeNode focusChild2 = focusTreeNode.getChildTwo();

		double gridSize = 100;

		// Parent spatial slot (0,-1) (row,col)
		CpdaveGraphNode parentGNode = new CpdaveGraphNode(0, -gridSize);
		parentGNode.setNodeName(focusParentTreeNode.userObject.toString());
		parentGNode.setColor(Color.getHSBColor(0.25f, 0.25f, 0.9f));
		// model.addNode(parentGNode);

		// Prev sib slot (-1,0)
		CpdaveGraphNode prevFocusGNode = new CpdaveGraphNode(-gridSize, 0);
		prevFocusGNode.setNodeName(prevFocusSib.userObject.toString());
		prevFocusGNode.setColor(Color.getHSBColor(0.25f, 0.25f, 0.9f));
		// model.addNode(prevFocusGNode);
		// model.linkNodes(parentGNode, prevFocusGNode);

		// Focus slot (0,0)
		CpdaveGraphNode focusGNode = new CpdaveGraphNode(0, 0);
		focusGNode.setNodeName(focusTreeNode.userObject.toString());
		focusGNode.setColor(Color.getHSBColor(0.25f, 0.25f, 0.9f));
		// model.addNode(focusGNode);
		// model.linkNodes(parentGNode, focusGNode);

		// Post focus slot (1,0)
		CpdaveGraphNode postFocusGNode = new CpdaveGraphNode(gridSize, 0);
		postFocusGNode.setNodeName(postFocusSib.userObject.toString());
		postFocusGNode.setColor(Color.getHSBColor(0.25f, 0.25f, 0.9f));
		// model.addNode(postFocusGNode);
		// model.linkNodes(parentGNode, postFocusGNode);

		// Child 0 slot (-1,1)
		CpdaveGraphNode child0GNode = new CpdaveGraphNode(-gridSize, gridSize);
		child0GNode.setNodeName("CHILD 0");
		postFocusGNode.setColor(Color.getHSBColor(0.25f, 0.25f, 0.9f));
		// model.addNode(child0GNode);
		// model.linkNodes(focusGNode, child0GNode);

		// Child 1 slot (0,1)
		CpdaveGraphNode child1GNode = new CpdaveGraphNode(0, gridSize);

		child1GNode.setNodeName("CHILD 1");
		postFocusGNode.setColor(Color.getHSBColor(0.25f, 0.25f, 0.9f));
		// model.addNode(child1GNode);
		// model.linkNodes(focusGNode, child1GNode);

		// Child 2 slot (1,1)
		CpdaveGraphNode child2GNode = new CpdaveGraphNode(gridSize, gridSize);
		child2GNode.setNodeName("CHILD 2");
		postFocusGNode.setColor(Color.getHSBColor(0.25f, 0.25f, 0.9f));
		// model.addNode(child2GNode);
		// model.linkNodes(focusGNode, child2GNode);

		// CpdaveGraphEdge edge = model.linkNodes(model.getNodes().get(0),
		// model.getNodes().get(1)); edge.setColor(new Color(40, 60, 100));
		// edge.setEdgeType(CpdaveEdgeType.STANDARD_SOLID);
		 
		graph.setAutoscrolls(true);
		mainPanel.add(graph, BorderLayout.CENTER);
	}
*/

	@Override
	public void valueChanged(TreeSelectionEvent e) {
		if (!listenToTreeSelection) return;
		
		TreePath[] treeNodes = e.getPaths(); // 

		clearSelectionAndFocus(myTreeRoot);
		for (TreePath path : treeNodes) {
			((MyTreeNode)((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject()).selected = true;
		}

		// we will pan and zoom here
		TreePath newLeadSelection = e.getNewLeadSelectionPath();
		DefaultMutableTreeNode dmtn = (DefaultMutableTreeNode)newLeadSelection.getLastPathComponent();
		if (dmtn != null) {
			((MyTreeNode)dmtn.getUserObject()).focus = true;	
		}

		//TreePath oldLeadSelection = e.getOldLeadSelectionPath(); // we will pan and zoom here
		//((MyTreeNode)((DefaultMutableTreeNode)oldLeadSelection.getLastPathComponent()).getUserObject()).focus = true;

		layoutAsTree(myTreeRoot);
	}

	private void clearSelectionAndFocus(MyTreeNode parent) {
		if (!listenToTreeSelection) return;
		parent.selected = false;
		parent.focus = false;
		for (MyTreeNode node : parent.children) {
			clearSelectionAndFocus(node);
		}
	}

	@Override
	public void treeExpanded(TreeExpansionEvent event) {
		updateGraphViz();
		layoutAsTree(myTreeRoot);
	}

	@Override
	public void treeCollapsed(TreeExpansionEvent event) {
		updateGraphViz();
		layoutAsTree(myTreeRoot);		
	}

	private void updateGraphViz() {
		// Follow the JTree and set the visibilities.
		List<TreePath> visiblePathList = new ArrayList<TreePath>();
		TreePath rootPath = new TreePath(jTreeModel.getPathToRoot((DefaultMutableTreeNode) jTreeModel.getRoot()));
		getExpansionPaths(jtree, rootPath, true, visiblePathList);
		List<TreePath> invisiblePathList = new ArrayList<TreePath>();
		getExpansionPaths(jtree, rootPath, false, invisiblePathList);

		for (TreePath path : visiblePathList) {
			((MyTreeNode)((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject()).visible = true;
		}
		for (TreePath path : invisiblePathList) {
			((MyTreeNode)((DefaultMutableTreeNode)path.getLastPathComponent()).getUserObject()).visible = false;
		}
	}

	public void getExpansionPaths(JTree tree, TreePath parent, boolean getExpandedPaths, List<TreePath> list) {

		if (getExpandedPaths && tree.isVisible(parent) || !getExpandedPaths && !tree.isVisible(parent)) list.add(parent);

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent
				.getLastPathComponent();
		if (node.getChildCount() >= 0) {
			for (Enumeration e = node.children(); e.hasMoreElements();) {
				DefaultMutableTreeNode n = (DefaultMutableTreeNode) e
						.nextElement();
				TreePath path = parent.pathByAddingChild(n);
				getExpansionPaths(tree, path, getExpandedPaths, list);
			}
		}
	}

	@Override
	public void selectionChanged(GraphView source, Object obj, boolean selected) {
		if (!(obj instanceof CpdaveGraphNode)) return;
		CpdaveGraphNode node = (CpdaveGraphNode)obj;
		MyTreeNode myTreeNode = (MyTreeNode)node.getUserObject();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) jTreeModel.getRoot();
		for (Enumeration e = root.depthFirstEnumeration(); e.hasMoreElements();) {
		    DefaultMutableTreeNode node2 = (DefaultMutableTreeNode) e.nextElement();
		    if (node2.getUserObject() == myTreeNode) {
		    	listenToTreeSelection = false;
		    	jtree.setSelectionPath(new TreePath(jTreeModel.getPathToRoot(node2)));
		    	listenToTreeSelection = true;
		    }
		}
	}

	@Override
	public void selectionCleared(GraphView source) {
		// TODO		
	}
	
	public static void main(String[] args) {
		
		Main main = new Main();
		main.init();
		main.setSize(1000, 800);;
		main.setVisible(true);
		
	}
}
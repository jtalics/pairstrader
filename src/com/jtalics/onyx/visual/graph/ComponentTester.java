package com.jtalics.onyx.visual.graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
/**
 * Simple application to test components for use in the visual editor. 
 */
public class ComponentTester implements Runnable {

	/**
	 * Starts the component tester application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		setLaf();
		EventQueue.invokeLater(new ComponentTester());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JComponent contentPane = (JComponent)frame.getContentPane();
		
		CpdaveGraphModel model = new CpdaveGraphModel();
		CpdaveGraph graph = new CpdaveGraph(model);
		
		// Generate nodes and place them in a circular pattern.
		final int nodeCount = 11;
		final int layoutRadius = 300;
		for (int i = 0 ; i < nodeCount ; i++) {
			CpdaveGraphNode node = new CpdaveGraphNode(Math.sin((i / (double)nodeCount) * 2 * Math.PI) * layoutRadius,
					-Math.cos((i / (double)nodeCount) * 2 * Math.PI) * layoutRadius);
			
			node.setColor(Color.getHSBColor(i / (float)nodeCount, 0.25f, 0.9f));
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
			CpdaveGraphEdge edge = model.linkNodes(nodes.get(lastNodeIndex), nodes.get(currentNodeIndex));
			edge.setColor(new Color(40, 60, 100));
			edge.setEdgeType(CpdaveEdgeType.STANDARD_SOLID);
		} while (currentNodeIndex != 0);
		
		// Generate thick dashed links between adjacent nodes.
		for (int i = 0 ; i < nodeCount ; i++) {
			CpdaveGraphEdge edge = model.linkNodes(nodes.get(i), nodes.get((i + 1) % nodeCount));
			edge.setColor(nodes.get(i).getColor().darker());
			edge.setEdgeType(CpdaveEdgeType.THICK_DASHED);
		}
		
		graph.setAutoscrolls(true);
		contentPane.add(graph, BorderLayout.CENTER);
		
		frame.setSize(900, 800);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	/**
	 * Assigns the look-and-feel.
	 */
	private static void setLaf() {
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        } else { ;
		        	// Check the next LAF.
		        }
		    }
		} catch (ClassNotFoundException |
		         InstantiationException |
		         IllegalAccessException |
		         UnsupportedLookAndFeelException e) {;} // Just use the default LAF.
	}
}


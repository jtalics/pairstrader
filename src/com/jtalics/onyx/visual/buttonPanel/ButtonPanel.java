package com.jtalics.onyx.visual.buttonPanel;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;

import com.jtalics.onyx.Main;
import com.jtalics.onyx.MainPanel;
import com.jtalics.onyx.visual.dialog.ErrorDialog;

/**
 * The Class ButtonPanel.
 */
public class ButtonPanel extends JPanel implements ActionListener {
	
	/* The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/* The title. */
	private String title = "";
	
	/* The zoom in. */
	private GlassButton zoomIn = null;
	
	/* The zoom out. */
	private GlassButton zoomOut = null;

	/* The zoom to fit. */
	private GlassButton zoomToFit = null;
	
    /* The original zoom in image. */
    private BufferedImage originalZoomInImage = null;
    
    /* The zoom in image. */
    private Image zoomInImage = null;
	
    /* The original zoom out image. */
    private BufferedImage originalZoomOutImage = null;
    
    /* The zoom out image. */
    private Image zoomOutImage = null;
	
    /* The original zoom to fit image. */
    private BufferedImage originalZoomToFitImage = null;
    
    /* The zoom to Fit image. */
    private Image zoomToFitImage = null;
	
    /* The Zoom Button Timer */
    private Timer timer = new Timer(100, this);
    
    /* Is this a Zoom In Action or Zoom Out Action */
    private Boolean zoomInAction = Boolean.FALSE;
    
    private Main applet = null;
    private MainPanel mainPanel = null;
    
	/**
	 * Create the panel.
	 *
	 * @param applet the applet
	 * @param panel the panel
	 */
	public ButtonPanel(Main theApplet, MainPanel panel) {
        try {
        	applet = theApplet;
        	mainPanel = panel;
    		// set the layout
    		setLayout(new BorderLayout(0, 0));
    		
    		// Create the Top Panel
    		GradientPanel topPanel = new GradientPanel();
    		add(topPanel, BorderLayout.NORTH);
    		topPanel.setBorder(new BevelBorder(BevelBorder.RAISED));
    		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
    		// Add the Title
    		JLabel lblCpdaveWorkflow = new JLabel(title);
    		topPanel.add(Box.createHorizontalStrut(5));
    		topPanel.add(lblCpdaveWorkflow);
    		
    		topPanel.add(Box.createHorizontalStrut(20));
    		topPanel.add(Box.createHorizontalGlue());
		
            originalZoomToFitImage = ImageIO.read(ButtonPanel.class.getResource("zoom_to_fit_16.png"));
	        zoomToFitImage = originalZoomToFitImage.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
			zoomToFit = new GlassButton(new ImageIcon(zoomToFitImage));
			zoomToFit.setColorTheme(Theme.GLASS_METALIC_BLUE_THEME);
			zoomToFit.setRollOverColorTheme(Theme.GLASS_LIGHTBLUE_THEME);
            zoomToFit.setSelectedColorTheme(Theme.GLASS_LIGHTBLUE_THEME);
			zoomToFit.setToolTipText("Fit To Window");
			//zoomToFit.addMouseListener(new ZoomToFitListener(mainPanel, this));
			topPanel.add(zoomToFit);

			topPanel.add(Box.createHorizontalStrut(10));
			
			originalZoomInImage = ImageIO.read(ButtonPanel.class.getResource("zoom_in_16.png"));
	        zoomInImage = originalZoomInImage.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
			
			zoomIn = new GlassButton(new ImageIcon(zoomInImage));
            zoomIn.setColorTheme(Theme.GLASS_METALIC_BLUE_THEME);
            zoomIn.setRollOverColorTheme(Theme.GLASS_LIGHTBLUE_THEME);
            zoomIn.setSelectedColorTheme(Theme.GLASS_LIGHTBLUE_THEME);
			zoomIn.setToolTipText("Zoom In");
			//zoomIn.addMouseListener(new ZoomInListener(mainPanel, this));
			topPanel.add(zoomIn);

			topPanel.add(Box.createHorizontalStrut(10));
			
			originalZoomOutImage = ImageIO.read(ButtonPanel.class.getResource("zoom_out_16.png"));
	        zoomOutImage = originalZoomOutImage.getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
			
			zoomOut = new GlassButton(new ImageIcon(zoomOutImage));
            zoomOut.setColorTheme(Theme.GLASS_METALIC_BLUE_THEME);
            zoomOut.setRollOverColorTheme(Theme.GLASS_LIGHTBLUE_THEME);
            zoomOut.setSelectedColorTheme(Theme.GLASS_LIGHTBLUE_THEME);
			zoomOut.setToolTipText("Zoom Out");
			//zoomOut.addMouseListener(new ZoomOutListener(mainPanel, this));
			topPanel.add(zoomOut);

        } catch (Throwable e2) {
            displayException("ButtonPanel",e2);     
        }
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(isZoomInAction()) {
			//mainPanel.getWorkflowZoom().zoom(1);
		} else {
			//mainPanel.getWorkflowZoom().zoom(0);
		}
		//mainPanel.update();
	}

	/**
	 * @return the zoomInAction
	 */
	public Boolean isZoomInAction() {
		return zoomInAction;
	}

	/**
	 * @param zoomInAction the zoomInAction to set
	 */
	public void setZoomInAction(boolean zoomInAction) {
		this.zoomInAction = Boolean.valueOf(zoomInAction);
	}
	
    /**
     * Display Error (Exceptions)
     */
    public void displayException(String method, Throwable e) {
		StringBuffer buffer = new StringBuffer();
		StackTraceElement[] trace = e.getStackTrace();
		for(StackTraceElement element : trace)    {
			buffer.append(element.toString()+"\r");
		}
		String message = e.getMessage();
		
		if (message == null) {
			message = e.getCause().getMessage();
		} else {
        	; // nothing to do
        }
		ErrorDialog dialog = new ErrorDialog(method+" EXCEPTION "+message + "\n" + buffer.toString());
		dialog.setSize(800, 800);
    }

	/**
	 * Start or Stop the Timer
	 */
	public void startTimer(boolean start) {
		if(start) {
			timer.start();
		} else {
			timer.stop();
		}
	}
}

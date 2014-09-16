/* This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY
 * IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR
 * NON-INFRINGEMENT, ARE HEREBY EXCLUDED. JTALICS LLC AND ITS LICENSORS SHALL NOT BE
 * LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING
 * OR DISTRIBUTING THE SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL JTALICS LLC OR ITS
 * LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
 * INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF
 * OR INABILITY TO USE SOFTWARE, EVEN IF JTALICS LLC HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 */
package com.jtalics.pairstrader;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
//Sold AS-IS with no warranties expressed or implied. 
import javax.swing.UnsupportedLookAndFeelException;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

import com.jtalics.pairstrader.util.SplashWindow;

public class Main {
	public static final String VERSION = " v0.04f";
	public static final Date expirationDate = new GregorianCalendar(2014, Calendar.SEPTEMBER, 30).getTime();

	public static String PRODUCT_NAME = "Pairs Trader";
	public static HelpBroker hb;

	// This method is called to start the application
	public static void main(String args[]) throws Exception {
		System.out.println("Starting in dir: " + System.getProperty("user.dir"));
		try {
			// Set cross-platform Java L&F (also called "Metal")
			// UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (UnsupportedLookAndFeelException e) {
			// handle exception
		}
		catch (ClassNotFoundException e) {
			// handle exception
		}
		catch (InstantiationException e) {
			// handle exception
		}
		catch (IllegalAccessException e) {
			// handle exception
		}
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				checkExpired(new Main());
			}
			
		});
	}

	private static void checkExpired(Main main) {
		if (new Date().before(expirationDate)) {
			SplashWindow.textColor = Color.GRAY;
			SplashWindow.showSplash();
			//main = new Main();
			Main.startApp();
			SplashWindow.hideSplash();
		}
		else {
			String message = PRODUCT_NAME + VERSION + "\n expired on " + expirationDate + ".\nhttp://www.jtalics.com.";
			System.err.println(message);
			JOptionPane.showMessageDialog(null, message);
		}
	}

	public static void shutdown() {
		SplashWindow.textColor = Color.YELLOW;
		SplashWindow.showSplash();
		// notifyShutdownListeners();
		SplashWindow.updateLoadingText("Shutting down...");
		SplashWindow.hideSplash();
		System.exit(0);
	}

	public static void inform(final Component parent, final String str) {
		if (SwingUtilities.isEventDispatchThread()) {
			showMsg(parent, str, JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					showMsg(parent, str, JOptionPane.INFORMATION_MESSAGE);
				}
			});
		}
	}

	private static void showMsg(Component parent, String str, int type) {
		// this function pops up a dlg box displaying a message
		JOptionPane.showMessageDialog(parent, str, PRODUCT_NAME, type);
	}

	public static void centerOnScreen(final Window frame) {
		final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		if (screenSize.getWidth() > frame.getWidth() && screenSize.getHeight() > frame.getHeight()) {
			frame.setLocation((int) (screenSize.getWidth() - frame.getWidth()) / 2, (int) (screenSize.getHeight() - frame.getHeight()) / 2);
		}
		else { // preferred height or width is larger than screen so maximize frame
			frame.setBounds(0, 0, (int) screenSize.getWidth(), (int) screenSize.getHeight());
		}
	}

	public static void startApp() {
		try {
			System.out.println(PRODUCT_NAME + VERSION + " IS ALIVE.");
			SplashWindow.updateLoadingText("Initializing...");
			String helpHS = "com/jtalics/pairstrader/help/helpset.hs";
			ClassLoader cl = Main.class.getClassLoader();
			try {
			    URL hsURL = HelpSet.findHelpSet(cl, helpHS);
			    HelpSet hs = new HelpSet(null, hsURL);
			    hb = hs.createHelpBroker();
			} catch (Exception ee) {
			    System.out.println( "HelpSet " + ee.getMessage());
			    System.out.println("HelpSet "+ helpHS +" not found");
			    return;
			}
			final MainFrame mainFrame = new MainFrame();
			mainFrame.setTitle(PRODUCT_NAME + VERSION + " ******************** ALPHA - UNDER DEVELOPMENT");
			centerOnScreen(mainFrame);
			// inform(frame, "hello");
			SplashWindow.updateLoadingText("Loading...");
			mainFrame.setVisible(true);
			if (PreferencesDialog.getAutoConnect()) {
				mainFrame.connectTo("",7496,0); // TODO: move defaults to preferences
				if (JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(mainFrame, "Do you understand and agree that\nthere is no warranty, implied or expressed?","Legal question",JOptionPane.YES_NO_OPTION)) {
					System.exit(0);
				};
			}
			mainFrame.fetch();
		}
		catch (final Throwable e) {
			e.printStackTrace();
			// logger.log(Level.SEVERE, "Could not initialize application: " + e, e);
			shutdown();
		}
	}

	static public void playStopLoss() {
		try {
			playSound(Main.class.getResource("stoploss.wav"));
		}
		catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	static public void playStopGain() {
		try {
			playSound(Main.class.getResource("stopgain.wav"));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	static public void playSound(URL url) throws Exception {

		AudioInputStream sound = AudioSystem.getAudioInputStream(url);

		// load the sound into memory (a Clip)
		DataLine.Info info = new DataLine.Info(Clip.class, sound.getFormat());
		Clip clip = (Clip) AudioSystem.getLine(info);
		clip.open(sound);

		// due to bug in Java Sound, explicitly exit the VM when
		// the sound has stopped.
		clip.addLineListener(new LineListener() {
			@Override
			public void update(LineEvent event) {
				if (event.getType() == LineEvent.Type.STOP) {
					event.getLine().close();
				}
			}
		});

		// play the sound clip
		clip.start();
	}
	
	public static void launch() throws BundleException, InterruptedException {
		/* SUMMARY:
		 * (1.) get a FrameworkFactory using java.util.ServiceLoader. (2.) create an
		 * OSGi framework using the FrameworkFactory (3.) start the OSGi framework 
		 * (4.) Install your bundle(s). (5.) Start all the bundles you installed. 
		 * (6.) Wait for the OSGi framework to shutdown.
		 */
		// Use the standard Java "ServiceLoader" approach to finding FrameworkFactory 
		// implementations in the classpath. The OSGi implementation (whatever it is) 
		// should have the appropriate entry in its MANIFEST.MF for the ServiceLoader to find..
		FrameworkFactory frameworkFactory = ServiceLoader.load(FrameworkFactory.class).iterator().next();

		// Start up the core OSGi stuff; the result is an environment with exactly
		// one OSGI bundle in it : the osgi "system" bundle.
		Map<String, String> config = initConfig();
		Framework framework = frameworkFactory.newFramework(config);
		framework.start();

		// Tell OSGi to load a bunch of bundles (aka jarfiles)
		BundleContext context = framework.getBundleContext();
		List<Bundle> installedBundles = new LinkedList<Bundle>();

		installedBundles.add(context.installBundle("file:/some/library.jar"));
		installedBundles.add(context.installBundle("file:/some/otherlib.jar"));

		// And now "start" the loaded bundles - except for "fragment" ones
		for (Bundle bundle : installedBundles) {
			if (bundle.getHeaders().get(Constants.FRAGMENT_HOST) == null) {
				bundle.start();
			}
		}

		// and finally leave the bundles alone to deal with events..
		try {
			framework.waitForStop(0);
		}
		finally {
			System.exit(0);
		}
	}

	static Map<String, String> initConfig() {
		Map<String, String> config = new HashMap<String, String>();

		// Allow OSGi bundles to import this package from the standard java
		// classpath classloader
		config.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA, "example.package");

		// See the org.osgi.framework.Constants class for other useful things that
		// can be put
		// into the OSGi configuration map.

		return config;
	}
}

package it.unisa.di.weblab.polyrec.gdt;

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * @author roberto
 *
 */
@SuppressWarnings("serial")
public class MainFrame extends JFrame implements WindowListener {

	static final int MAINSCREEN = 0;
	static final int DETAILSCREEN = 1;

	int screen_mode = MAINSCREEN;

	private String openedFile;//name of opened file
	private String extOpenedFile;//extension of opened file

	static final String EXTENSION_PGS = "PolyRec gesture set (.pgs)";
	static final String EXTENSION_XML = "Extnsible markup language (.xml)";
	static final String EXTENSION_JAR = "Java archive (.jar)";

	private ExtendedPolyRecognizerGSS recognizer;

	private JPanel container;
	private Menu menu;



	/**
	 * @param args
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws UnsupportedLookAndFeelException
	 * @throws IOException
	 */
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException,
			IllegalAccessException, UnsupportedLookAndFeelException, IOException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		new MainFrame();

		Settings.loadSettings();

	}

	/**
	 * @throws IOException
	 */
	public MainFrame() throws IOException {

		recognizer = new ExtendedPolyRecognizerGSS();

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setTitle("PolyRec GDT");

		// setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(this);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setMinimumSize(new Dimension(1024, 980));
		setLocation(dim.width / 2 - getSize().width / 2, dim.height / 2 - getSize().height / 2);

		setVisible(true);

		menu = new Menu(this);
		setScreen(new DashboardScreen(this, false));

	}

	/**
	 * @param panel
	 */
	public void setScreen(JPanel panel) {

		container = panel;

		setContentPane(container);

		repaint();

	}

	
	public JPanel getScreen() {
		return container;

	}

	public void paint(Graphics g) {

		paintComponents(g);

	}

	@Override
	public void windowOpened(WindowEvent e) {

	}

	@Override
	public void windowClosing(WindowEvent e) {
		File file = new File("gestures.pgs");
		file.deleteOnExit();
		int result = JOptionPane.CLOSED_OPTION;
	
		if (menu.save.isEnabled() || menu.saveas.isEnabled()) {
			result = JOptionPane.showConfirmDialog(null, "Save Gesture Set before closing?", "Confirm",
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		
			if (result == JOptionPane.YES_OPTION) {
				if (menu.save.isEnabled())
					menu.save.doClick();
				else if (menu.saveas.isEnabled())
					menu.saveas.doClick();
				
				
				dispose();
				System.exit(0);
			} else if (result == JOptionPane.NO_OPTION) {
				
				dispose();
				System.exit(0);
			}
			
			
		} else {
			
			dispose();
			System.exit(0);
		}
		

	}

	@Override
	public void windowClosed(WindowEvent e) {

	}

	@Override
	public void windowIconified(WindowEvent e) {

	}

	@Override
	public void windowDeiconified(WindowEvent e) {

	}

	@Override
	public void windowActivated(WindowEvent e) {

	}

	@Override
	public void windowDeactivated(WindowEvent e) {

	}

	
	public ExtendedPolyRecognizerGSS getRecognizer() {
		return recognizer;
	}

	public void setRecognizer(ExtendedPolyRecognizerGSS recognizer) {
		this.recognizer = recognizer;
	}
	
	public String getOpenedFile() {
		return openedFile;
	}

	public void setOpenedFile(String openedFile) {
		this.openedFile = openedFile;
	}
	
	public String getExtOpenedFile() {
		return extOpenedFile;
	}

	public void setExtOpenedFile(String extOpenedFile) {
		this.extOpenedFile = extOpenedFile;
	}
	
	public Menu getMenu() {
		return menu;
	}


	public static boolean isModalDialogShowing() {
		Window[] windows = Window.getWindows();
		if (windows != null) {
			for (Window w : windows) {
				if (w.isShowing() && w instanceof Dialog)
					return true;
			}
		}
		return false;
	}
	

}

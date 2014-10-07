package malictus.tagcorral.ui.viewer;

import java.awt.*;
import java.io.*;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.*;
import malictus.tagcorral.ui.*;
import malictus.tagcorral.lib.chunk.MetaChunk;
import malictus.tagcorral.lib.file.*;

/**
 * Main window for the Viewer application
 */
public class MainWindow extends JFrame {

	public static final int INITIAL_WIDTH = 950;
	public static final int INITIAL_HEIGHT = 700;
	public static final int SMALLEST_WIDTH = 875;
	public static final int SMALLEST_HEIGHT = 560;
	static public Vector<Integer> supportedFileTypes = new Vector<Integer>();
	static {
		supportedFileTypes.add(TCFileGuesser.FILETYPE_GIF);
		supportedFileTypes.add(TCFileGuesser.FILETYPE_PNG);
		supportedFileTypes.add(TCFileGuesser.FILETYPE_RIFF);		//also currently includes WAV and AVI
	}
	
	//the currently open folder
	private File theFolder = null;
	//the current file
	private TCFile theFile = null;
	protected JFileChooser folderchooser = new JFileChooser();
	
	private JPanel contentPane = null;
	private TagListPanel tagListPanel = null;
	private TagInfoPanel tagInfoPanel = null;
	private FileBrowserPanel fileBrowserPanel = null;
	
	/* ******************
	 * MAIN METHOD
	 * ******************/
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception err) {
			JOptionPane.showMessageDialog(null, "Error setting look and feel.");
		}
		new MainWindow();
	}
	
	/* ******************
	 * CONSTRUCTOR
	 * ******************/
	public MainWindow() {
		super();
		try {
			java.net.URL myurl = this.getClass().getResource("/malictus/tagcorral/ui/resources/logo.gif");
			Image im = Toolkit.getDefaultToolkit().getImage(myurl);
			this.setIconImage(im);
		} catch (Exception err) { }
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(true);
        this.setTitle(TCStrings.getStringFor("VIEWER_PROGRAM_NAME") + " " + TCStrings.getStringFor("VIEWER_VERSION"));
        contentPane = new JPanel();
        this.setContentPane(contentPane);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        BorderLayout bl = new BorderLayout();
        bl.setHgap(5);
        bl.setVgap(5);
        contentPane.setLayout(bl);
        this.setSize(INITIAL_WIDTH, INITIAL_HEIGHT);
        this.setMinimumSize(new Dimension(SMALLEST_WIDTH, SMALLEST_HEIGHT));        
        tagListPanel = new TagListPanel(this);
        tagListPanel.setPreferredSize(new Dimension(280, 100));
        tagInfoPanel = new TagInfoPanel();
        fileBrowserPanel = new FileBrowserPanel(this);
        JSplitPane bottomLeftRightPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tagListPanel, tagInfoPanel);
        JSplitPane topBottom = new JSplitPane(JSplitPane.VERTICAL_SPLIT, fileBrowserPanel, bottomLeftRightPanel);
        contentPane.add(topBottom);
        folderchooser.setMultiSelectionEnabled(false);
		folderchooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        UIUtils.centerWindow(this);
        this.setVisible(true);
		newFileSelected();
	}
	
	/* ******************
	 * GETTERS
	 * ******************/
	public File getCurrentFolder() {
		return theFolder;
	}
	
	public TCFile getCurrentFile() {
		return theFile;
	}
	
	/* ******************
	 * MESSAGES RECEIVED
	 * ******************/
	//file browser set a new folder
	protected void MSG_fileBrowserFolderChosen (File folder) {
		this.theFolder = folder;
	}
	
	//file browser set a new file (may be null for folder or nonsupported file)
	protected void MSG_fileBrowserFileChosen (TCFile file) {
		this.theFile = file;
		newFileSelected();
	}
	
	//TagListPanel selected a new chunk (use null for nothing selected)
	protected void MSG_tagListPanelNewChunk(MetaChunk chunk) {
		if (chunk == null) {
			tagInfoPanel.MSG_mainWindowNewChunk(null);
		} else {
			tagInfoPanel.MSG_mainWindowNewChunk(chunk);	
		}
	}
	
	//turn off (or on) the interface while doing something with a timer
	protected void MSG_setInterfaceEnabled(boolean val) {
		if (!val) {
			setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		} else {
			setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		}
		if (tagListPanel != null) {
			tagListPanel.MSG_setInterfaceEnabled(val);
		}
		if (tagInfoPanel != null) {
			tagInfoPanel.MSG_setInterfaceEnabled(val);
		}
		if (fileBrowserPanel != null) {
			fileBrowserPanel.MSG_setInterfaceEnabled(val);
		}
	}
	
	/* ******************
	 * HELPER METHODS
	 * ******************/
	protected void showErrorMessage(String message) {
		JOptionPane.showMessageDialog(this, message, TCStrings.getStringFor("ERROR_DIALOG_TITLE"), JOptionPane.ERROR_MESSAGE);
	}
	
	private void newFileSelected() {
		if (theFile == null) {
			this.setTitle(TCStrings.getStringFor("VIEWER_PROGRAM_NAME") + " " + TCStrings.getStringFor("VIEWER_VERSION"));
			tagListPanel.MSG_mainWindowFileOpened(null);	
			tagInfoPanel.MSG_mainWindowNewChunk(null);
		} else {
			this.setTitle(TCStrings.getStringFor("VIEWER_PROGRAM_NAME") + " " + TCStrings.getStringFor("VIEWER_VERSION") + " - " + theFile.getName());
			tagListPanel.MSG_mainWindowFileOpened(theFile);
			tagInfoPanel.MSG_mainWindowNewChunk(null);
		}
	}
	
}

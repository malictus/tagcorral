package malictus.tagcorral.ui.viewer;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import javax.swing.*;
import java.io.*;
import java.net.URI;
import java.util.Hashtable;
import malictus.tagcorral.lib.file.TCFile;
import malictus.tagcorral.ui.*;

/**
 * A panel that contains a file browser to navigate around
 */
public class FileBrowserPanel extends JScrollPane {
	
	private TCLabel lblFolder;
	private JButton btnFolderBrowse;
	private FileBrowserTable tblFileBrowse;
	private TCTextField txtfFilePath;
	private JCheckBox chkShowHidden;
	private JPanel content;
	protected MainWindow parent;
	private TCLabel lblAbout;
	private JPanel pnlProg;
	private JProgressBar prgProg;
	private JButton btnCancel;
	private JLabel lblProg;
	
	private int progLength = 0;
	
	public FileBrowserPanel(MainWindow parent) {
		super();
		this.parent = parent;
		content = new JPanel();
		this.setViewportView(content);
		content.setLayout(new BorderLayout());
		JPanel topPart = new JPanel();
		FlowLayout f1 = new FlowLayout();
		f1.setAlignment(FlowLayout.LEFT);
		topPart.setLayout(f1);
		content.add(topPart, BorderLayout.NORTH);
		lblFolder = new TCLabel(TCStrings.getStringFor("FBP_LBLFOLDER"));
		txtfFilePath = new TCTextField();
		txtfFilePath.setPreferredSize(new Dimension(300, 20));
		txtfFilePath.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent evt) {
				int iKey = evt.getKeyCode();
				if (iKey == KeyEvent.VK_ENTER) {
					navigateTo(new File(txtfFilePath.getText()));
				}
			}
		});
		btnFolderBrowse = new JButton(TCStrings.getStringFor("FBP_BTNFOLDERBROWSE"));
		btnFolderBrowse.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				doBrowse();
			}
        });
		chkShowHidden = new JCheckBox(TCStrings.getStringFor("FBP_CHKSHOWUPSUPPORTED"));
		chkShowHidden.setSelected(true);
		if (TCPrefs.getPrefValueFor(TCPrefs.PREF_SHOW_UNSUPPORTED_FILES).equals("false")) {
			chkShowHidden.setSelected(false);
		}
		chkShowHidden.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				if (chkShowHidden.isSelected()) {
					TCPrefs.setPrefValue(TCPrefs.PREF_SHOW_UNSUPPORTED_FILES, "true");
				} else {
					TCPrefs.setPrefValue(TCPrefs.PREF_SHOW_UNSUPPORTED_FILES, "false");
				}
				tblFileBrowse.MSG_setHidden(chkShowHidden.isSelected());
			}
        });
		lblAbout = new TCLabel(TCStrings.getStringFor("ABOUT_VIEWER"));
		lblAbout.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e){
				showAbout();
		     }
		} );
		Hashtable<TextAttribute, Object> map = new Hashtable<TextAttribute, Object>();
		map.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);
		map.put(TextAttribute.FOREGROUND, Color.BLUE);
		Font font = lblAbout.getFont();
		font = font.deriveFont(map);
        lblAbout.setFont(font); 
        lblAbout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        topPart.add(lblFolder);
       
        topPart.add(txtfFilePath);
        topPart.add(btnFolderBrowse);
        topPart.add(chkShowHidden);
        topPart.add(lblAbout);
        
        tblFileBrowse = new FileBrowserTable(this);
        content.add(tblFileBrowse, BorderLayout.CENTER);
        pnlProg = new JPanel();
        FlowLayout fl = new FlowLayout();
        fl.setHgap(10);
        fl.setVgap(0);
        fl.setAlignment(FlowLayout.LEFT);
        pnlProg.setLayout(fl);
        pnlProg.setPreferredSize(new Dimension(10, 25));
        prgProg = new JProgressBar();
        prgProg.setPreferredSize(new Dimension(200, 20));
        btnCancel = new JButton(TCStrings.getStringFor("CANCEL"));
        btnCancel.setEnabled(false);
        btnCancel.setMargin(new Insets(2,5,2,5));
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				doCancel();
			}
        });
        lblProg = new JLabel("");
        pnlProg.add(prgProg);
        pnlProg.add(btnCancel);
        pnlProg.add(lblProg);
        content.add(pnlProg, BorderLayout.SOUTH);
        //start off in user's home directory if no prefs
        File oneToGoTo = null;
        File home = new File(System.getProperty("user.home"));
        if (home.exists()) {
        	oneToGoTo = home;
        }
        if (!TCPrefs.getPrefValueFor(TCPrefs.PREF_LAST_OPENED_FOLDER).equals("")) {
        	File oldHome = new File(TCPrefs.getPrefValueFor(TCPrefs.PREF_LAST_OPENED_FOLDER));
        	if (oldHome.exists() && oldHome.isDirectory()) {
        		oneToGoTo = oldHome;
        	}
        }
        //the first time, this is necessary for some reason
        MSG_setInterfaceEnabled(false);
        if (oneToGoTo != null) {
        	navigateTo(oneToGoTo);
        }
	}
	
	protected void MSG_setInterfaceEnabled(boolean val) {
		btnFolderBrowse.setEnabled(val);
		tblFileBrowse.MSG_enableInterface(val);
		txtfFilePath.setEnabled(val);
		chkShowHidden.setEnabled(val);
		btnCancel.setEnabled(!val);
	}
	
	//called by table to set prog length (when initiating)
	protected void MSG_setProgLength(int val) {
		this.prgProg.setMaximum(val);
		this.progLength = val;
	}
	
	protected void MSG_setProgVal(int val) {
		this.prgProg.setValue(val);
		this.lblProg.setText(TCStrings.getStringFor("FBP_READING_FILE") + " " + val + " " + TCStrings.getStringFor("FBP_OF")+ " " + progLength);
	}
	
	protected void MSG_resetProg() {
		this.prgProg.setValue(0);
		this.lblProg.setText("");
	}
	
	protected MainWindow getViewerMain() {
		return parent;
	}
	
	//called by FileBrowserTable when a new folder clicked on
	protected void MSG_tableFolderChosen(File newFolder) {
		navigateTo(newFolder);
	}
	
	//called by FileBrowserTable when a new file clicked on; will be null when non valid files or folders clicked on
	protected void MSG_tableFileChosen(TCFile newFile) {
		parent.MSG_fileBrowserFileChosen(newFile);
	}
	
	private void showAbout() {
		String x = TCStrings.getStringFor("VIEWER_PROGRAM_NAME") + " " + TCStrings.getStringFor("VIEWER_VERSION");
		JOptionPane.showMessageDialog(this, x + "\n" + TCStrings.getStringFor("ABOUT_MESSAGE"), TCStrings.getStringFor("ABOUT_MESSAGE_TITLE"), JOptionPane.INFORMATION_MESSAGE);
	}
	
	private void doCancel() {
		tblFileBrowse.MSG_docancel();
	}
	
	private void doBrowse() {
		int response = parent.folderchooser.showOpenDialog(this);
		if (response != JFileChooser.CANCEL_OPTION) {
			File x = parent.folderchooser.getSelectedFile();
			navigateTo(x);
		}			
	}
	
	private void navigateTo(File x) {
		//needed to fix a strange windows thing
		if (x.getPath().endsWith(":")) {
			x = new File(x.getPath() + File.separator);
		}
		if (x.exists() && x.isDirectory() && x.canRead()) {
			parent.MSG_fileBrowserFolderChosen(x);
			tblFileBrowse.MSG_newFolderChosen(x, this.chkShowHidden.isSelected());
			this.txtfFilePath.setText(x.getPath());
			TCPrefs.setPrefValue(TCPrefs.PREF_LAST_OPENED_FOLDER, x.getPath());
		} else {
			parent.showErrorMessage(TCStrings.getStringFor("FBP_ERROR_NOT_VALID_FOLDER"));
		}
	}

}

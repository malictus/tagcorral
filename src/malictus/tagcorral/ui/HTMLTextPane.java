package malictus.tagcorral.ui;

import java.awt.event.*;
import java.io.*;
import java.nio.charset.Charset;
import javax.swing.*;
import malictus.tagcorral.lib.file.*;

/**
 * An HTMLTextPane is a JEditorPane specific for displaying HTML (and dealing with link clicks), wrapped in a JScrollPane
 */
public class HTMLTextPane extends JScrollPane {
	
	private JEditorPane theTextPane;
	private JPopupMenu popup;
	static private JFileChooser CHOOSE = new JFileChooser();
	private String exportName = "export.html";
	private JMenuItem mnuCopy;
	
	public HTMLTextPane(String initialText) {
		init(initialText, null);
	}
	
	/**
	 * Initiate the HTMLTextPane
	 * @param initialText initial text for the text pane; may be null for empty text pane
	 * @param nameForExport an optional name for the exported file to be used in the Export To HTML option (may be null)
	 */
	public HTMLTextPane(String initialText, String nameForExport) {
		init(initialText, nameForExport);
	}
	
	/**
	 * Retrieval method for the JTextPane itself
	 * @return the JTextPane itself
	 */
	public JEditorPane getTextPane() {
		return theTextPane;
	}
	
	/**
	 * Convenience method for setting JTextpane's text.
	 * @param value the new text for the JTextpane
	 */
	public void setText(String value) {
		theTextPane.setText(value);
		theTextPane.setCaretPosition(0);
	}
	
	/**
	 * Convenience method for JTextpane's text retrieval
	 * @return the text in the JTextpane
	 */
	public String getText() {
		return theTextPane.getText();
	}
	
	public void setExportName(String exportName) {
		this.exportName = exportName;
	}
	
	private void doCopy() {
		theTextPane.copy();
	}
	
	private void doExportHTML() {
		File folder = CHOOSE.getCurrentDirectory();
		File saveFile = new File(folder.getPath() + File.separator + exportName);
		CHOOSE.setSelectedFile(saveFile);
		int response = CHOOSE.showSaveDialog(this);
		if (response == JFileChooser.CANCEL_OPTION) {
			return;
		}
		try {
			File x = CHOOSE.getSelectedFile();
			if (x.exists()) {
				int response2 = JOptionPane.showConfirmDialog(this, TCStrings.getStringFor("OVERWRITE_DIALOG_TEXT"), TCStrings.getStringFor("OVERWRITE_DIALOG_TITLE"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (response2 != JOptionPane.YES_OPTION) {
					return;
				}
				x.delete();
			}
			x.createNewFile();
			TCBaseFile baseFile = new TCBaseFile(x);
			String theText = theTextPane.getText();
			byte[] theBytes = theText.getBytes(Charset.forName("UTF-8"));
			baseFile.appendToFile(theBytes);
		} catch (Exception err) {
			err.printStackTrace();
			JOptionPane.showMessageDialog(this, TCStrings.getStringFor("ERROR_DIALOG_SAVING_FILE"), TCStrings.getStringFor("ERROR_DIALOG_TITLE"), JOptionPane.WARNING_MESSAGE);
		}
	}
	
	private void init(String initialText, String nameForExport) {
		if (nameForExport != null) {
			exportName = nameForExport;
		}
		if (initialText != null) {
			theTextPane = new JTextPane();
			theTextPane.setContentType("text/html");
			theTextPane.setEditable(false);
			theTextPane.setText(initialText);
		} else {
			theTextPane = new JTextPane();
		}
		this.setViewportView(theTextPane);
		//init popup menu
	    popup = new JPopupMenu();
	    mnuCopy = new JMenuItem(TCStrings.getStringFor("MENU_COPY"));
	    JMenuItem mnuExportHTML = new JMenuItem(TCStrings.getStringFor("MENU_SAVE_HTML"));
	    mnuExportHTML.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				doExportHTML();
			}
		});
	    mnuCopy.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				doCopy();
			}
		});
	    popup.add(mnuExportHTML);
	    popup.add(mnuCopy);
	    theTextPane.addMouseListener(new MousePopupListener());
	    CHOOSE.setAcceptAllFileFilterUsed(true);
	    CHOOSE.setMultiSelectionEnabled(false);
	    CHOOSE.setFileSelectionMode(JFileChooser.FILES_ONLY);
	}
	 
	private class MousePopupListener extends MouseAdapter {
	    public void mousePressed(MouseEvent e) {
	    	checkPopup(e);
	    }
	    public void mouseClicked(MouseEvent e) {
	    	checkPopup(e);
	    }
	    public void mouseReleased(MouseEvent e) {
	    	checkPopup(e);
	    }
	    private void checkPopup(MouseEvent e) {
	    	if (e.isPopupTrigger()) {
	    		if (((JEditorPane)e.getComponent()).getSelectedText() != null) {
	    			mnuCopy.setEnabled(true);
	    		} else {
	    			mnuCopy.setEnabled(false);
	    		}
	    		popup.show(theTextPane, e.getX(), e.getY());
	    	}
	    }
	}

}

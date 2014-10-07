package malictus.tagcorral.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

import malictus.tagcorral.lib.file.TCBaseFile;

import java.io.File;
import java.nio.charset.*;

/**
 * A TCTextArea is a JTextArea wrapped in a JScrollPane
 */
public class TCTextArea extends JScrollPane {
	
	private JTextArea theTextArea;
	private JPopupMenu popup;
	JMenuItem mnuCopy;
	JMenuItem mnuCut;
	JMenuItem mnuPaste;
	private Charset savingCharset;
	private String exportName = "export.txt";
	static private JFileChooser CHOOSE = new JFileChooser();
	
	public TCTextArea(String initialText) {
		init(initialText, null);
	}
	
	public TCTextArea(String initialText, String exportName) {
		this.exportName = exportName;
		init(initialText, null);
	}
	
	public TCTextArea(String initialText, Charset charsetForSaving, String exportName) {
		this.exportName = exportName;
		init(initialText, charsetForSaving);
	}
	
	/**
	 * Initiate the TCTextArea
	 * @param initialText initial text for the text area; may be null for empty text area
	 * @param charsetForSaving the charset that should be used when saving this text to file; if null, default UTF-8 will be used
	 */
	public TCTextArea(String initialText, Charset charsetForSaving) {
		init(initialText, charsetForSaving);
	}
	
	public void setSavingCharset(Charset charset) {
		this.savingCharset = charset;
	}
	
	/**
	 * Retrieval method for the JTextArea itself
	 * @return the JTextArea itself
	 */
	public JTextArea getTextArea() {
		return theTextArea;
	}
	
	/**
	 * Convenience method for setting JTextArea's text.
	 * @param value the new text for the JTextArea
	 */
	public void setText(String value) {
		theTextArea.setText(value);
	}
	
	/**
	 * Convenience method for JTextArea's text retrieval
	 * @return the text in the JTextArea
	 */
	public String getText() {
		return theTextArea.getText();
	}
	
	private void doCopy() {
		theTextArea.copy();
	}
	
	private void doCut() {
		theTextArea.cut();
	}
	
	private void doPaste() {
		theTextArea.paste();
	}
	
	public void doExportText() {
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
			String theText = theTextArea.getText();
			byte[] theBytes = theText.getBytes(savingCharset);
			baseFile.appendToFile(theBytes);
		} catch (Exception err) {
			err.printStackTrace();
			JOptionPane.showMessageDialog(this, TCStrings.getStringFor("ERROR_DIALOG_SAVING_FILE"), TCStrings.getStringFor("ERROR_DIALOG_TITLE"), JOptionPane.WARNING_MESSAGE);
		}
	}
	
	private void init(String initialText, Charset charsetForSaving) {
		if (charsetForSaving == null) {
			this.savingCharset = Charset.forName("UTF-8");
		} else {
			this.savingCharset = charsetForSaving;
		}
		if (initialText != null) {
			theTextArea = new JTextArea(initialText);
		} else {
			theTextArea = new JTextArea();
		}
		this.setViewportView(theTextArea);
		//init popup menu
	    popup = new JPopupMenu();
	    mnuCopy = new JMenuItem(TCStrings.getStringFor("MENU_COPY"));
	    mnuCopy.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				doCopy();
			}
		});
	    mnuCut = new JMenuItem(TCStrings.getStringFor("MENU_CUT"));
	    mnuCut.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				doCut();
			}
		});
	    mnuPaste = new JMenuItem(TCStrings.getStringFor("MENU_PASTE"));
	    mnuPaste.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				doPaste();
			}
		});
	    popup.add(mnuCut);
	    popup.add(mnuCopy);
	    popup.add(mnuPaste);
	    JMenuItem mnuExportText = new JMenuItem(TCStrings.getStringFor("MENU_SAVE_TEXTFILE"));
	    mnuExportText.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				doExportText();
			}
		});
	    popup.addSeparator();
	    popup.add(mnuExportText);
	    theTextArea.addMouseListener(new MousePopupListener());
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
	    		if (((JTextArea)e.getComponent()).isEditable()) {
	    			if (((JTextArea)e.getComponent()).getSelectedText() != null) {
	    				mnuCut.setEnabled(true);
	    			} else {
	    				mnuCut.setEnabled(false);
	    			}
    				mnuPaste.setEnabled(true);
    			} else {
    				mnuCut.setEnabled(false);
    				mnuPaste.setEnabled(false);
    			}
	    		if (((JTextArea)e.getComponent()).getSelectedText() != null) {
	    			mnuCopy.setEnabled(true);
	    		} else {
	    			mnuCopy.setEnabled(false);
	    		}
	    		popup.show(e.getComponent(), e.getX(), e.getY());
	    	}
	    }
	}

}

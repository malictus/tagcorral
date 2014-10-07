package malictus.tagcorral.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

public class TCTextField extends JTextField {
	
	private JPopupMenu popup;
	JMenuItem mnuCopy;
	JMenuItem mnuCut;
	JMenuItem mnuPaste;
	
	public TCTextField() {
		super();
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
	    this.addMouseListener(new MousePopupListener());
	}
	
	private void doCopy() {
		this.copy();
	}
	
	private void doCut() {
		this.cut();
	}
	
	private void doPaste() {
		this.paste();
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
	    		if (((TCTextField)e.getComponent()).isEditable()) {
	    			if (((TCTextField)e.getComponent()).getSelectedText() != null) {
	    				mnuCut.setEnabled(true);
	    			} else {
	    				mnuCut.setEnabled(false);
	    			}
    				mnuPaste.setEnabled(true);
    			} else {
    				mnuCut.setEnabled(false);
    				mnuPaste.setEnabled(false);
    			}
	    		if (((TCTextField)e.getComponent()).getSelectedText() != null) {
	    			mnuCopy.setEnabled(true);
	    		} else {
	    			mnuCopy.setEnabled(false);
	    		}
	    		popup.show(e.getComponent(), e.getX(), e.getY());
	    	}
	    }
	}

}

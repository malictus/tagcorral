package malictus.tagcorral.ui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

/**
 * A TCLabel is actually a JTextField, but it's disguised to look like a label so that labels can be copy/pasted easily.
 */
public class TCLabel extends JTextField {
	
	private JPopupMenu popup;
	
	/**
	 * Initialize the TCLabel
	 * @param lblText initial label text
	 */
	public TCLabel(String lblText) {
		super(lblText);
		this.setBorder(null);
		this.setOpaque(false);
		this.setEditable(false); 
		FontMetrics fm = this.getFontMetrics(this.getFont());
    	int height = fm.getHeight();
		//this is needed to make box layout work properly
		this.setMaximumSize(new java.awt.Dimension(10000, height + 6));
		this.setPreferredSize(new java.awt.Dimension(0, height + 6));
		//have to call it here manually to force a possible resize
		this.setText(lblText);
		//init popup menu
	    popup = new JPopupMenu();
	    JMenuItem mnuCopy = new JMenuItem(TCStrings.getStringFor("MENU_COPY"));
	    mnuCopy.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				doCopy();
			}
		});
	    popup.add(mnuCopy);
	    this.addMouseListener(new MousePopupListener());
	}
	
	public TCLabel(String lblText, int fontSize, int style) {
		super(lblText);
		this.setBorder(null);
		this.setOpaque(false);
		this.setEditable(false);
		this.setFont(new Font(this.getFont().getName(), style, fontSize));
		FontMetrics fm = this.getFontMetrics(this.getFont());
    	int height = fm.getHeight();
		//this is needed to make box layout work properly
		this.setMaximumSize(new java.awt.Dimension(10000, height + 6));
		this.setPreferredSize(new java.awt.Dimension(0, height + 6));
		//have to call it here manually to force a possible resize
		this.setText(lblText);
		//init popup menu
	    popup = new JPopupMenu();
	    JMenuItem mnuCopy = new JMenuItem(TCStrings.getStringFor("MENU_COPY"));
	    mnuCopy.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				doCopy();
			}
		});
	    popup.add(mnuCopy);
	    this.addMouseListener(new MousePopupListener());
	}
	
	/**
	 * Override of set text, so that long labels will look correct
	 * @param text new text for the object
	 */
	public void setText(String text) {
		super.setText(text);
		this.setCaretPosition(0);
		FontMetrics fm = this.getFontMetrics(this.getFont());
		int height = fm.getHeight();
		int width = fm.stringWidth(text) + 2;
		this.setPreferredSize( new Dimension(width + 2, height + 6));
	}
	
	/**
	 * Override of set font
	 * @param font new font for the object
	 */
	public void setFont(Font font) {
		super.setFont(font);
		String text = "";
		try {
			text = this.getText();
		} catch (Exception err) {
			text = "";
		}
		this.setCaretPosition(0);
		FontMetrics fm = this.getFontMetrics(this.getFont());
		int height = fm.getHeight();
		int width = fm.stringWidth(text) + 2;
		this.setPreferredSize( new Dimension(width + 2, height + 6));
	}
	
	private void doCopy() {
		this.copy();
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
	    		popup.show(e.getComponent(), e.getX(), e.getY());
	    	}
	    }
	}

}

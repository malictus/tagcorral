package malictus.tagcorral.ui.viewer;

import malictus.tagcorral.lib.chunk.MetaChunk;
import malictus.tagcorral.lib.file.TCFile;
import malictus.tagcorral.ui.TCStrings;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * This panel displays all the metadata tags
 */
public class TagListPanel extends JPanel {
	
	MainWindow parent;
	TagListTable table;
	JPanel pnlTop;
	JLabel lblName;
	JLabel lblErr;
	TCFile fileForErr = null;
	
	public TagListPanel(MainWindow parent) {
		super();
		this.parent = parent;
		table = new TagListTable(this);
		BorderLayout bl = new BorderLayout();
		bl.setHgap(5);
		bl.setVgap(5);
		this.setBorder(new EmptyBorder(5, 5, 5, 5));
		this.setLayout(bl);
		this.add(table, BorderLayout.CENTER);
		lblErr = new JLabel("");
		lblErr.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e){
				showErrDialog();
		     }
		} );
		lblName = new JLabel("");
		pnlTop = new JPanel();
		BorderLayout bl2 = new BorderLayout();
		pnlTop.setLayout(bl2);
		pnlTop.add(lblName, BorderLayout.NORTH);
		pnlTop.add(lblErr, BorderLayout.SOUTH);
		this.add(pnlTop, BorderLayout.NORTH);
	}
	
	//caled by MainWindow when a new file selected; null when no file open
	protected void MSG_mainWindowFileOpened(TCFile file) {
		fileForErr = file;
		table.MSG_mainWindowFileOpened(file);
		lblErr.setText("");
		if (file == null) {
			lblName.setText("");
		} else {
			String text = "<html><body>" + TCStrings.getStringFor("TLP_SELECTED_FILE") + " " + file.getName();
			text = text + "</body></html>";
			lblName.setText(text);
			if (file.getInvalidReasons().size() > 0) {
				lblErr.setText("<html><body><br/><font color=\"blue\"><u>" + TCStrings.getStringFor("TLP_FILE_PROBS") + "</u></font></body></html>");
		        lblErr.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			}
		}
	}
	
	protected void MSG_setInterfaceEnabled(boolean val) {
		table.MSG_setInterfaceEnabled(val);
	}
	
	protected void MSG_tagListTableNewChunk(MetaChunk chunk) {
		parent.MSG_tagListPanelNewChunk(chunk);
	}
	
	private void showErrDialog() {
		if (fileForErr != null) {
			new FileInfoDialog(fileForErr, parent);
		}
	}
	
}

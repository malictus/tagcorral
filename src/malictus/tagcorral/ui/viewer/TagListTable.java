package malictus.tagcorral.ui.viewer;

import java.awt.*;
import java.util.*;
import malictus.tagcorral.lib.chunk.MetaChunk;
import malictus.tagcorral.lib.file.TCFile;
import malictus.tagcorral.ui.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class TagListTable extends TCTable {
	
	TagListPanel parent;
	
	public TagListTable(TagListPanel parent) {
		super();
		this.parent = parent;
		//make the table cells a big bigger than normal for readability
		this.getTable().setRowHeight(18);
		this.getTable().setFont(new Font(this.getTable().getFont().getFontName(), Font.PLAIN, 12));
		this.getTable().setShowHorizontalLines(false);
        this.getTable().setShowVerticalLines(false);
		ListSelectionModel rowSM = this.getTable().getSelectionModel();;
        rowSM.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting()) return;
                newRowSelected();
            }
        });
	}
	
	//caled by MainWindow when a new file selected; null when no file open
	protected void MSG_mainWindowFileOpened(TCFile file) {
		this.setTableData(getHeaders(), file);
	}
	
	protected void MSG_setInterfaceEnabled(boolean val) {
		this.getTable().setEnabled(val);
	}
	
	private String[] getHeaders() {
		String[] headers = new String[1];
		headers[0] = TCStrings.getStringFor("TLP_HEADER_TITLE");
		return headers;
	}
	
	private void setTableData(String[] headers, TCFile file) {
		if (file == null) {
			this.getTable().setModel(new TLPTableModel(headers, new Vector<MetaChunk>()));
		} else {
			this.getTable().setModel(new TLPTableModel(headers, file.getMetadataChunks()));
		}
		int counter = 0;
		while (counter < headers.length) {
			TableColumn column = this.getTable().getColumnModel().getColumn(counter);
			column.setCellRenderer(new TLPCellRenderer());
			counter = counter + 1;
		}
		if (this.getTable().getModel().getRowCount() > 0) {
			SwingUtilities.invokeLater( new Runnable() {
				public void run() {
					getTable().setRowSelectionInterval(0, 0);
				}
			});
		}
	}
	
	private void newRowSelected() {
		if (this.getTable().getSelectedRow() >= 0) {
			Object x = getTable().getModel().getValueAt(this.getTable().getSelectedRow(), 0);
			parent.MSG_tagListTableNewChunk((MetaChunk)x);
		}
	}
	
	private class TLPTableModel extends AbstractTableModel {
		
		private String[] columnNames;
		private Vector<MetaChunk> chunks;
		
		public TLPTableModel(String[] columnNames, Vector<MetaChunk> chunks) {
			super();
			this.columnNames = columnNames;
			this.chunks = chunks;
		}

		public int getColumnCount() {
			return columnNames.length;
		}
		
		public int getRowCount() {
			return chunks.size();
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			return chunks.get(row);
		}
		
	}
	
	private class TLPCellRenderer extends DefaultTableCellRenderer { 
		
		public Component getTableCellRendererComponent(JTable table, Object object, 
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, object, isSelected, hasFocus, row, column);
			//have to set it here in case it gets changed below
			this.setForeground(SystemColor.textText);
			MetaChunk theChunk = (MetaChunk)object;
			setText(UIUtils.getChunkTypeString(theChunk));
			if (!theChunk.chunkIsValid()) {
				this.setForeground(Color.red);
			}
			return this;				
		}
		
	}
	
}

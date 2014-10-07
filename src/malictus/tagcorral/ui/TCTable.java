package malictus.tagcorral.ui;

import javax.swing.*;
import javax.swing.table.*;

/**
 * A noneditable String-only JTable embedded in a scroll pane.
 */
public class TCTable extends JScrollPane {
	
	private JTable theTable;
	
	public TCTable() {
		super();
		theTable = new JTable();
		this.setViewportView(theTable);
		theTable.setFillsViewportHeight(true);
		theTable.getTableHeader().setReorderingAllowed(false);
		theTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		theTable.setModel(new NonEditableTableModel(new String[0], new String[0][0]));
	}
	
	public JTable getTable() {
		return theTable;
	}
	
	public void clearTable() {
		theTable.setModel(new NonEditableTableModel(new String[0], new String[0][0]));
	}
	
	public void setTableData(String[] headers, String[][] data) {	
		theTable.setModel(new NonEditableTableModel(headers, data));
	}
	
	private class NonEditableTableModel extends AbstractTableModel {
		
		private String[] columnNames;
		private String[][] data;
		
		public NonEditableTableModel(String[] columnNames, String[][] data) {
			super();
			this.columnNames = columnNames;
			this.data = data;
		}

		public int getColumnCount() {
			return columnNames.length;
		}
		
		public int getRowCount() {
			return data.length;
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			return data[row][col];
		}
	}

}

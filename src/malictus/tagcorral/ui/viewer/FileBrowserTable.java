package malictus.tagcorral.ui.viewer;

import malictus.tagcorral.lib.TCUtil;
import malictus.tagcorral.lib.file.*;
import malictus.tagcorral.ui.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import javax.swing.table.*;
import java.util.*;
import javax.swing.event.*;

/**
 * This panel displays the currently open folder, and gives info about the files in it
 */
public class FileBrowserTable extends TCTable {
	
	private FileBrowserPanel parent;
	
	//constants so that the columns can easily be rearranged
	private final static int FILETYPE_COLUMN = 0;
	private final static int FILESIZE_COLUMN = 2;
	private final static int TAGCOUNT_COLUMN = 1;
	private final static int FILENAME_COLUMN = 3;
	
	//current panel status variables
	private File currentFolder;
	private boolean showHidden = false;
	private int sortColumn = FILENAME_COLUMN;
	private boolean reverseSort = false;
	private boolean cancelFlag = false;
	
	//used to hold the file objects themselves
	Vector<File> vecFiles = new Vector<File>();
	
	//comparators for the file objects
	Comparator<File> compareFileSize = new FileSizeComparator();
	Comparator<File> compareFileType = new FileTypeComparator();
	Comparator<File> compareTagCount = new TagCountComparator();
	Comparator<File> compareFileName = new FileNameComparator();
	
	//ui components
	private JPopupMenu popupFolder;
	private JPopupMenu popupFile;
	private JMenuItem mnuOpenFolder;
	private JMenuItem mnuOpenFile;
	private JMenuItem mnuRefresh;
	private JMenuItem mnuRefresh2;
	
	private boolean enabled = true;
	
	public FileBrowserTable(FileBrowserPanel parent) {
		super();
		//read prefs, if they exist
		if (!TCPrefs.getPrefValueFor(TCPrefs.PREF_SORT_COLUMN).equals("")) {
			sortColumn = Integer.parseInt(TCPrefs.getPrefValueFor(TCPrefs.PREF_SORT_COLUMN));
		}
		if (TCPrefs.getPrefValueFor(TCPrefs.PREF_REVERSE_SORT).equals("true")) {
			reverseSort = true;
		}
		this.setPreferredSize(new Dimension(200, 175));
		//make the table cells a big bigger than normal for readability
		this.getTable().setRowHeight(18);
		this.getTable().setFont(new Font(this.getTable().getFont().getFontName(), Font.PLAIN, 12));
		this.getTable().setShowHorizontalLines(false);
        this.getTable().setShowVerticalLines(false);
		this.getTable().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e){
				if (SwingUtilities.isLeftMouseButton(e)) {
					if (e.getClickCount() == 2) {
						if (enabled) {	
							doDoubleClick();
						}
			        }
				} else if (SwingUtilities.isRightMouseButton(e)) {
					if (enabled) {
						Point p = e.getPoint();
						int rowNumber = getTable().rowAtPoint(p);
						ListSelectionModel model = getTable().getSelectionModel();
						model.setSelectionInterval( rowNumber, rowNumber );
						showPopup(e.getX(), e.getY());
					}
				}
		     }
		} );
		JTableHeader header = this.getTable().getTableHeader();
	    header.addMouseListener(new ColumnListener(this.getTable()));
		TCSelectionListener listener = new TCSelectionListener();
	    this.getTable().getSelectionModel().addListSelectionListener(listener);
		this.parent = parent;
		popupFolder = new JPopupMenu();
		mnuOpenFolder = new JMenuItem(TCStrings.getStringFor("MENU_OPEN_FOLDER"));
		mnuOpenFolder.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				if (enabled) {
					doOpenFolder();
				}
			}
		});
		popupFolder.add(mnuOpenFolder);
		popupFile = new JPopupMenu();
		mnuOpenFile = new JMenuItem(TCStrings.getStringFor("MENU_SHOW_FILE_INFO"));
		mnuOpenFile.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				//emulate double-click behavior
				if (enabled) {
					doDoubleClick();
				}
			}
		});
		popupFile.add(mnuOpenFile);
		mnuRefresh = new JMenuItem(TCStrings.getStringFor("MENU_REFRESH"));
		mnuRefresh.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				if (enabled) {
					doRefresh();
				}
			}
		});
		mnuRefresh2 = new JMenuItem(TCStrings.getStringFor("MENU_REFRESH"));
		mnuRefresh2.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				if (enabled) {
					doRefresh();
				}
			}
		});
		popupFile.add(mnuRefresh);
		popupFolder.add(mnuRefresh2);
	}
	
	// ********************** MESSAGES RECEIVED **********************/
	//called by the parent component to set a new folder
	protected void MSG_newFolderChosen(File folder, boolean showHidden) {
		this.showHidden = showHidden;
		this.currentFolder = folder;
		reparseFilesFor(folder);	
	}
	
	protected void MSG_enableInterface(boolean val) {
		this.setEnabled(val);
		this.getTable().setEnabled(val);
		enabled = val;
	}
	
	protected void MSG_docancel() {
		cancelFlag = true;
	}
	
	//called by parent component when switching between showing and hiding invalid/unsupported files
	protected void MSG_setHidden(boolean isHidden) {
		this.showHidden = isHidden;
		rebuildTable(showHidden);
	}
	
	// ********************** HELPER METHODS *************************/
	//show the popup menu that applies to whatever is currently selected
	private void showPopup(int x, int y) {
		int row = getTable().getSelectedRow();
		Object obj = getTable().getModel().getValueAt(row, 0);
		File f = null;
		if (obj instanceof File) {
			f = (File)getTable().getModel().getValueAt(row, 0);
		} else {
			f = new File((String)obj);
		}
		if (f.isDirectory()) {
			popupFolder.show(getTable(), x, y);
		} else {
			popupFile.show(getTable(), x, y);
		}
	}
	
	//refresh the window to reflect the current state of the file system
	private void doRefresh() {
		reparseFilesFor(currentFolder);	
	}
	
	//double-click or right-click menu selection (open file info)
	private void doDoubleClick() {
		int row = getTable().getSelectedRow();
		Object x = getTable().getModel().getValueAt(row, 0);
		File f = null;
		if (x instanceof File) {
			f = (File)getTable().getModel().getValueAt(row, 0);
		} else {
			f = new File((String)x);
		}
		if (f.isDirectory()) {
			navigateTo(f);
		} else {
			new FileInfoDialog(f, parent.parent);
		}
	}
	
	private void doOpenFolder() {
		int row = getTable().getSelectedRow();
		Object x = getTable().getModel().getValueAt(row, 0);
		File f = null;
		if (x instanceof File) {
			f = (File)getTable().getModel().getValueAt(row, 0);
		} else {
			f = new File((String)x);
		}
		if (f.isDirectory()) {
			navigateTo(f);
		}
	}
	
	private void navigateTo(File x) {
		if (x.exists() && x.isDirectory() && x.canRead()) {
			parent.MSG_tableFolderChosen(x);
		}
	}
	
	private String[] getHeaders() {
		String[] headers = new String[4];
		headers[FILETYPE_COLUMN] = TCStrings.getStringFor("FBT_HEADER_FILETYPE");
		headers[FILESIZE_COLUMN] = TCStrings.getStringFor("FBT_HEADER_FILESIZE");
		headers[TAGCOUNT_COLUMN] = TCStrings.getStringFor("FBT_HEADER_TAGCOUNT");
		headers[FILENAME_COLUMN] = TCStrings.getStringFor("FBT_HEADER_FILENAME");
		return headers;
	}
	
	private void newRowSelected() {
		if (this.getTable().getSelectedRow() >= 0) {
			Object x = getTable().getModel().getValueAt(this.getTable().getSelectedRow(), 0);
			if (!(x instanceof TCFile)) {
				parent.MSG_tableFileChosen(null);
			} else {
				parent.MSG_tableFileChosen((TCFile)x);
			}
		}
	}
	
	private void setInterfaceEnabled(boolean val) {
		parent.parent.MSG_setInterfaceEnabled(val);
	}
	
	private void reparseFilesFor(File folder) {
		//turn off the interface until this task completes
		setInterfaceEnabled(false);
		final File FOLDER = folder;
		Runnable q = new Runnable() {
            public void run() {
            	parseNewFolder(FOLDER);
            }
        };
        Thread t = new Thread(q);
        t.start();
	}
	
	//this should always be done in a separate thread
	private void parseNewFolder(File folder) {
		File[] files = folder.listFiles();
		//prep file array
		vecFiles = new Vector<File>();
		int counter = 0;
		parent.MSG_setProgLength(files.length);
		while ((counter < files.length) && !cancelFlag) {
			parent.MSG_setProgVal(counter);
			File x = files[counter];
			if (x.isDirectory() && (x.listFiles() == null)) {
				//special case that applies to shortcut folders like C:\Users\xxx\My Documents
			} else if (x.isFile() && (x.getName().endsWith(".lnk"))) {
				//always filter out all shortcuts				
			} else {
				if (x.isDirectory()) {
					vecFiles.add(x);
				} else {
					TCBaseFile base = null;
					TCFile enhancedFile = null;
					try {
						base = new TCBaseFile(x);
					} catch (IOException err) {
						//file is directory, or something else strange
						base = null;
					}
					if (base != null) {
						//attempt to create enhanced file objects
						try {
							if (MainWindow.supportedFileTypes.contains(TCFileGuesser.guessFileType(base))) {
								enhancedFile = TCFileFactory.createFileFor(base);
							}
						} catch (IOException err) {
							enhancedFile = null;
						}
					}
					if (enhancedFile != null) {
						vecFiles.add(enhancedFile);
					} else {
						vecFiles.add(x);
					}
				}
			}
			counter = counter + 1;
		}
		cancelFlag = false;
		parent.MSG_resetProg();
		//turn on interface
		SwingUtilities.invokeLater( new Runnable() {
			public void run() {
				cancelFlag = false;
				setInterfaceEnabled(true);
				rebuildTable(showHidden);
			}
		});
	}

	private void rebuildTable(boolean showHidden) {
		//get currently selected row so we can re-select it at the end
		File selFile = null;
		boolean topOne = false;
		if (this.getTable().getSelectedRow() > 0) {
			Object x = getTable().getModel().getValueAt(this.getTable().getSelectedRow(), 0);
			if (x instanceof File) {
				selFile = (File)x;
			}
		}
		if (this.getTable().getSelectedRow() == 0) {
			topOne = true;
		}
		if (this.sortColumn == FILENAME_COLUMN) {	
			Collections.sort(vecFiles, compareFileName);
		} else if (this.sortColumn == FILESIZE_COLUMN) { 
			Collections.sort(vecFiles, compareFileSize);
		} else if (this.sortColumn == FILETYPE_COLUMN) { 
			Collections.sort(vecFiles, compareFileType);
		} else if (this.sortColumn == TAGCOUNT_COLUMN) { 
			Collections.sort(vecFiles, compareTagCount);
		}
		//reverse order if applicable
		if (reverseSort) {
			Collections.reverse(vecFiles);
		}
		String[] headers = getHeaders();
		Vector<File> all = new Vector<File>();
		int counter = 0;
		while (counter < vecFiles.size()) {
			File fil = vecFiles.get(counter);
			if ((this.showHidden) || (fil.isDirectory()) || (fil instanceof TCFile)) {
				all.add(fil);
			}
			counter = counter + 1;
		}
		//do different for empty folders (to ensure one-up links still show)
		if (all.size() == 0) {
			this.getTable().setModel(new FileBrowserTableModel(headers, (this.parent.parent.getCurrentFolder().getParent() != null)));
		} else {
			this.getTable().setModel(new FileBrowserTableModel(headers, all));
		}
		counter = 0;
		while (counter < headers.length) {
			TableColumn column = this.getTable().getColumnModel().getColumn(counter);
			column.setCellRenderer(new FBTCellRenderer());
			if (counter == sortColumn) {
				String arrow = " &darr;";
				if (reverseSort) {
					arrow = " &uarr;";
				}
				column.setHeaderValue("<html><b>" + column.getHeaderValue() + arrow + "</b></html>");
			}
			counter = counter + 1;
		}		
		
		TableColumn col = this.getTable().getColumnModel().getColumn(FILESIZE_COLUMN);
		col.setPreferredWidth(87);
		col.setMaxWidth(500);	//needed to make it work correctly
		col = this.getTable().getColumnModel().getColumn(FILETYPE_COLUMN);
		col.setPreferredWidth(115);
		col.setMaxWidth(500);	//needed to make it work correctly
		col = this.getTable().getColumnModel().getColumn(TAGCOUNT_COLUMN);
		col.setPreferredWidth(60);
		col.setMaxWidth(500);	//needed to make it work correctly
		this.getTable().setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);	
		
		//get selected row from before
		if (topOne) {
			getTable().getSelectionModel().setSelectionInterval(0, 0);
		} else if (selFile != null) {
			counter = 0;
			while (counter < getTable().getRowCount()) {
				Object obj = getTable().getModel().getValueAt(counter, 0);
				if (obj instanceof File) {
					File objF = (File)obj;
					if (objF.getPath().equals(selFile.getPath())) {
						//select this one
						getTable().getSelectionModel().setSelectionInterval(counter, counter);
					}
				}
				counter = counter + 1;
			}
		}
	}
	
	/****************** Listeners ***************/
	//handle resorting behaviors based on column clicks
	private class ColumnListener extends MouseAdapter {
		
	    protected JTable table;

	    public ColumnListener(JTable t) {
	    	table = t;
	    }

	    public void mouseClicked(MouseEvent e) {
	    	TableColumnModel colModel = table.getColumnModel();
	    	int columnModelIndex = colModel.getColumnIndexAtX(e.getX());
	    	int modelIndex = colModel.getColumn(columnModelIndex).getModelIndex();
	    	if (modelIndex < 0) {
	    		return;
	    	}
	    	if (sortColumn == modelIndex) {
	    		reverseSort = !reverseSort;
	    	} else {
	    		sortColumn = modelIndex;
	    	}
	    	TCPrefs.setPrefValue(TCPrefs.PREF_SORT_COLUMN, "" + sortColumn);
	    	if (reverseSort) {
	    		TCPrefs.setPrefValue(TCPrefs.PREF_REVERSE_SORT, "true");
	    	} else {
	    		TCPrefs.setPrefValue(TCPrefs.PREF_REVERSE_SORT, "false");
	    	}
	    	rebuildTable(showHidden);
	    }
	}
	
	//handles row selection behaviors
	private class TCSelectionListener implements ListSelectionListener {
		
		public void valueChanged(ListSelectionEvent e) {
			if (e.getValueIsAdjusting()) {
				return;
			}
			newRowSelected();	    
		}
		
	}
	
	/********************** Private Classes ******************/
	//table model
	private class FileBrowserTableModel extends AbstractTableModel {
		
		private String[] columnNames;
		private Vector<File> files;
		boolean hasParent = false;
		
		public FileBrowserTableModel(String[] columnNames, Vector<File> files) {
			super();
			this.columnNames = columnNames;
			this.files = files;
			hasParent = false;
			if ((files != null) && (files.size() > 0)) {
				if (files.get(0).getParentFile().getParentFile() != null) {
					hasParent = true;
				} 
			}
		}
		
		//alternate constructor for empty folders (to ensure up one links show up)
		public FileBrowserTableModel(String[] columnNames, boolean hasParent) {
			super();
			this.columnNames = columnNames;
			this.files = new Vector<File>();
			this.hasParent = hasParent;
		}

		public int getColumnCount() {
			return columnNames.length;
		}
		
		public int getRowCount() {
			if ((files == null) && (hasParent)) {
				return 1;
			} else if (files == null) {
				return 0;
			}
			if (hasParent) {
				return files.size() + 1;
			} else {
				return files.size();
			}
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}

		public Object getValueAt(int row, int col) {
			if (row < 0) {
				return "";
			}
			if (hasParent && row == 0) {
				//return parent file as string to differentiate it
				if (parent.getViewerMain().getCurrentFolder().getParentFile() != null) {
					return parent.getViewerMain().getCurrentFolder().getParent();
				} else {
					return "";		//shouldn't happen, but just as a double check
				}
			}
			if (hasParent) {
				return files.get(row - 1);
			} else {
				return files.get(row);	
			}
		}
		
	}
	
	//cell renderer
	private class FBTCellRenderer extends DefaultTableCellRenderer { 
		
		public Component getTableCellRendererComponent(JTable table, Object object, 
				boolean isSelected, boolean hasFocus, int row, int column) {
			super.getTableCellRendererComponent(table, object, isSelected, hasFocus, row, column);
			//have to set it here in case it gets changed below
			this.setForeground(SystemColor.textText);
			if (object instanceof String) {
				//this is the one level up link
				String up = (String)object;
				if (up.equals("")) {
					//top level
					setText("");
				} else {
					if (column == FILENAME_COLUMN) {
						File x = new File(up);
						FileSystemView view = FileSystemView.getFileSystemView();    
						Icon icon = view.getSystemIcon(x); 
						setIcon(icon);
						setText(TCStrings.getStringFor("FBT_UP_ONE"));
					} else {
						setText("");
					}
				}
			} else if (object instanceof TCFile) {
				TCFile theFile = (TCFile)object;
				if (column == FILESIZE_COLUMN) {
					if (theFile.isFile()) {
						setText(TCUtil.stringForBytes(theFile.length()));
					} else {
						setText("");
					}
				} else if (column == FILETYPE_COLUMN) {
					setText(UIUtils.getFileTypeString((TCFile)object));
				} else if (column == TAGCOUNT_COLUMN) {
					setText("" + theFile.getMetadataChunks().size());
				} else if (column == FILENAME_COLUMN) {
					FileSystemView view = FileSystemView.getFileSystemView();    
					try {
						Icon icon = view.getSystemIcon(theFile); 
						setIcon(icon);
					} catch (Exception err) {
						//sometimes errors happen here; we can ignore them
					}
					//set color red if either file or chunk has problems
					if (!theFile.fileIsValid()) {
						this.setForeground(Color.red);
					} else {
						this.setForeground(SystemColor.textText);
						int counter = 0;
						while (counter < theFile.getMetadataChunks().size()) {
							if (!theFile.getMetadataChunks().get(counter).chunkIsValid()) {
								this.setForeground(Color.red);
								break;
							}
							counter = counter + 1;
						}
					}
					setText(theFile.getName());
				} else {
					setText("");
				}
			} else if ((object instanceof File)) {
				File theFile = (File)object;
				if (column == FILESIZE_COLUMN) {
					if (theFile.isFile()) {
						setText(TCUtil.stringForBytes(theFile.length()));
					} else {
						setText("");
					}
				} else if (column == FILENAME_COLUMN) {
					FileSystemView view = FileSystemView.getFileSystemView();    
					try {
						Icon icon = view.getSystemIcon(theFile); 
						setIcon(icon);
					} catch (Exception err) {
						//sometimes errors happen here; we can ignore them
					}
					setText(theFile.getName());
					if (theFile.isFile()) {
						this.setForeground(SystemColor.textInactiveText);
					}
				} else {
					setText("");
				}
			} else {
				setText("");
				setIcon(null);
			}
			return this;				
		}
	}
	
	/****************************** COMPARATORS *********************/
	private class FileSizeComparator implements Comparator<File> {

	    public int compare(File filea, File fileb) {
	    	if (filea.isDirectory() && (!fileb.isDirectory())) {
	        	return -1;
	        }
	        if ((!filea.isDirectory()) && fileb.isDirectory()) {
	        	return 1;
	        }
	    	if (filea.isDirectory() && fileb.isDirectory()) {
	    		return filea.getName().compareToIgnoreCase(fileb.getName());
	        }
	        if (filea.length() > fileb.length()) {
	        	return -1;
	        } else if (filea.length() < fileb.length()) {
	        	return 1;
	        } else {
	        	return filea.getName().compareToIgnoreCase(fileb.getName());
	        }
	    }
	    
	}
	
	private class FileNameComparator implements Comparator<File> {

	    public int compare(File filea, File fileb) {
	    	if (filea.isDirectory() && (!fileb.isDirectory())) {
	        	return -1;
	        }
	        if ((!filea.isDirectory()) && fileb.isDirectory()) {
	        	return 1;
	        }
	        return filea.getName().compareToIgnoreCase(fileb.getName());
	    }
	    
	}
	
	private class FileTypeComparator implements Comparator<File> {

	    public int compare(File filea, File fileb) {
	    	//one dir and the other not
	    	if (filea.isDirectory() && (!fileb.isDirectory())) {
	        	return -1;
	        }
	        if ((!filea.isDirectory()) && fileb.isDirectory()) {
	        	return 1;
	        }
	        //both dirs
	    	if (filea.isDirectory() && fileb.isDirectory()) {
	    		return filea.getName().compareToIgnoreCase(fileb.getName());
	        }
	    	//one known file and the other not
	    	if ((filea instanceof TCFile) && (!(fileb instanceof TCFile))) {
	    		return -1;
	    	}
	    	if ((fileb instanceof TCFile) && (!(filea instanceof TCFile))) {
	    		return 1;
	    	}
	    	//neither file known
	    	if ((!(filea instanceof TCFile)) && (!(fileb instanceof TCFile))) {
	    		return filea.getName().compareToIgnoreCase(fileb.getName());
	    	}
	    	//both files known
	    	TCFile tA = (TCFile)filea;
	    	TCFile tB = (TCFile)fileb;
	    	int ret = UIUtils.getFileTypeString(tA).compareToIgnoreCase(UIUtils.getFileTypeString(tB)); 
	    	if (ret != 0) {
	    		return ret;
	    	} else {
	    		return filea.getName().compareToIgnoreCase(fileb.getName());
	    	}
	    }
	    
	}
	
	private class TagCountComparator implements Comparator<File> {

	    public int compare(File filea, File fileb) {
	    	//one dir and the other not
	    	if (filea.isDirectory() && (!fileb.isDirectory())) {
	        	return -1;
	        }
	        if ((!filea.isDirectory()) && fileb.isDirectory()) {
	        	return 1;
	        }
	        //both dirs
	    	if (filea.isDirectory() && fileb.isDirectory()) {
	    		return filea.getName().compareToIgnoreCase(fileb.getName());
	        }
	    	//one known file and the other not
	    	if ((filea instanceof TCFile) && (!(fileb instanceof TCFile))) {
	    		return -1;
	    	}
	    	if ((fileb instanceof TCFile) && (!(filea instanceof TCFile))) {
	    		return 1;
	    	}
	    	//neither file known
	    	if ((!(filea instanceof TCFile)) && (!(fileb instanceof TCFile))) {
	    		return filea.getName().compareToIgnoreCase(fileb.getName());
	    	}
	    	//both files known
	    	TCFile tA = (TCFile)filea;
	    	TCFile tB = (TCFile)fileb;
	    	int a = tA.getMetadataChunks().size();
	    	int b = tB.getMetadataChunks().size();
	    	if (a > b) {
	    		return -1;
	    	}
	    	if (a < b) {
	    		return 1;
	    	}
	    	return filea.getName().compareToIgnoreCase(fileb.getName());
	    }
	    
	}
	
}

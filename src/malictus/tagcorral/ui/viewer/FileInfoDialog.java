package malictus.tagcorral.ui.viewer;

import javax.swing.*;
import malictus.tagcorral.lib.TCUtil;
import malictus.tagcorral.ui.*;
import malictus.tagcorral.lib.chunk.MetaChunk;
import malictus.tagcorral.lib.file.*;
import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.*;

public class FileInfoDialog extends JDialog {
	
	private JPanel contentPane = null;
	private HTMLTextPane txtpContent = null;
	
	public FileInfoDialog(File theFile, MainWindow parentWindow) {
		super();
		try {
			java.net.URL myurl = this.getClass().getResource("/malictus/tagcorral/ui/resources/logo.gif");
			Image im = Toolkit.getDefaultToolkit().getImage(myurl);
			this.setIconImage(im);
		} catch (Exception err) { }
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setResizable(true);
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.setSize(700, 400);
        this.setTitle(TCStrings.getStringFor("FID_DIALOG_TITLE") + " " + theFile.getName());
        contentPane = new JPanel();
        txtpContent = new HTMLTextPane("", theFile.getName() + "_" + TCStrings.getStringFor("INFO") + ".html");
        contentPane.setLayout(new BorderLayout());
        contentPane.add(txtpContent, BorderLayout.CENTER);
        this.setContentPane(contentPane);
        this.setLocation(parentWindow.getX() + 20, parentWindow.getY() + 20);
        String contentString = "<html><body><font face = 'sans-serif'>";
        contentString = contentString + "<b>" + TCStrings.getStringFor("FID_FILENAME") + "</b> " + theFile.getName() + "<br/>";
        contentString = contentString + "<b>" + TCStrings.getStringFor("FID_FILEPATH") + "</b> " + theFile.getPath() + "<br/>";
        contentString = contentString + "<b>" + TCStrings.getStringFor("FID_FILESIZE") + "</b> " + TCUtil.stringForBytes(theFile.length()) + "<br/>";
        if (theFile instanceof TCFile) {
        	TCFile tcFile = (TCFile)theFile;
        	contentString = contentString + "<b>" + TCStrings.getStringFor("FID_FILETYPE") + "</b> " + UIUtils.getFileTypeString(tcFile) + "<br/>";
        	contentString = contentString + "<b>" + TCStrings.getStringFor("FID_TAGNUM") + "</b> " + tcFile.getMetadataChunks().size() + "<br/>";
        	boolean fileProbs = false;
        	boolean chunkProbs = false;
        	if (!tcFile.fileIsValid()) {
        		fileProbs = true;
        	}
    		int counter = 0;
    		while (counter < tcFile.getMetadataChunks().size()) {
    			if (!tcFile.getMetadataChunks().get(counter).chunkIsValid()) {
    				chunkProbs = true;
    				break;
    			}
    			counter = counter + 1;
    		}
        	if (!fileProbs && !chunkProbs) {
        		contentString = contentString + "<br/><b>" + TCStrings.getStringFor("FID_NOISSUES") + "</b><br/>";
        	} else {
        		if (fileProbs) {
        			counter = 0;
        			contentString = contentString + "<br/><b><font color='red'>" + TCStrings.getStringFor("FID_GENERAL_FILE_ISSUES") + "</font></b>";
        			contentString = contentString + "<ul>";
        			while (counter < tcFile.getInvalidReasons().size()) {
        				contentString = contentString + "<li>";
        				contentString = contentString + TCStrings.getStringFor(tcFile.getInvalidReasons().get(counter));
        				contentString = contentString + "</li>";
        				counter = counter + 1;
        			}
        			contentString = contentString + "</ul>";
        		}
        		if (chunkProbs) {
        			counter = 0;
        			contentString = contentString + "<br/><b><font color='red'>" + TCStrings.getStringFor("FID_CHUNK_ISSUES") + "</font></b>";
        			contentString = contentString + "<ul>";
        			while (counter < tcFile.getMetadataChunks().size()) {
        				MetaChunk chunk = tcFile.getMetadataChunks().get(counter);
        				int subcounter = 0;
        				while (subcounter < chunk.getInvalidReasons().size()) {
        					contentString = contentString + "<li>";
        					contentString = contentString + "<b>" + UIUtils.getChunkTypeString(chunk) + " [start " + chunk.getStartByte() + "] : </b>";
        		
        					contentString = contentString + TCStrings.getStringFor(chunk.getInvalidReasons().get(subcounter));
            				contentString = contentString + "</li>";
        					subcounter = subcounter + 1;
        				}
        				counter = counter + 1;
        			}
        			contentString = contentString + "</ul>";
        		}
        		
        	}
        } else {
        	contentString = contentString + "<br/>" + TCStrings.getStringFor("FID_UNSUPPORTED") + "<br/>";
        }
        contentString = contentString + "</font></body></html>";
        txtpContent.setText(contentString);
        
        this.setVisible(true);
	}

}

package malictus.tagcorral.ui;

import java.awt.*;
import javax.swing.*;
import malictus.tagcorral.lib.chunk.MetaChunk;
import malictus.tagcorral.lib.file.TCFile;
import malictus.tagcorral.lib.gif.GIFComment;
import malictus.tagcorral.lib.gif.GIFFile;
import malictus.tagcorral.lib.png.PNGFile;
import malictus.tagcorral.lib.png.TIMEChunk;
import malictus.tagcorral.lib.png.TextChunk;
import malictus.tagcorral.lib.riff.*;

/**
 * A collection of general purpose UI utilities
 */
public class UIUtils {
	
	private UIUtils() {}
	
	public static void centerWindow(JFrame window) {
		 //center window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = window.getSize();
        if (frameSize.height > screenSize.height) {
        	frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
        	frameSize.width = screenSize.width;
        }
        window.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
	}
	
	public static String getFileTypeString(TCFile file) {
		if (file instanceof RIFFFile) {
			RIFFFile ri = (RIFFFile)file;
			if (ri.getRIFFFileType().toUpperCase().equals("WAVE")) {
				return TCStrings.getStringFor("FILE_WAVE");
			} else if (ri.getRIFFFileType().toUpperCase().equals("AVI ")) {
				return TCStrings.getStringFor("FILE_AVI");
			} else {
				return TCStrings.getStringFor("FILE_RIFF");
			}
		} else if (file instanceof GIFFile) {
			return TCStrings.getStringFor("FILE_GIF");
		} else if (file instanceof PNGFile) {
			return TCStrings.getStringFor("FILE_PNG");
		}
		return "";
	}
	
	public static String getChunkTypeString(MetaChunk chunk) {
		if (chunk instanceof TextChunk) {
			TextChunk x = (TextChunk)chunk;
			return TCStrings.getStringFor("CHUNK_PNG_" + x.getChunkType());
		} else if (chunk instanceof INFOChunk) {
			INFOChunk info = (INFOChunk)chunk;
			if (info.isExtended()) {
				return TCStrings.getStringFor("CHUNK_EXTENDED_RIFFINFO") + " -- " +
					((INFOChunk)chunk).getChunkName() + " (" +
					TCStrings.getStringFor("INFOCHUNK_" + ((INFOChunk)chunk).getChunkName()) + ")";
			} else {
				return TCStrings.getStringFor("CHUNK_RIFFINFO") + " -- " +
					((INFOChunk)chunk).getChunkName() + " (" +
					TCStrings.getStringFor("INFOCHUNK_" + ((INFOChunk)chunk).getChunkName()) + ")";	
			}
			
		} else if (chunk instanceof GIFComment) {
			return TCStrings.getStringFor("CHUNK_GIF_COMMENT");
		} else if (chunk instanceof TIMEChunk) {
			return TCStrings.getStringFor("CHUNK_PNG_TIME");
		} else if (chunk instanceof IXMLChunk) {
			return TCStrings.getStringFor("CHUNK_IXML");
		} else if (chunk instanceof LINKChunk) {
			return TCStrings.getStringFor("CHUNK_LINK");
		} else if (chunk instanceof AXMLChunk) {
			return TCStrings.getStringFor("CHUNK_AXML");
		} else if (chunk instanceof RIFFXMPChunk) {
			return TCStrings.getStringFor("CHUNK_RIFFXMP");
		}
		return "";
	}
	
}

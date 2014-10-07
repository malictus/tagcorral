package malictus.tagcorral.lib.file;

import java.io.*;

/**
 * Standalone class used to determine a file's type (without looking at the file extension).
 */
public class TCFileGuesser {
	
	public static final int FILETYPE_INVALID = 0;
	public static final int FILETYPE_GIF = 1;
	public static final int FILETYPE_PNG = 2;
	public static final int FILETYPE_RIFF = 3;
	
	private TCFileGuesser() {}
	
	public static int guessFileType(TCBaseFile file) {
		TCRaf raf = null;
		int fileType = FILETYPE_INVALID;
		try {
			raf = new TCRaf(file, "r");
			raf.seek(0);
			byte[] front = new byte[32];
			raf.read(front);
			raf.close();
			String ascii = new String(front, "US-ASCII");
			//PNG test
			if ( (front[0] == -119) && (front[1] == 80) && (front[2] == 78) && (front[3] == 71) &&
					(front[4] == 13) && (front[5] == 10) && (front[6] == 26) && (front[7] == 10)) {
				return FILETYPE_PNG;
			}
			//GIF test
			if (ascii.startsWith("GIF87a") || ascii.startsWith("GIF89a")) {
				return FILETYPE_GIF;
			}
			//RIFF test
			if (ascii.startsWith("RIFF")) {
				return FILETYPE_RIFF;
			}
			return fileType;
		} catch (IOException err) {
			if (raf != null) {
				try {raf.close();} catch (IOException foo) {}
			}
			return FILETYPE_INVALID;
		}
	}
	
}

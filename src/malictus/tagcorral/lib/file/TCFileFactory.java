package malictus.tagcorral.lib.file;

import java.io.*;
import malictus.tagcorral.lib.gif.*;
import malictus.tagcorral.lib.png.*;
import malictus.tagcorral.lib.riff.*;

/**
 * This class creates instances of TCFile objects based on TCBaseFile objects.
 */
public class TCFileFactory {

	private TCFileFactory() {}
	
	public static TCFile createFileFor(TCBaseFile x) throws IOException {
		int type = TCFileGuesser.guessFileType(x);
		if (type == TCFileGuesser.FILETYPE_GIF) {
			GIFFile g = new GIFFile(x.getPath());
			return g;
		} else if (type == TCFileGuesser.FILETYPE_PNG) {
			PNGFile p = new PNGFile(x.getPath());
			return p;
		} else if (type == TCFileGuesser.FILETYPE_RIFF) {
			RIFFFile p = new RIFFFile(x.getPath());
			return p;
		}
		throw new IOException("File type not found");
	}
	
}

package malictus.tagcorral.lib;

import javax.swing.*;
import java.io.*;

/**
 * Test the charset detection utility.
 */
public class CharsetTester {

	private CharsetTester() {}
	
	public static void main(String[] args) {
		JFileChooser choose = new JFileChooser();
		int s = choose.showOpenDialog(null);
		if (s == JFileChooser.CANCEL_OPTION) {
			return;
		}
		File x = choose.getSelectedFile();
		if (x.length() != ((int)x.length())) {
			System.out.println("too big!");
		}
		try {
			RandomAccessFile rafSource = new RandomAccessFile(x, "r");
			byte[] buf = new byte[(int)x.length()];
			rafSource.read(buf);
			rafSource.close();
			CharsetInfo ci = TCUtil.getCharsetFor(buf);
			System.out.println("File " + x.getPath() + " read ");
			System.out.println("Charset Info: BOM: " + ci.containsBOM());
			System.out.println("Charset Info: Charset Detected: " + ci.getCharset().name());
			System.out.println("Charset Info: Errors Detected: " + ci.hasEncodingErrors());
			String theString = TCUtil.convertToString(buf, ci.getCharset());
			System.out.println(theString);
		} catch (Exception err) {
			err.printStackTrace();
		}
	}
	
}

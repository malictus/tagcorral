package malictus.tagcorral.lib;

import java.nio.charset.Charset;

/**
 * A class for storing information about a given byte chunk's charset.
 */
public class CharsetInfo {
	
	private Charset theCharset = null;
	private boolean containsBOM = false;
	//true for XML chunks that contains incorrect charset encoding specifications, files with BOM but incorrect encodings, etc.
	private boolean hasEncodingErrors = false;

	public CharsetInfo(Charset theCharset, boolean containsBOM, boolean hasEncodingErrors) {
		this.theCharset = theCharset;
		this.containsBOM = containsBOM;
		this.hasEncodingErrors = hasEncodingErrors;
	}
	
	public CharsetInfo() {}
	
	public boolean containsBOM() {
		return containsBOM;
	}
	
	public boolean hasEncodingErrors() {
		return hasEncodingErrors;
	}
	
	//null = unknown charset
	public Charset getCharset() {
		return theCharset;
	}

}

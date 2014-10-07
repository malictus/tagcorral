package malictus.tagcorral.lib.chunk;

import java.io.IOException;
import malictus.tagcorral.lib.*;
import malictus.tagcorral.lib.file.*;

/**
 * A PlainTextChunk is any metadata chunk that consists of a single, regular block of plain text of unspecified of variable encoding.
 */
public abstract class PlainTextChunk extends MetaChunk {
	
	private String theText = "";
	private CharsetInfo charsetInfo = new CharsetInfo(null, false, true);

	public PlainTextChunk(TCFile parentFile, long startByte, long length, TCRaf raf) throws IOException {
		super(parentFile, startByte, length);
		if (length != (int)length) {
			//chunk is too long to create a byte array (will still display first part of it)
			this.addInvalidReason(InvalidReasons.INVALID_CHUNK_TOO_LONG_FOR_STRING);
		}
		raf.seek(startByte);
		byte[] thearray = new byte[(int)length];
		raf.read(thearray);
		charsetInfo = TCUtil.getCharsetFor(thearray);
		if (charsetInfo.getCharset() == null) {
			this.addInvalidReason(InvalidReasons.INVALID_CHUNK_UNKNOWN_ENCODING);
			return;
		}
		if (charsetInfo.hasEncodingErrors()) {
			this.addInvalidReason(InvalidReasons.INVALID_CHUNK_ENCODING_ERRORS);
		}
		if (charsetInfo.containsBOM() && charsetInfo.getCharset().name().equals("UTF-8")) {
			this.addInvalidReason(InvalidReasons.INVALID_CHUNK_UTF8_WITH_BOM);
		}
		theText = TCUtil.convertToString(thearray, charsetInfo.getCharset());
	}
	
	public String getChunkText() {
		return theText;
	}
	
	public CharsetInfo getCharsetInfo() {
		return charsetInfo;
	}
	
}

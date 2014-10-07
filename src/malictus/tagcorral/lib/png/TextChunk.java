package malictus.tagcorral.lib.png;

import java.io.*;
import java.nio.charset.Charset;

import malictus.tagcorral.lib.file.*;
import malictus.tagcorral.lib.chunk.*;
import malictus.tagcorral.lib.*;

/**
 * Chunk to represent a iTXt/zTXt/tEXt chunk in a PNG file.
 */
public class TextChunk extends MetaChunk {
	
	private PNGFileChunk parentChunk;
	private String keyword;
	private String value = "";
	private String chunkType;
	private String languageTag = "";
	private String translatedKeyword = "";
	private boolean compressed = false;
	
	/**
	 * TextChunk chunk creator method.
	 * 
	 * @param parentFile the parent file that this chunk is associated with
	 * @param startByte the start of the chunk, in bytes
	 * @param length the length of the chunk, in bytes.
	 * @param parentChunk the PNGFileChunk that this chunk is a part of
	 * @param raf a pointer to the file, can be in any byte position
	 * @throws IOException if file is invalid, or if this chunk can't be parsed at all
	 */
	public TextChunk(TCFile parentFile, long startByte, long length, PNGFileChunk parentChunk, TCRaf raf) throws IOException {
		super(parentFile, startByte, length);
		this.parentChunk = parentChunk;
		if (parentFile.length() < (startByte + length)) {
			throw new IOException("Chunk does not have enough bytes");
		}
		raf.seek(startByte + 4);
		chunkType = raf.readFourCC();
		if (! (chunkType.equals("iTXt") || chunkType.equals("zTXt") || chunkType.equals("tEXt"))) {
			throw new IOException("Chunk has incorrect FourCC");
		}
		keyword = raf.readNullTerminatedString("ISO-8859-1", startByte + length, true);
		byte[] verify = keyword.getBytes("ISO-8859-1");
		if (!TCUtil.encodingIsCorrect(verify, "ISO-8859-1")) {
			this.addInvalidReason(InvalidReasons.INVALID_CHUNK_PNGTEXT_BAD_KEYWORD);
		}
		if (keyword.length() > 79) {
			this.addInvalidReason(InvalidReasons.INVALID_CHUNK_PNGTEXT_LONG_KEYWORD);
		}
		if (!keyword.trim().equals(keyword)) {
			this.addInvalidReason(InvalidReasons.INVALID_CHUNK_PNGTEXT_KEYWORD_TRIM);
		}
		if (keyword.contains("" + '\u00A0')) {
			this.addInvalidReason(InvalidReasons.INVALID_CHUNK_PNGTEXT_KEYWORD_NONBREAKING_SPACE);
		}
		
		if (chunkType.equals("tEXt")) {
			byte[] val = new byte[(int)(this.getLength() - (raf.getFilePointer() - this.getStartByte()) - 4)];
			raf.read(val);
			value = new String(val, "ISO-8859-1"); 
			if (!TCUtil.encodingIsCorrect(val, "ISO-8859-1")) {
				this.addInvalidReason(InvalidReasons.INVALID_CHUNK_PNGTEXT_TEXT_BAD_VALUE);
			}
			compressed = false;
		} else if (chunkType.equals("zTXt")) {
			if (raf.read1ByteInt(false) != 0) {
				//unknown compression type
				this.addInvalidReason(InvalidReasons.INVALID_CHUNK_PNGTEXT_UNKNOWN_COMPRESSION);
			}
			byte[] val = new byte[(int)(this.getLength() - (raf.getFilePointer() - this.getStartByte()) - 4)];
			raf.read(val);
			byte[] result = TCUtil.decompress(val);
			value = new String(result, "ISO-8859-1");
			if (!TCUtil.encodingIsCorrect(val, "ISO-8859-1")) {
				this.addInvalidReason(InvalidReasons.INVALID_CHUNK_PNGTEXT_TEXT_BAD_VALUE);
			}
			compressed = true;
		} else if (chunkType.equals("iTXt")) {
			//compression flag
			if (raf.read1ByteInt(false) == 0) {
				compressed = false;
			} else {
				compressed = true;
			}
			int compressionmethod = raf.read1ByteInt(false);
			if (compressed && (compressionmethod != 0)) {
				this.addInvalidReason(InvalidReasons.INVALID_CHUNK_PNGTEXT_UNKNOWN_COMPRESSION);
			}
			//read language tag if present
			languageTag = raf.readNullTerminatedString("ISO-8859-1", startByte + length, true);
			if (!TCUtil.encodingIsCorrect(languageTag.getBytes("ISO-8859-1"), "ISO-8859-1")) {
				this.addInvalidReason(InvalidReasons.INVALID_CHUNK_PNGTEXT_LANGUAGE_BAD_VALUE);
			}
			//read translated keyword if present
			translatedKeyword = raf.readNullTerminatedString("UTF-8", startByte + length, true);
			if (!TCUtil.encodingIsCorrect(translatedKeyword.getBytes("UTF-8"), "UTF-8")) {
				this.addInvalidReason(InvalidReasons.INVALID_CHUNK_PNGTEXT_TRANSKEYWORD_BAD_VALUE);
			}
			//read value
			byte[] val = new byte[(int)(this.getLength() - (raf.getFilePointer() - this.getStartByte()) - 4)];
			raf.read(val);
			try {
				if (compressed) {
					val = TCUtil.decompress(val);
				}
				value = TCUtil.convertToString(val, Charset.forName("UTF-8"));
				if (!TCUtil.encodingIsCorrect(val, "UTF-8")) {
					this.addInvalidReason(InvalidReasons.INVALID_CHUNK_PNGTEXT_ITXT_BAD_VALUE);
				}
			} catch (Exception err) {
				this.addInvalidReason(InvalidReasons.INVALID_CHUNK_PNGTEXT_ITXT_BAD_VALUE);
			}
		} else {
			throw new IOException ("Unknown chunk type");
		}
	}
	
	public PNGFileChunk getParentChunk() {
		return parentChunk;
	}
	
	/**
	 * Getter method for the chunk's type
	 * 
	 * @return the chunk's type (iTXt, tEXt, or zTXt)
	 */
	public String getChunkType() {
		return chunkType;
	}
	
	/**
	 * Getter method for chunk's keyword
	 * 
	 * @return the chunk's keyword
	 */
	public String getKeyword() {
		return keyword;
	}
	
	/**
	 * Getter method for determining whether the chunk's textual data is compressed.
	 * @return true if the chunk's textual data is compressed (regardelss of chunk type), and false otherwise
	 */
	public boolean getCompressed() {
		return compressed;
	}
	
	/**
	 * Getter method for chunk's language tag (iTXt only)
	 * 
	 * @return the chunk's language tag, or empty string if none
	 */
	public String getLanguageTag() {
		return languageTag;
	}
	
	/**
	 * Getter method for the chunk's translated keyword (iTXt only)
	 * 
	 * @return the chunk's keyword traslated, or empty string if none exists
	 */
	public String getTranslatedKeyword() {
		return translatedKeyword;
	}
	
	/**
	 * Getter method for the chunk's value
	 * 
	 * @return the chunk's value
	 */
	public String getValue() {
		return value;
	}

}

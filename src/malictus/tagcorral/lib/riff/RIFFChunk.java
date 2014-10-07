package malictus.tagcorral.lib.riff;

import java.util.*;
import java.io.*;
import malictus.tagcorral.lib.InvalidReasons;
import malictus.tagcorral.lib.TCUtil;
import malictus.tagcorral.lib.chunk.*;
import malictus.tagcorral.lib.file.*;

/**
 * Represents a RIFF chunk.
 */
public class RIFFChunk extends Chunk {
	
	private RIFFChunk parentChunk;
	private Vector<Chunk> subChunks = new Vector<Chunk>();
	private String subType = "";
	
	/**
	 * RIFFChunk creator method.
	 * 
	 * @param parentFile the parent file that this chunk is associated with
	 * @param startByte the start of the chunk, in bytes
	 * @param length the length of the chunk, in bytes.
	 * @param raf a pointer to the file; doesn't have to be in the correct file position
	 * @param parentChunk the parent RIFF chunk to this one (null if no parent)
	 * @throws IOException if file is invalid, or start point or length are invalid
	 */
	public RIFFChunk(TCFile parentFile, long startByte, long length, TCRaf raf, RIFFChunk parentChunk) throws IOException {
		super(parentFile, startByte, length);
		this.parentChunk = parentChunk;
		parseChunk(raf);
	}
	
	public Vector<Chunk> getSubChunks() {
		return subChunks;
	}
	
	/**
	 * @return the parent to this chunk (may be null)
	 */
	public RIFFChunk getParent() {
		return parentChunk;
	}
	
	/**
	 * @return the subtype for this chunk (WAVE, for example); will be null for chunks with no subchunks
	 */
	public String getSubType() {
		return subType;
	}
	
	/**
	 * Does the actual business of parsing the RIFF chunk.
	 * @param raf pointer to the file (position not important)
	 * @throws IOException if file can't be parsed
	 */
	private void parseChunk(TCRaf raf) throws IOException {
		byte[] buf = new byte[4];
		raf.seek(this.getStartByte());
		raf.read(buf);
		raf.skipBytes(4);
		String test = new String(buf);
		if (test.toUpperCase().equals("RIFF") || (test.toUpperCase().equals("LIST"))) {
			//subchunks are present
			if (raf.getFilePointer() <= ((this.getStartByte() + this.getLength()) - 4)) {
				try {
					subType = raf.readFourCC();
				} catch (IOException err) {
					this.getParentFile().addInvalidReason(InvalidReasons.INVALID_FILE_READ_ERROR);
					subType = "";
				}
				boolean keepgoing = true;
				if (raf.getFilePointer() >= (this.getStartByte() + this.getLength() - 8)) {
					//no sub chunks at all?
					keepgoing = false;
				}
				while (keepgoing) {
					//read all chunks
					String chunkName = "";
					try {
						chunkName = raf.readFourCC();
					} catch (IOException err) {
						err.printStackTrace();
						keepgoing = false;
						break;
					}
					long length = raf.read4ByteInt(false, false);
					long endpos = raf.getFilePointer() + length;
					if (endpos > this.getParentFile().length()) {
						//file is truncated perhaps ?
						endpos = this.getParentFile().length();
						length = this.getParentFile().length() - raf.getFilePointer();
						this.getParentFile().addInvalidReason(InvalidReasons.INVALID_FILE_UNEXPECTED_END);
					}
					long curPos = raf.getFilePointer();
					if ((INFOChunk.STANDARD_INFO_TAGS.contains(chunkName)) || 
							(INFOChunk.EXTENDED_INFO_TAGS.contains(chunkName)) ) {
						INFOChunk info = new INFOChunk(this.getParentFile(), raf.getFilePointer() - 8, length + 8, raf, this);
						this.getParentFile().addMetadataChunk(info);
						if (!this.subType.equals("INFO")) {
							info.addInvalidReason(InvalidReasons.INVALID_CHUNK_INFO_NOT_IN_LIST);
						}
					} else if (chunkName.toLowerCase().equals("ixml")) {
						//here we remove the header part and do only the text itself
						IXMLChunk ixml = new IXMLChunk(this.getParentFile(), raf.getFilePointer(), length, raf, this);
						if (!chunkName.equals("iXML")) {
							ixml.addInvalidReason(InvalidReasons.INVALID_CHUNK_INCORRECT_CASE_NAME);
						}
						this.getParentFile().addMetadataChunk(ixml);
					} else if (chunkName.toLowerCase().equals("link")) {
						//here we remove the header part and do only the text itself
						LINKChunk link = new LINKChunk(this.getParentFile(), raf.getFilePointer(), length, raf, this);
						if (!chunkName.equals("link")) {
							link.addInvalidReason(InvalidReasons.INVALID_CHUNK_INCORRECT_CASE_NAME);
						}
						this.getParentFile().addMetadataChunk(link);
					} else if (chunkName.toLowerCase().equals("axml")) {
						//here we remove the header part and do only the text itself
						AXMLChunk axml = new AXMLChunk(this.getParentFile(), raf.getFilePointer(), length, raf, this);
						if (!chunkName.equals("axml")) {
							axml.addInvalidReason(InvalidReasons.INVALID_CHUNK_INCORRECT_CASE_NAME);
						}
						this.getParentFile().addMetadataChunk(axml);
					} else if (chunkName.toLowerCase().equals("_pmx")) {
						//here we remove the header part and do only the text itself
						RIFFXMPChunk axml = new RIFFXMPChunk(this.getParentFile(), raf.getFilePointer(), length, raf, this);
						if (!chunkName.equals("_PMX")) {
							axml.addInvalidReason(InvalidReasons.INVALID_CHUNK_INCORRECT_CASE_NAME);
						}
						this.getParentFile().addMetadataChunk(axml);
					}
					//backup and add the chunk as a generic RIFF chunk
					raf.seek(curPos);
					RIFFChunk rc = new RIFFChunk(this.getParentFile(), raf.getFilePointer() - 8, length + 8, raf, this);
					subChunks.add(rc);
					raf.seek(TCUtil.adjustForPadByte(endpos));
					if (raf.getFilePointer() >= (this.getStartByte() + this.getLength() - 8)) {
						keepgoing = false;
					}
				}
			} else {
				this.getParentFile().addInvalidReason(InvalidReasons.INVALID_FILE_UNEXPECTED_END);
			}
		} 
	}
	
}

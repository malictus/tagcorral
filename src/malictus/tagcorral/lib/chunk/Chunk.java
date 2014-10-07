package malictus.tagcorral.lib.chunk;

import java.io.*;
import malictus.tagcorral.lib.file.*;

/**
 * A chunk is any abitrary, contiguous, portion of a file.
 */
public abstract class Chunk {
	
	private TCFile parentFile;
	private long startByte;
	private long length;

	/**
	 * Chunk creator method.
	 * 
	 * @param parentFile the parent file that this chunk is associated with
	 * @param startByte the start byte of the chunk
	 * @param length the length of the chunk, in bytes. Must be at least 1.
	 * @throws IOException if file is invalid, or start point or length are invalid
	 */
	public Chunk(TCFile parentFile, long startByte, long length) throws IOException {
		this.parentFile = parentFile;
		this.startByte = startByte;
		this.length = length;
		verifyChunk();
	}
	
	/**
	 * Retrieval method for parent file.
	 * 
	 * @return the parent file that this chunk is associated with
	 */
	public TCFile getParentFile() {
		return parentFile;
	}
	
	/**
	 * Retrieval method for start byte.
	 * 
	 * @return the start byte of this chunk
	 */
	public long getStartByte() {
		return startByte;
	}
	
	/**
	 * Setter method for start byte.
	 * 
	 * @param startByte the new start byte associated with this chunk
	 * @throws IOException if the new position causes the chunk to become invalid
	 */
	public void setStartByte(long startByte) throws IOException {
		this.startByte = startByte;
		verifyChunk();
	}
	
	/**
	 * Retrieval method for chunk length.
	 * 
	 * @return the length of this chunk in bytes
	 */
	public long getLength() {
		return length;
	}
	
	/**
	 * Setter method for chunk length.
	 * 
	 * @param length the new length associated with this chunk
	 * @throws IOException if the new length causes the chunk to become invalid
	 */
	public void setLength(long length) throws IOException {
		this.length = length;
		verifyChunk();
	}
	
	/**
	 * Verify that a chunk exists in a real file.
	 * 
	 * @throws IOException if the chunk can't be verified
	 */
	private void verifyChunk() throws IOException {
		if (parentFile == null) {
			throw new IOException("Parent file does no exist");
		}
		if (startByte < 0) {
			throw new IOException("Start position must be greater than 0");
		}
		if (startByte > (parentFile.length() - 1)) {
			throw new IOException("Start position must be within file length");
		}
		if (length < 0) {
			throw new IOException("Length must be at least 1");
		}
		if ((length + startByte) > this.getParentFile().length()) {
			throw new IOException("Length runs past the end of the file");
		}
	}
	
}

package malictus.tagcorral.lib.png;

import java.io.*;
import malictus.tagcorral.lib.InvalidReasons;
import malictus.tagcorral.lib.file.*;
import malictus.tagcorral.lib.chunk.*;

/**
 * Chunk to represent a tIME chunk in a PNG file.
 */
public class TIMEChunk extends MetaChunk {
	
	private PNGFileChunk parentChunk;
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	private int second;
	
	/**
	 * TIMEChunk chunk creator method.
	 * 
	 * @param parentFile the parent file that this chunk is associated with
	 * @param startByte the start of the chunk, in bytes
	 * @param length the length of the chunk, in bytes.
	 * @param parentChunk the PNGFileChunk that this chunk is a part of
	 * @param raf a pointer to the file, can be in any byte position
	 * @throws IOException if file is invalid, or if this chunk can't be parsed at all
	 */
	public TIMEChunk(TCFile parentFile, long startByte, long length, PNGFileChunk parentChunk, TCRaf raf) throws IOException {
		super(parentFile, startByte, length);
		this.parentChunk = parentChunk;
		if (parentFile.length() < (startByte + length)) {
			throw new IOException("Chunk does not have enough bytes");
		}
		raf.seek(startByte + 8);
		
		if (length < 19) {
			this.addInvalidReason(InvalidReasons.INVALID_CHUNK_TOO_SHORT);
			return;
		}
		if (length > 19) {
			this.addInvalidReason(InvalidReasons.INVALID_CHUNK_TOO_LONG);
		}
		year = raf.read2ByteInt(false, true);
		month = raf.read1ByteInt(false);
		day = raf.read1ByteInt(false);
		hour = raf.read1ByteInt(false);
		minute = raf.read1ByteInt(false);
		second = raf.read1ByteInt(false);
		if ((year < 1900) || (month < 1) || (month > 12) || (day < 1) || (day > 31) || (hour < 0) 
				|| (hour > 23) || (minute < 0) || (minute > 59) || (second < 0) || (second > 60)) {
			this.addInvalidReason(InvalidReasons.INVALID_CHUNK_PNGTIME_NOT_VALID);
		}
	}
	
	public PNGFileChunk getParentChunk() {
		return parentChunk;
	}
	
	public int getYear() {
		return year;
	}
	
	public int getMonth() {
		return month;
	}
	
	public int getDay() {
		return day;
	}
	
	public int getHour() {
		return hour;
	}
	
	public int getMinute() {
		return minute;
	}
	
	public int getSecond() {
		return second;
	}

}

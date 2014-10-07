package malictus.tagcorral.lib.file;

import java.io.*;

import malictus.tagcorral.lib.TCUtil;

/**
 * TCRAF is an extension of RandomAccessFile that provides additional file
 * reading and writing capabilities.
 */
public class TCRaf extends RandomAccessFile {

	public TCRaf(String name, String mode) throws FileNotFoundException {
		super(name, mode);
	}
	
	public TCRaf(File file, String mode) throws FileNotFoundException {
		super(file, mode);
	}
	
	/**
	 * Read a one-byte integer value.
	 * 
	 * @param signed if true, value will be signed
	 * @return the one-byte value as an int
	 * @throws IOException if read error occurs
	 */
	public int read1ByteInt(boolean signed) throws IOException {
		if (signed) {
			return (int)this.readByte();
		} else {
			return this.readUnsignedByte();
		}
	}
	
	/**
	 * Write a one-byte integer value. Will work correctly with either a signed or unsigned value.
	 * 
	 * @param value the byte value to write
	 * @throws IOException if value is out of range or write error occurs
	 */
	public void write1ByteInt(int value) throws IOException {
		if ((value < -128) || (value > 255)) {
			throw new IOException("Int value is out of range");
		}
		if (value >= 128) {
			value = value - 256;
		}
		this.writeByte(value);
	}
	
	/**
	 * Read a two-byte integer value.
	 * 
	 * @param signed if true, value will be signed
	 * @param isBigEndian if true, value will be read as a big-endian value; false for small-endian
	 * @return the two-byte value as an int
	 * @throws IOException if read error occurs
	 */
	public int read2ByteInt(boolean signed, boolean isBigEndian) throws IOException {
		if (signed) {
			if (isBigEndian) {
				return (int)this.readShort();
			} else {
				short val = this.readShort();
				val = Short.reverseBytes(val);
				return (int)val;
			}
		} else {
			if (isBigEndian) {
				short val = this.readShort();
				int output = val & 0xffff;
				return output;
			} else {
				short val = this.readShort();
				val = Short.reverseBytes(val);
				int output = val & 0xffff;
				return output;
			}
		}
	}
	
	/**
	 * Write a 2-byte integer to a file
	 * 
	 * @param value the value expressed as an integer
	 * @param signed whether the value should be written as a signed or unsigned value
	 * @param isBigEndian true if the value should be written as big-endian, false for small-endian
	 * @throws IOException if value is out of range, or a write error occurs
	 */
	public void write2ByteInt(int value, boolean signed, boolean isBigEndian) throws IOException {
		if (signed) {
			if ((value < -32768) || (value > 32767)) {
				throw new IOException("Int value is out of range");
			}
		} else {
			if ((value < 0) || (value > 65535)) {
				throw new IOException("Int value is out of range");
			}
		}
		if (signed) {
			if (isBigEndian) {
				this.writeShort(value);
				return;
			} else {
				short output = Short.reverseBytes((short)value);
				this.writeShort(output);
			}
		} else {
			if (isBigEndian) {
				short output = (short)(value & 0xffff);
				this.writeShort(output);
			} else {
				short output = (short)(value & 0xffff);
				output = Short.reverseBytes(output);
				this.writeShort(output);
			}
		}
	}
	
	/**
	 * Read a four-byte integer value. Since unsigned values can exceed the capacity of an int, this value is stored as a long.
	 * 
	 * @param signed if true, value will be signed
	 * @param isBigEndian if true, value will be read as a big-endian value; false for small-endian
	 * @return the two-byte value as a long
	 * @throws IOException if read error occurs
	 */
	public long read4ByteInt(boolean signed, boolean isBigEndian) throws IOException {
		if (signed) {
			if (isBigEndian) {
				return (long)this.readInt();
			} else {
				int input = this.readInt();
				input = Integer.reverseBytes(input);
				return (long)input;
			}
		} else {
			if (isBigEndian) {
				int val = this.readInt();
				long output = val & 0xffffffffL;
				return output;
			} else {
				int val = this.readInt();
				val = Integer.reverseBytes(val);
				long output = val & 0xffffffffL;	
				return output;
			}
		}
	}
	
	/**
	 * Write a 4-byte integer to a file.
	 * 
	 * @param value the value expressed as a long
	 * @param signed whether the value should be written as a signed or unsigned value
	 * @param isBigEndian true if the value should be written as big-endian, false for small-endian
	 * @throws IOException if value is out of range, or a write error occurs
	 */
	public void write4ByteInt(long value, boolean signed, boolean isBigEndian) throws IOException {
		if (signed) {			
			if ((value < -2147483648) || (value > 2147483647)) {
				throw new IOException("Long value is out of range");
			}
		} else {
			if ((value < 0) || (value > 4294967295L)) {
				throw new IOException("Long value is out of range");
			}
		}
		if (signed) {
			if (isBigEndian) {
				this.writeInt((int)value);
				return;
			} else {				
				int output = Integer.reverseBytes((int)value);
				this.writeInt(output);
			}
		} else {
			if (isBigEndian) {
				int output = (int)(value & 0xffffffffL);
				this.writeInt(output);
			} else {
				int output = (int)(value & 0xffffffffL);
				output = Integer.reverseBytes(output);
				this.writeInt(output);
			}
		}
	}
	
	/**
	 * Read a 'fourCC'-style four character ASCII string (WAVE, FORM, etc.)
	 * 
	 * @return the four character string
	 * @throws IOException if the string can't be read, or isn't pure ASCII
	 */
	public String readFourCC() throws IOException {
		byte[] array = new byte[4];
		int read = this.read(array);
		if (read != 4) {
			throw new IOException("Four byte value cannot be read");
		}
		if (!TCUtil.encodingIsCorrect(array, "US-ASCII")) {
			throw new IOException("Four byte value is not pure ASCII");
		}
		return new String(array, "US-ASCII");
	}
	
	/**
	 * Write a 'fourCC' style four-character ASCII string to a file.
	 * 
	 * @param val the four-character ASCII string
	 * @throws IOException if string is wrong length, not pure ASCII, or write error occurs
	 */
	public void writeFourCC(String val) throws IOException {
		if (val.length() != 4) {
			throw new IOException("String is wrong length");
		}
		byte[] test = val.getBytes("US-ASCII");
		if (test.length != 4) {
			throw new IOException("String is wrong length");
		}
		this.write(test);
	}
	
	/**
	 * Read a null-terminated string from a file.
	 * 
	 * @param encoding the text encoding of the string
	 * @param limitPoint the point in the file at which to stop looking for a null; use 0 if no limit is specified
	 * @param requireNull if true, method will throw exception if no null is found before limit is reached
	 * @return the string (without the null character)
	 * @throws IOException if read error occurs, or limitPoint is reached (if requireNull is true)
	 */
	public String readNullTerminatedString(String encoding, long limitPoint, boolean requireNull) throws IOException {
		long startPoint = this.getFilePointer();
		int check = this.read1ByteInt(false);
		if (check == 0) {
			return "";
		}
		if (limitPoint == 0) {
			limitPoint = this.length();
		}
		//loop through once just looking for limit or null
		while ((this.getFilePointer() < limitPoint) && (check != 0)) {
			check = this.read1ByteInt(false);
		}
		long endPoint = 0;
		//did we reach limit without finding null?
		if (check != 0) {
			if (requireNull) {
				throw new IOException("Null character never found");
			}
			endPoint = this.getFilePointer();
		} else {
			//null found
			endPoint = this.getFilePointer() - 1;
		}
		this.seek(startPoint);
		byte[] buf = new byte[(int)(endPoint - startPoint)];
		this.read(buf);
		if (check == 0) {
			//skip over null byte
			this.skipBytes(1);
		}
		if (TCUtil.encodingIsCorrect(buf, encoding)) {
			return new String(buf, encoding);
		}
		throw new IOException("Encoding is incorrect");
	}

}

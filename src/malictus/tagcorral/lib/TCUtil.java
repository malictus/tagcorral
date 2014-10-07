package malictus.tagcorral.lib;

import java.text.*;
import java.util.zip.*;
import java.io.*;
import java.nio.*;
import java.nio.charset.*;

/**
 * A collection of general purpose backend utilties.
 */
public class TCUtil {
	
	private TCUtil() {}
	
	/**
	 * Convert an int into a hex string.
	 *
	 * @param decimal the integer
	 * @return the formatted hex string
	 */
	public static String convertToHex(int decimal) {
		String x = Integer.toHexString(decimal);
		while (x.length() < 4) {
			x = "0" + x;
		}
		x = "0x" + x;
		return x;
	}
	
	/**
	 * Convert a long into a hex string.
	 *
	 * @param decimal the long
	 * @return the formatted hex string
	 */
	public static String convertToHex(long decimal) {
		String x = Long.toHexString(decimal);
		if (x.length() <= 2) {
			while (x.length() < 2) {
				x = "0" + x;
			}
		} else if (x.length() <= 4) {
			while (x.length() < 4) {
				x = "0" + x;
			}
		} else if (x.length() <= 6) {
			while (x.length() < 6) {
				x = "0" + x;
			}
		} else {
			while (x.length() < 8) {
				x = "0" + x;
			}
		}
		x = "0x" + x;
		return x;
	}
	
	/**
	 * Helper method to adjust for pad bytes, as found in IFF files such as WAV and AIFF
	 * @param input the byte position before the adjustment
	 * @return the byte position after the adjustment (the same if input is even, and +1 if odd)
	 */
	public static long adjustForPadByte(long input) {
		long output = input;
		if ((output % 2) != 0) {
			output = output + 1;
		}
		return output;
	}
	
	/**
	 * Given a byte array of uncompressed data, return a byte array of compressed data.
	 * Uses java.util.zip (zlib) compression. Currently set to use best compression
	 * available every time.
	 * 
	 * @param uncompressedData the uncompressed byte array
	 * @return the compressed byte array, using highest compression level
	 */
	public static byte[] compress(byte[] uncompressedData) {
		Deflater compressor = new Deflater();
		compressor.setLevel(Deflater.BEST_COMPRESSION);
		compressor.setInput(uncompressedData);
		compressor.finish();
		ByteArrayOutputStream bos = new ByteArrayOutputStream(uncompressedData.length);
		byte[] buf = new byte[1024];
		while (!compressor.finished()) {
		    int count = compressor.deflate(buf);
		    bos.write(buf, 0, count);
		}
		try {
		    bos.close();
		} catch (IOException e) {}
		byte[] compressedData = bos.toByteArray();
		return compressedData;
	}
	
	/**
	 * Given a byte array of compressed data, return a byte array of uncompressed data.
	 * Uses java.util.zip (zlib) compression.
	 * 
	 * @param compressedData the compressed byte array
	 * @return the uncompressed byte array
	 * @throws IOException if data can't be decompressed
	 */
	public static byte[] decompress(byte[] compressedData) throws IOException {
		Inflater decompressor = new Inflater();
		decompressor.setInput(compressedData);
		ByteArrayOutputStream bos = new ByteArrayOutputStream(compressedData.length);
		byte[] buf = new byte[1024];
		while (!decompressor.finished()) {
		    try {
		        int count = decompressor.inflate(buf);
		        bos.write(buf, 0, count);
		    } catch (DataFormatException e) {
		    	throw new IOException("Data can't be decompressed");
		    }
		}
		try {
		    bos.close();
		} catch (IOException e) {}
		byte[] decompressedData = bos.toByteArray();
		return decompressedData;
	}
	
	/**
	 * Given a string, return the string's file extension, defined as everything in the
	 * string after the last period (.) If no periods are present in string, an empty
	 * string is returned.
	 *
	 * @param string The input string
	 * @return The string's file extension
	 */
	public static String getExtension(String string) {
		String extension = "";
		int i = string.lastIndexOf('.');
        if ((i > 0) &&  (i < string.length() - 1)) {
            extension = string.substring(i+1);
        }
        return extension;
	}
	
	/**
	 * Return a human-readable indication of a size, given a number of bytes.
	 * @param bytes the number of bytes
	 * @return a string such as (1.23GB, 1.4MB, etc.)
	 */
	public static String stringForBytes(long bytes) {
		DecimalFormat deci = new DecimalFormat("0.00");
		double gigs = (((double)bytes/1024d)/1024d/1024d);
		double megs = (((double)bytes/1024d)/1024d);
		double kb = ((double)bytes/1024d);
		if (gigs > 1) {
			return deci.format(gigs) + " GB";
		}
		if (megs > 1) {
			return deci.format(megs) + " MB";
		}
		if (kb > 1) {
			return deci.format(kb) + " KB";
		}
		return bytes + " bytes";
	}
	
	/**
	 * Return a one byte integer as a byte
	 * @param value the value to write to a byte
	 * @param signed true if value is signed, false otherwise
	 * @return the value as a byte
	 * @throws IOException if integer value is out of range
	 */
	public static byte write1ByteInt(int value, boolean signed) throws IOException {
		if ((value < -128) || (value > 255)) {
			throw new IOException("Int value is out of range");
		}
		if (value >= 128) {
			value = value - 256;
		}
		return (byte)value;
	}
	
	/**
	 * Write a two byte integer to a byte array
	 * @param value the value to write to an array
	 * @param signed true if the value should be signed, false otherwise
	 * @param isBigEndian true if value should be written big-endian, and false otherwise
	 * @return a byte array representing the value
	 * @throws IOException if the specified int is out of range
	 */
	public static byte[] write2ByteInt(int value, boolean signed, boolean isBigEndian) throws IOException {
		if (signed) {
			if ((value < -32768) || (value > 32767)) {
				throw new IOException("Int value is out of range");
			}
		} else {
			if ((value < 0) || (value > 65535)) {
				throw new IOException("Int value is out of range");
			}
		}
		byte[] out = new byte[2];
		if (signed) {
			if (isBigEndian) {
				out[0] = (byte)((value >>> 8) & 0xFF);
				out[1] = (byte)((value >>> 0) & 0xFF);
			} else {
				out[0] = (byte)((value >>> 0) & 0xFF);
				out[1] = (byte)((value >>> 8) & 0xFF);
			}
		} else {
			if (isBigEndian) {
				short output = (short)(value & 0xffff);
				out[0] = (byte)((output >>> 8) & 0xFF);
				out[1] = (byte)((output >>> 0) & 0xFF);
			} else {
				short output = (short)(value & 0xffff);
				out[0] = (byte)((output >>> 0) & 0xFF);
				out[1] = (byte)((output >>> 8) & 0xFF);
			}
		}
		return out;
	}
	
	/**
	 * Used to verify the encoding of a byte array. This method does not actually
	 * convert to string; it merely checks to see if it is possible without errors.
	 * @param bytes the bytes to convert to string.
	 * @param charset the charset to convert to
	 * @return true is the bytes can be encoded into this charset, and false otherwise
	 */
	public static boolean encodingIsCorrect(byte[] bytes, String charset) {
		try {
			CharsetDecoder decoder = Charset.forName(charset).newDecoder();
			decoder.decode(ByteBuffer.wrap(bytes));
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	/*
	 * Just the regular conversion to string, but removes the UTF-8 BOM if present
	 * Also removes the UTF-16 BOM's, since specifying charsets like 'UTF-16BE' make it read BOM as a character GRRR
	 */
	public static String convertToString(byte[] bytes, Charset charset) {
		String ret = new String(bytes, charset);
		if ( (bytes[0] == 0xEF - 256) && (bytes[1] == 0xBB - 256) && (bytes[2] == 0xBF - 256) ) {
			ret = ret.substring(1);
		} else if ( (bytes[0] == 0xFE - 256) && (bytes[1] == 0xFF - 256) ) {
			ret = ret.substring(1);
		} else if ( (bytes[0] == 0xFF - 256) && (bytes[1] == 0xFE - 256) ) {
			ret = ret.substring(1);
		}
		return ret;
	}
	
	public static CharsetInfo getCharsetFor(byte[] byteArray) {		
		//copy the first 100 bytes to a new testing array
		int amt = byteArray.length;
		if (amt > 100) {
			amt = 100;
		}
		byte[] testingArray = new byte[amt];
		System.arraycopy(byteArray, 0, testingArray, 0, amt);
		String stringForBytesUTF8 = TCUtil.convertToString(testingArray, Charset.forName("UTF-8"));
		String stringForBytesUTF16BE = TCUtil.convertToString(testingArray, Charset.forName("UTF-16BE"));
		String stringForBytesUTF16LE = TCUtil.convertToString(testingArray, Charset.forName("UTF-16LE"));
		//if no length, give up
		if (byteArray.length < 1) {
			//we'll just assume UTF-8 and give up
			return new CharsetInfo(Charset.forName("UTF-8"), false, false);
		}
		//first, deal with files that have BOM
		if (testingArray.length >= 3) {
			if ( (testingArray[0] == 0xEF - 256) && (testingArray[1] == 0xBB - 256) && (testingArray[2] == 0xBF - 256) ) {
				boolean hasErrors = false;
				//UTF-8 MIGHT have BOM (Notepad adds it for instance), but it's not a good idea
				if (!TCUtil.encodingIsCorrect(byteArray, "UTF-8")) {
					hasErrors = true;
				}
				return new CharsetInfo(Charset.forName("UTF-8"), true, hasErrors);				
			} else if ( (testingArray[0] == 0xFE - 256) && (testingArray[1] == 0xFF - 256) ) {
				boolean hasErrors = false;
				if (!TCUtil.encodingIsCorrect(byteArray, "UTF-16BE")) {
					hasErrors = true;
				}
				return new CharsetInfo(Charset.forName("UTF-16BE"), true, hasErrors);
			} else if ( (testingArray[0] == 0xFF - 256) && (testingArray[1] == 0xFE - 256) ) {
				boolean hasErrors = false;
				if (!TCUtil.encodingIsCorrect(byteArray, "UTF-16LE")) {
					hasErrors = true;
				}
				return new CharsetInfo(Charset.forName("UTF-16LE"), true, hasErrors);
			}
		}
		//now, deal with files with XML encoding specified
		String encodingTest = "";
		if (stringForBytesUTF8.startsWith("<?xml ")) {
			encodingTest = stringForBytesUTF8;
		} else if (stringForBytesUTF16BE.startsWith("<?xml ")) {
			encodingTest = stringForBytesUTF16BE;
		} else if (stringForBytesUTF16LE.startsWith("<?xml ")) {
			encodingTest = stringForBytesUTF16LE;
		}
		if (!(encodingTest.equals(""))) {
			encodingTest = encodingTest.toLowerCase();
			if (encodingTest.indexOf("encoding") > 0) {
				int startPos = encodingTest.indexOf("encoding");
				startPos = encodingTest.indexOf("\"", startPos) + 1;
				int endPos = encodingTest.indexOf(("\""), startPos);
				if ( (endPos > startPos) && (endPos > 0) && (startPos > 0) ) {
					encodingTest = encodingTest.substring(startPos, endPos);
				} else {
					encodingTest = "";
				}
			} else {
				encodingTest = "";
			}
		}
		boolean encodingTypeErr = false;
		if (!encodingTest.equals("")) {
			try {
				Charset theCharset = Charset.forName(encodingTest);
				boolean hasErrors = false;
				if (!TCUtil.encodingIsCorrect(byteArray, theCharset.name())) {
					hasErrors = true;
				}
				return new CharsetInfo(theCharset, false, hasErrors);
			} catch (Exception err) {
				//not a valid encoding type; just keep going and mark it as error
				encodingTypeErr = true;
			}
		}
		//check for XHTML with no header (assumed UTF-8)
		if (stringForBytesUTF8.startsWith("<!DOCTYPE HTML")) {
			boolean hasErrors = encodingTypeErr;
			if (!TCUtil.encodingIsCorrect(byteArray, "UTF-8")) {
				hasErrors = true;
			}
			return new CharsetInfo(Charset.forName("UTF-8"), false, hasErrors);
		}
		//here we check for XMP declarations
		//look for '<x:xmpmeta ' or '<?xpacket ' in a variety of encodings
		if (stringForBytesUTF8.startsWith("<?xpacket ")) {
			boolean hasErrors = encodingTypeErr;
			if (!TCUtil.encodingIsCorrect(byteArray, "UTF-8")) {
				hasErrors = true;
			}
			return new CharsetInfo(Charset.forName("UTF-8"), false, hasErrors);
		}
		if (stringForBytesUTF8.startsWith("<x:xmpmeta ")) {
			boolean hasErrors = encodingTypeErr;
			if (!TCUtil.encodingIsCorrect(byteArray, "UTF-8")) {
				hasErrors = true;
			}
			return new CharsetInfo(Charset.forName("UTF-8"), false, hasErrors);
		}
		if (stringForBytesUTF16BE.startsWith("<?xpacket ")) {
			boolean hasErrors = encodingTypeErr;
			if (!TCUtil.encodingIsCorrect(byteArray, "UTF-16BE")) {
				hasErrors = true;
			}
			return new CharsetInfo(Charset.forName("UTF-16BE"), false, hasErrors);
		}
		if (stringForBytesUTF16BE.startsWith("<x:xmpmeta ")) {
			boolean hasErrors = encodingTypeErr;
			if (!TCUtil.encodingIsCorrect(byteArray, "UTF-16BE")) {
				hasErrors = true;
			}
			return new CharsetInfo(Charset.forName("UTF-16BE"), false, hasErrors);
		}
		if (stringForBytesUTF16LE.startsWith("<?xpacket ")) {
			boolean hasErrors = encodingTypeErr;
			if (!TCUtil.encodingIsCorrect(byteArray, "UTF-16LE")) {
				hasErrors = true;
			}
			return new CharsetInfo(Charset.forName("UTF-16LE"), false, hasErrors);
		}
		if (stringForBytesUTF16LE.startsWith("<x:xmpmeta ")) {
			boolean hasErrors = encodingTypeErr;
			if (!TCUtil.encodingIsCorrect(byteArray, "UTF-16LE")) {
				hasErrors = true;
			}
			return new CharsetInfo(Charset.forName("UTF-16LE"), false, hasErrors);
		}
		//if we reached here, file is just plain text, not XMP or XML, so just check standard ones; if any of them encode properly, use that
		//if neither, give up
		if (TCUtil.encodingIsCorrect(byteArray, "UTF-8")) {
			return new CharsetInfo(Charset.forName("UTF-8"), false, encodingTypeErr);
		}
		if (TCUtil.encodingIsCorrect(byteArray, "ISO-8859-1")) {
			return new CharsetInfo(Charset.forName("ISO-8859-1"), false, encodingTypeErr);
		}
		if (TCUtil.encodingIsCorrect(byteArray, "UTF-16LE")) {
			return new CharsetInfo(Charset.forName("UTF-16LE"), false, encodingTypeErr);
		}
		if (TCUtil.encodingIsCorrect(byteArray, "UTF-16BE")) {
			return new CharsetInfo(Charset.forName("UTF-16BE"), false, encodingTypeErr);
		}
		return new CharsetInfo(null, false, true);
	}

}

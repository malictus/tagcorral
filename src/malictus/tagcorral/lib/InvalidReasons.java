package malictus.tagcorral.lib;

/**
 * Just a list of invalid conditions for files and chunks. 
 * Messages strings should be kept in sync with this list.
 */
public class InvalidReasons {

	private InvalidReasons() {}
	
	/****************** INVALID FILE REASONS ********************/
	//end of file reached unexpectedly
	public static final String INVALID_FILE_UNEXPECTED_END = "INVALID_FILE_UNEXPECTED_END";
	//unexpected data was found in the file
	public static final String INVALID_FILE_UNEXPECTED_DATA = "INVALID_FILE_UNEXPECTED_DATA";
	//extra data was found after the expected end of file
	public static final String INVALID_FILE_UNEXPECTED_EXTRA_DATA = "INVALID_FILE_UNEXPECTED_EXTRA_DATA";
	//file read error
	public static final String INVALID_FILE_READ_ERROR = "INVALID_FILE_READ_ERROR";
	
	/****************** INVALID CHUNK REASONS ********************/
	//chunk should be ASCII but is not
	public static final String INVALID_CHUNK_NOT_ASCII = "INVALID_CHUNK_NOT_ASCII";
	//a GIF comment chunk is in a GIF87a file
	public static final String INVALID_CHUNK_GIFCOMMENT_NOT_89A = "INVALID_CHUNK_GIFCOMMENT_NOT_89A";
	//this chunk comes before a required header chunk
	public static final String INVALID_CHUNK_BEFORE_HEADER = "INVALID_CHUNK_BEFORE_HEADER";
	//this chunk is duplicated, but should be unique to a file
	public static final String INVALID_CHUNK_SHOULD_BE_UNIQUE = "INVALID_CHUNK_SHOULD_BE_UNIQUE";
	//PNG text chunk (any) keyword not in ISO-8859-1 charset
	public static final String INVALID_CHUNK_PNGTEXT_BAD_KEYWORD = "INVALID_CHUNK_PNGTEXT_BAD_KEYWORD";
	//PNG text chunk (any) with keyword longer than 79 chars
	public static final String INVALID_CHUNK_PNGTEXT_LONG_KEYWORD = "INVALID_CHUNK_PNGTEXT_LONG_KEYWORD";
	//PNG text chunk (any) keyword has white space at beginning or end
	public static final String INVALID_CHUNK_PNGTEXT_KEYWORD_TRIM = "INVALID_CHUNK_PNGTEXT_KEYWORD_TRIM";
	//PNG text chunk (any) keyword contains nonbreaking space
	public static final String INVALID_CHUNK_PNGTEXT_KEYWORD_NONBREAKING_SPACE = "INVALID_CHUNK_PNGTEXT_KEYWORD_NONBREAKING_SPACE";
	//PNG text chunk (tEXt or zTXt) text value is not ISO-8859-1 charset
	public static final String INVALID_CHUNK_PNGTEXT_TEXT_BAD_VALUE = "INVALID_CHUNK_PNGTEXT_TEXT_BAD_VALUE";
	//PNG text chunk (zTXt or iTXt) uses an unknown compression method
	public static final String INVALID_CHUNK_PNGTEXT_UNKNOWN_COMPRESSION = "INVALID_CHUNK_PNGTEXT_UNKNOWN_COMPRESSION";
	//PNG iTXt Language tag is not ISO-8859-1
	public static final String INVALID_CHUNK_PNGTEXT_LANGUAGE_BAD_VALUE = "INVALID_CHUNK_PNGTEXT_LANGUAGE_BAD_VALUE";
	//PNG iTXt translated keyword not UTF-8 encoding
	public static final String INVALID_CHUNK_PNGTEXT_TRANSKEYWORD_BAD_VALUE = "INVALID_CHUNK_PNGTEXT_TRANSKEYWORD_BAD_VALUE";
	//PNG iTXt text value isn't UTF-8
	public static final String INVALID_CHUNK_PNGTEXT_ITXT_BAD_VALUE = "INVALID_CHUNK_PNGTEXT_ITXT_BAD_VALUE";
	//chunk is shorter than expected
	public static final String INVALID_CHUNK_TOO_SHORT = "INVALID_CHUNK_TOO_SHORT";
	//chunk is longer than expected
	public static final String INVALID_CHUNK_TOO_LONG = "INVALID_CHUNK_TOO_LONG";
	//plain text chunk is too long to create a valid byte array to store the string
	public static final String INVALID_CHUNK_TOO_LONG_FOR_STRING = "INVALID_CHUNK_TOO_LONG_FOR_STRING";
	//PNG tIME chunk has invalid values
	public static final String INVALID_CHUNK_PNGTIME_NOT_VALID = "INVALID_CHUNK_PNGTIME_NOT_VALID";
	//a chunk name that should be ASCII is not
	public static final String INVALID_CHUNK_CHUNK_NAME_NOT_ASCII = "INVALID_CHUNK_CHUNK_NAME_NOT_ASCII";
	//info chunk data is UTF-8 rather than ASCII
	public static final String INVALID_CHUNK_INFO_CHUNK_NOT_ASCII = "INVALID_CHUNK_INFO_CHUNK_NOT_ASCII";
	//chunk should end with null terminator but does not
	public static final String INVALID_CHUNK_NO_NULL_TERMINATOR = "INVALID_CHUNK_NO_NULL_TERMINATOR";
	//catchall error for invalid chunks with no specific reason
	public static final String INVALID_CHUNK_INVALID = "INVALID_CHUNK_INVALID";
	//RIFF INFO chunk tag not enclosed in a list of type 'INFO'
	public static final String INVALID_CHUNK_INFO_NOT_IN_LIST = "INVALID_CHUNK_INFO_NOT_IN_LIST";
	//a plain text chunk where the chunk data can't be read
	public static final String INVALID_CHUNK_UNKNOWN_ENCODING = "INVALID_CHUNK_UNKNOWN_ENCODING";
	//encoding errors
	public static final String INVALID_CHUNK_ENCODING_ERRORS = "INVALID_CHUNK_ENCODING_ERRORS";
	//UTF-8 with BOM
	public static final String INVALID_CHUNK_UTF8_WITH_BOM = "INVALID_CHUNK_UTF8_WITH_BOM";
	//WAV-specific chunk in non-wave file
	public static final String INVALID_CHUNK_NOT_WAVE = "INVALID_CHUNK_NOT_WAVE";
	//chunk name has wrong case
	public static final String INVALID_CHUNK_INCORRECT_CASE_NAME = "INVALID_CHUNK_INCORRECT_CASE_NAME";
	//XMP chunk that isn't UTF-8 in file type where it should be
	public static final String INVALID_CHUNK_XMP_NOT_UTF8 = "INVALID_CHUNK_XMP_NOT_UTF8";
	
}

package org.csvcompiler.lexical;

/**
 * Each Token refers to a Velocity Engine language Unit
 * To valid tokens of the CSVCompiler are the following
 * 1)TEXT
 * 2)REFERENCE
 * 3)LIST_ITEM
 * 4)FOREACH
 * 5)END
 * 6)LP for Left Paranthesis
 * 7)RP for Right Paranthesis
 * 8)EOF for End of File indicating no more tokens ahead 
 * 9)NULL for an somehow unidentified token  
 * ?)IN should be but not exists currently
 * 
 * @author Onur
 * 
 */

public class Token {
    /**The type name (given in the Class description) of the token */
	public String type;
	/**The value of Token (i.e. for REFERENCE token TODAY)*/
	public String value;
	/**Line number this token is encountered while parsing template file, used for error reporting*/
	public int line;
	/**The seperator for the token (important for Reference and ListItems)*/
	public String seperator;
	
	/**Mostly used Constructor*/
	public Token(String t, String v) {
		type = t;
		value = v;
	}
	/**Empty Constructor*/
	public Token() {
		line = 0;
		value = "";
		type = "";
	}

}
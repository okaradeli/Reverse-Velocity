package org.csvcompiler.lexical;

import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.util.Vector;



/**
 * Lexical Analyzer, preparse the template file
 * and create 10-15 kind of Tokens including
 * <BR>REFERENCE,LIST_ITEM,STATIC_TEXT,FOREACH,IN,LP,RP
 * 
 * @author Onur
 *
 * 
 * 
 */
public class LexicalAnalayzer {
    /**Token table that the parser will traverse on*/
	public Vector tokenTable=new Vector();
	/**The Template file DataInputStream*/
	public Reader dataInput;
	/**Current Token that is found*/
	public Token currToken;
	/**Last character read from the inputStream*/
	public char currChar;
	/**Current Line Number, used for errorReporting*/
	public int currLineNumber;
	/**Some keywords that would not be understand as Static texts #foreach,#end*/
	public String keywordTable[] =
	{
		"#foreach",
		"#end",
	};
	public String templateCharacters="";//these are formatContent specific characters such as ,dot,space ... 
										//And treated specially while parsing the actual input
	
	
	/**
	 * The constructor and entry point of LexicalAnalyzer 
	 * 
	 * @param input The template file InputStream
	 * @throws Exception
	 */
	//public LexicalAnalayzer(InputStream input)throws Exception{
	public LexicalAnalayzer(Reader input)throws Exception{
		dataInput=input;
		currChar=getNextChar();		
		
		Token token=null;
		while(true){
			token=getNextToken();
			tokenTable.add(token);
			if(token.type.equals("EOF"))
				break;
		}
		int a=3;
		
	}
	
	
	/**
	 * Finds and returns the next Token
	 * <br>A single state machine is used to identify 10-15 kinds of Tokes 
	 * <br>Refer to Velocity Manual for valid tokenization.
	 * 
	 * 
	 * @return currToken -The token found
	 * @throws Exception
	 */
	Token getNextToken() throws Exception{		
		currToken = new Token();
		currToken.type = "NULL";
		currToken.value = "";
		String Id = "";
		
		//if char is # it may be foreach or end (foreach)
		if(currChar=='#'){
			//get the text to the next non alpha numeric char
			String tokenString=getAlphaNumericString();
			if(tokenString.equals("foreach")){
				currToken=new Token("FOREACH","");
			}
			else if(tokenString.equals("end")){
				currToken=new Token("END","");
				//a bad implementation of Velocity engine after #end , the \r\n are removed implicityly
				if(currChar=='\r'||currChar=='\n'){
				    currChar=getNextChar();
				    if(currChar=='\n')
				        currChar=getNextChar();
				}
			}
			else{
				currToken=new Token("TEXT","#"+tokenString);
			}
		}
		//it the char is $ it must be an identifier.
		else if(currChar=='$'){
			//get the text to the next non alpha numeric char
			String tokenString=getAlphaNumericString();
			
			//LIST ITEM
			if(currChar=='.'){				
				String listItemString=getAlphaNumericString();
				currToken=new Token("LIST_ITEM", listItemString);
			}
			else{
				//single REFERENCE
				currToken=new Token("REFERENCE", tokenString);
			}
			//the identifier delimiter is found as well (examples ,.;)
			currToken.seperator=String.valueOf(currChar);
			
		}
		//control for the the LEFT PARANTHESIS
		else if (currChar == '(') {
			currToken=new Token("LP","");
			currChar=getNextChar();
		}
		else if (currChar == ')') {
			currToken=new Token("RP","");
			currChar=getNextChar();
			//a bad implementation of Velocity engine after #foreach's closing paranthesis ) , the \r\n are removed implicityly
			if(currChar=='\r'||currChar=='\n'){
			    currChar=getNextChar();
			    if(currChar=='\n')
			        currChar=getNextChar();
			}
			
		}
		//if EOF is reached (no tokens left)
		else if (currChar == 0) {
			currToken=new Token("EOF","");
			currChar=getNextChar();
		}
		//otherwise it is free text
		else {
			String tokenString=getFreeText();
			currToken=new Token("TEXT",tokenString);			
		}
		//what is the line of token?
		currToken.line = currLineNumber;
		return (currToken);
	}
	
	/**
	 * For tokenizer finds the next language element
	 * Characters such as # $ ) ( will end a free text
	 * 
	 * @return freeText
	 */
	private String getFreeText() {
	    String freeText="";
			
		while (true) {
			if (!(Character.isLetterOrDigit(currChar)||currChar == '#' || currChar == '$' || currChar == 0 || currChar == '(' || currChar == ')'))//this character is not format content specific
				templateCharacters+=currChar;
				
	        freeText += currChar;
			//regardless of lexical analyze , \n indicates a new line thus increment the line counter
			if(currChar=='\n'){
			    currLineNumber++;
			}
	        currChar = getNextChar();	        
	        if (currChar == '#' || currChar == '$' || currChar == 0 || currChar == '(' || currChar == ')')//this character is a part of language
	            break;//for the above Language specific chars we dont know if they are special character for the acual input or not (?) 
	        
	    }

		return freeText;
	}

	/**
	 * A reference or list item candidate thus must include
	 * only alphaNumerical characters 
	 * 
	 * @return tokenString
	 */
	private String getAlphaNumericString()throws Exception {
		
		String tokenString="";
		currChar=getNextChar();//skip the $ and start with the ID name
		
		//if the optional prefix '{'  is used ignore it (ex. ${fieldName} instead of $fieldName ) refer to Velocity Specification for further info
		if(currChar=='{')
			currChar=getNextChar();
		
		
		while(Character.isLetterOrDigit(currChar) || currChar=='_'){
			tokenString+=currChar;
			currChar=getNextChar();
		}
		
		//if the optional suffix '}'  is used ignore it (ex. ${fieldName} instead of $fieldName)refer to Velocity Specification for further info
		if(currChar=='}')
			currChar=getNextChar();
		
		
		if(tokenString.length()==0){
			lexicalError("Invalid LIST_ITEM: Should be alphaNumeric string");
		}
		
		
		return tokenString;
	}

	
	/**
	 * The nextCharacter read from the input stream
	 * or 0 if EOF reached
	 * 
	 * @return nextChar
	 */
	public char getNextChar() {
		char c;
		try {
			int i=dataInput.read();//returns integer not char (integer can represent char)
			if(i==-1)//-1 means end of file
				return 0;
			c = (char)i;
			return c;
		} catch (EOFException eofex) {
			return 0;//End of file found
		} catch (IOException io) {
		}
		return 0;
	}
	
    /**
     * For informative messages
     * <br>This could be the Standart Output(Screen)
     * or a Log file. Thus a central errorReporting method is used.
     * 
     * @param errorMessage -The core error message
     * @throws Exception
     */
	public void lexicalError(String errorMessage)throws Exception{
	    String message="";
	    message+="LEXICAL_ERROR Line:"+currLineNumber+"\n";
	    message+=errorMessage+"\n";
	    
	    System.out.println(message);
	    throw new Exception(message);
	}
	

}

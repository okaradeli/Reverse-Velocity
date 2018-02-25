package org.csvcompiler.compiler;

import java.io.EOFException;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Vector;

import org.csvcompiler.Foreach;
import org.csvcompiler.ListItem;
import org.csvcompiler.ParseTree;
import org.csvcompiler.Reference;
import org.csvcompiler.StatementList;
import org.csvcompiler.Text;

;

/**
 * Used to parse and compile CSV files the format of file should obey to the
 * rules given in the predefined VM file (VM file is the standart of Velocity
 * Template Engine)
 * 
 * @author Onur Karadeli
 * 
 */
public class Compiler {

	/**Reference to Parse Tree*/
	public ParseTree parseTree;
	
    public static final String READ_INPUT_WITH_BUFFER="WITH_BUFFER";
    public static final String READ_INPUT_WITHOUT_BUFFER="WITHOUT_BUFFER";

    /** The final output of all these CSV processing */
    public HashMap compilerOutput;

    /**The input Stream (i.e. csv file)*/
    //public DataInputStream dataInput;
    public Reader dataInput;

    /**The last character read from the input stream*/
    //public char currChar;

    /**Last string read from the input stream, is used to put back the contents read to a buffer*/
    public String currInputText;

    /** Because we dont rewind the back the input, we store the last read string in this buffer before reading from the InputStream*/
    public String inputBuffer = "";
    
    /** Related with inputBuffer, it is copied to input buffer when for loop is terminated(i.e when mismatch occurs)*/
    public String forLoopBuffer = "";
    

    /** used to report where compilation error occurs*/
    public int inputLineNumber = 1;

    /**
     * The only constructor
     * 
     * @param inputStream- The input file to read from 
     * @param parser-The parse tree to match the input file with
     * @throws Exception
     */
    public Compiler(Reader inputStream, ParseTree parseTree) throws Exception {    	
        //dataInput = new DataInputStream(inputStream);
    	dataInput=inputStream;
        this.parseTree=parseTree;
        Vector statementList = parseTree.statementList.statements;
        //currChar = getNextChar();
        compilerOutput=new HashMap();
        compileStatementList(statementList);

    }

    /**
     * For each compiler: Each statement in the foreach is executed 
     * till there is no match. A mismatch always occurs at the end of
     * the foreach loop(i.e. searching for a listitem static text but 
     * seeing a completly different static text) 
     * <br>When a mismatch occurs in the for each loop it is not necessarily
     * a Compile-Error, it might be an indication of the end of for each loop.
     * <br>Also when EOF is reached the foreach loop is ended. 
     * 
     * @param foreach 
     * @throws Exception
     */
    public void compileForeachStatement(Foreach foreach) throws Exception {
        Vector statementList = foreach.forEachStmList.statements;
        boolean loopEnded = false;

        int itemIndex = 0;
        while (!loopEnded) {// for each loop will continue , till the input not
            // matches with the template
            for (int i = 0; i < statementList.size(); i++) {
                
                try{
                    if (statementList.get(i) instanceof Text) {// constant text
                        Text text = (Text) statementList.get(i);
                        compileTextStatement(text,READ_INPUT_WITH_BUFFER);
                    } else if (statementList.get(i) instanceof Reference) {
                        Reference reference = (Reference) statementList.get(i);
                        compileReferenceStatement(reference,READ_INPUT_WITH_BUFFER);
                    } else if (statementList.get(i) instanceof ListItem) {
                        ListItem listItem = (ListItem) statementList.get(i);
                        compileListStatement(foreach, listItem, itemIndex,READ_INPUT_WITH_BUFFER);
                    }
                }catch(Exception e){
                    //the mis-match might be an indication of the end of the for-loop, not necessarilly an error
                    loopEnded = true;// the mis-match might be an indication of the end of the
                    inputBuffer=forLoopBuffer+inputBuffer;  
                    forLoopBuffer="";
                    //clear the last item that is being added from the CompilerOutput                    
                    HashMap list = (HashMap) compilerOutput.get(foreach.listName);
                    list.remove(new Integer(itemIndex));
                    break;
                    
                }
                
            }
            itemIndex++;
            forLoopBuffer="";
        }

    }

    /**List statement is $item.CUSTOMER_NAME type of statements
     * before evaluating the CUSTOMER_NAME's value the compiler
     * assigns the name of the list.
     * <br>After evaluating the value of the listItem a triple
     * is put onto the compiler output
     * <br>(listName,itemIndex,key,value)
     * <br>
     * <br>The listItem may be variable length or fixed length
     * Thus getVariableText,getFixedLengthText methods are used
     * accordingly   
     * 
     * 
     * @param foreach The foreach structure, so that we can get the name of the list
     * @param listItem List Item we are processing
     * @param itemIndex When putting onto the compilerOutput the index of the item (i.e. an automatically incremented value)
     * @param bufferType When a mismatch occur, indicator wheter to put the read characters to a buffer so that we can read this input again
     */
    private void compileListStatement(Foreach foreach, ListItem listItem, int itemIndex,String bufferType) throws Exception {
        // put the name of the listItem's containerName
        listItem.listName = foreach.listName.referenceName;

        // get the List (if previously added)
        HashMap list = (HashMap) compilerOutput.get(foreach.listName);
        if (list == null) {// first time we are creating a variable of this
            // list
            list = new HashMap();
            compilerOutput.put(foreach.listName, list);
        }
        // after storing the LIST name, store the variable name,its index and
        // its value
        String itemText = null;
        try {
            if (listItem.keyFixedLength)
                itemText = getFixedLengthText(listItem.keyLength, listItem.keyAlignment, listItem.keyFillCharacter);
            else
                itemText = getVariableText(listItem.seperator);
        } catch (EOFException e) {
            // if End of file found dont search futher
            throw new CSException(e);
        }
        if (itemText.length() == 0) {
            //compilerError(listItem.itemName+" için uygun deðer buluanamdý. Metin uzunluðu:0");
        }
        if (bufferType.equals(READ_INPUT_WITH_BUFFER)) {
            forLoopBuffer+=itemText;//+currChar;
            //currChar = getNextChar();
        }
        

        HashMap listItemDetails = (HashMap) list.get(new Integer(itemIndex));
        if (listItemDetails == null) {
            listItemDetails = new HashMap();
            list.put(new Integer(itemIndex), listItemDetails);
        }
        listItemDetails.put(listItem.itemName, itemText);

    }

    /**
     * a single key (i.e. $ASSET_TYPE)
     * <br>The Reference may be variable length or fixed length 
     * (see {@link CSVCompiler} for more about this parameter)
     * Thus getVariableText,getFixedLengthText methods are used
     * accordingly   
     * 
     * @param reference
     * @throws Exception
     */
    public void compileReferenceStatement(Reference reference,String bufferType) throws Exception {
        String variableText = "";
        try {
            if (reference.keyFixedLength)
                variableText = getFixedLengthText(reference.keyLength, reference.keyAlignment, reference.keyFillCharacter);
            else
                variableText = getVariableText(reference.seperator);
        } catch (Exception e) {
            throw e;// when EOF file found dont do anything
        }
        if (variableText.length() == 0) {
            //compilerError("Empty text for REFERENCE:$" + reference.referenceName);
            //variableText="";
        }

        //when mismatches occur , put the read characters to inputbuffer (for further investigation)
        if(bufferType.equals(READ_INPUT_WITH_BUFFER)){
            forLoopBuffer += variableText;
        }
        
        // register the variable to the Bag
        compilerOutput.put(reference.referenceName, variableText);
        int a=5;
    }

    /**
     * A Static text: Each static text MUST match with the static text in 
     * the template file 
     * (one exception is for the end conditions of foreach loop
     * only a WARNING is given there)
     * 
     * 
     * @param text: coming from the ParseTree
     * @throws Exception
     */
    public void compileTextStatement(Text text,String bufferType) throws Exception {

        // get the size of the constant text and match it with the input
        // if they dont match there is an error
        int a = 0;
        int textLen = text.content.length();
        String inputText = getInputText(textLen);
        if (!inputText.equals(text.content)) {
            if(bufferType.equals(READ_INPUT_WITH_BUFFER))
                forLoopBuffer += inputText;

            // following is the decision princible of this compiler, look the
            // API documentation why this is a warning but not an ERROR
            String warningMessage = "WARNING:'" + inputText + "' found while searching '" + text.content + "'";
            compilerError(warningMessage);
        } else {// if successful matching there is nothing to do with constant
            //clear the input buffer; we are on the correct path
            //inputBuffer="";
            // text
            return;
        }

    }

    /**
     * A list of consequtive statements
     * <br>Currently the Main Template and Foreach statements
     * has StatementLists. 
     * <br>Each Statement in the List must be either Text,Reference or Foreach
     * all other kinds of Tokens will raise a compile time error 
     * (i.e. ListItem cant alone be a Statement)
     * 
     * @param statementList -Vector containing list of {@link StatementList}
     *  
     * @throws Exception
     */
    private void compileStatementList(Vector statementList) throws Exception {
        for (int i = 0; i < statementList.size(); i++) {
            try {
                if (statementList.get(i) instanceof Text) {// constant text
                    Text text = (Text) statementList.get(i);
                    compileTextStatement(text,READ_INPUT_WITHOUT_BUFFER);
                } else if (statementList.get(i) instanceof Reference) {
                    Reference reference = (Reference) statementList.get(i);
                    compileReferenceStatement(reference,READ_INPUT_WITHOUT_BUFFER);
                } else if (statementList.get(i) instanceof Foreach) {
                    Foreach foreach = (Foreach) statementList.get(i);
                    compileForeachStatement(foreach);
                }
                int a=5;
            } catch (EOFException eof) {
                return;// when EOF reached without an error , we are Done!
            } catch (Exception e) {
                throw e;
            }
        }
    }

    /**
     * Variable text are replaced with template variables ,then cant contain
     * TEMPLATE_CHARACTERS (i.e. #$<> )
     * <br>Till template characters is encountered a string is built and returned
     * @param seperator - Until this character is seen the variable is get appended with input
     * @return varText Text containing a variable replacement text
     */
    public String getVariableText(String seperator) throws Exception {
        String varText = "";
        char currChar;

        while (true) {
            currChar = getNextChar();
            if (currChar == 0)// EOF dont search any characters return the String read
                return varText;//throw new EOFException();
            // Merge text upto the template characters (indexOf method test if
            // the string contains a given char)
            if (seperator.indexOf(currChar) == -1) {
                varText += currChar;
            } else{
                inputBuffer=currChar+inputBuffer;//put back the last character to the input buffer
                break;
            }
            
        }

        return varText;
    }

    /**
     * If the Key is given a fixedLength Key then , keyLength chars are 
     * read from the InputStream then processed further to trim the fillment characters
     * and returned as the core Key value.
     * 
     * @param fixedTextValue -The core key value, trimmed from fillment characters and so on
     * @return
     */
    private String getFixedLengthText(int keyLength, String keyAlignment, String keyFillCharacter) throws Exception {
        String fixedText = "";
        String fixedTextValue = "";
        char currChar;
        
        for (int i = 0; i < keyLength; i++) {
            currChar = getNextChar();
            if (currChar == 0){// EOF dont search any characters
                inputBuffer=currChar+inputBuffer;//put back the last character to the input buffer
                return fixedText;
            }
            fixedText += currChar;
            
        }

        //now apply the alignment and fillment operations on the text
        if (keyAlignment == ParseTree.KEY_ALIGN_NONE) {//NO ALIGNMENT, paste as is
            return fixedText;
        }
        else{//process fixed text with alignment and fill characters
            if (keyAlignment.equals( ParseTree.KEY_ALIGN_LEFT)) {
                for (int i = 0; i < fixedText.length(); i++) {
                    if (keyFillCharacter.equals("" + fixedText.charAt(i))) {
                        break;
                    }
                    fixedTextValue += fixedText.charAt(i);
                }
            }
            if (keyAlignment.equals(ParseTree.KEY_ALIGN_RIGHT)) {
                for (int i = fixedText.length() - 1; i >= 0; i--) {
                    if (keyFillCharacter.equals("" + fixedText.charAt(i))) {
                        break;
                    }
                    fixedTextValue = fixedText.charAt(i) + fixedTextValue;
                }
            }
            return fixedTextValue;
        }

    }

    /**
     * Is used to get Static text
     * if EOF is read, the so far read string is returned  
     * 
     * @param textLength -How much characters do we need to read from InputStream
     * @return an i character String from the dataInputStream
     */
    public String getInputText(int textLength) throws Exception {

        String inputText = "";
        char currChar;

        for (int index = 0; index < textLength; index++) {
            currChar = getNextChar();
            // an ordinary character
            if (currChar != 0) {
                inputText += currChar;
            }
            // EOF character
            else {
                if(inputBuffer.length()!=0){//look for inputBuffer also
                    currChar = getNextChar();
                    index--;//bad core, refactor here
                    continue;
                }
            }
            
        }
        return inputText;
    }

    
    /**
     * Takes the character from the InputStream to be used by the compiler methods
     * <br>The inputBuffer is used instead of InputStream in case it is not empty
     * <br>Because the compiler is one-pass compiler, if an Input text should be 
     * put back to the inputStream(i.e. end of foreach loop) it is put to this buffer
     * so that the getNextChar method will read this text before asking to InputStream
     * <br>For informative messages lineNumber is also updated if '\n' char is seen
     * 
     * @return nextChar -The next character read or 0 if EOF (or any kind of error) is reached
     */
    public char getNextChar() {
        char c;
        try {
            if (!inputBuffer.equals("")) {
                c = inputBuffer.charAt(0);
                inputBuffer = inputBuffer.substring(1);// after removing the
                // first char
            } else {
            	int i=dataInput.read();// there is no buffer so read regardless of compilation when \n (supposed to be end of line) is encountered , increment the line counter
                if(i==-1)//if c is -1 means end of file
                	return 0;
                c=(char)i;
                if (c == '\n') {
                    inputLineNumber++;
                }
            }
            return (char) c;
        } catch (EOFException eofex) {
            return 0;
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
    public void compilerError(String errorMessage) throws Exception {
        String message = "";
        message += "COMPILER_MESSAGE Line:" + inputLineNumber + "\n";
        message += errorMessage;

        System.out.println(message);
        throw new Exception(message);
    }

}
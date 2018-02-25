package org.csvcompiler.parsetree;

import java.util.HashMap;
import java.util.Vector;

import org.csvcompiler.lexical.LexicalAnalayzer;
import org.csvcompiler.lexical.Token;


/**
 * Stage before Compilation and after LexicalAnalyze
 * <br>Serves as a container for the real parse Items
 * 
 * @author Onur
 */
public class ParseTree {
	
	/**Reference to LexicalAnalyzer*/
	public LexicalAnalayzer lexAnalyzer;	
    /**List of tokens coming from LexicalAnalyzer*/
    public Vector tokenTable;
    /**Last read token*/
    public Token currToken;
    /**List of statemtents finally constructed*/
    public StatementList statementList;
    /**A CSBag containing attributes of keys for compilation process*/
    //public CSBag keyAttributes;
    /**A HashMap containing attributes of keys for compilation process*/
    public HashMap keyHashMap;

    /**If key is to be compiled as a variable length text*/
    public static final String KEY_ALIGN_NONE="ALIGN_NONE";
    /**If key is to be compiled as aligned left (for compiling fixed-length keys)*/
    public static final String KEY_ALIGN_LEFT="ALIGN_LF";
    /**If key is to be compiled as aligned right (for compiling fixed-length keys)*/
    public static final String KEY_ALIGN_RIGHT="ALIGN_RG";

    /**
     * The HashMap keyAttributes version constructor
     * 
     * @param tokens list of token from LexicalAnalyze
     * @param keys A HashMap containing the attributes of Keys in the template file
     * @throws Exception
     */
    
    public ParseTree(LexicalAnalayzer pLexicalAnalayzer,HashMap keys)throws Exception {
    	lexAnalyzer=pLexicalAnalayzer;
        tokenTable=lexAnalyzer.tokenTable;
        keyHashMap=keys;
        statementList=new StatementList(this,"");
        int a=5;
    }
    

    /**
     * @return nextToken and removes it from the stack
     */
    public Token getNextToken() {
        return (Token)tokenTable.remove(0);
    }
    
    /**Useful for debugging*/
    public String toString() {
        return statementList.toString();
    }    

}

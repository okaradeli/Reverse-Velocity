package org.csvcompiler.parsetree;

/**
 * Parsing and validating the Foreach node
 * After validation
 * <br>1)The Container Name (ListName) of foreach is identified
 * <br>2)The List iterator  is identified
 * <br>3)The list of statements are built
 * 
 * @author Onur
 *
 */
public class Foreach {

    public Reference listItem;
    public Reference listName;
    public StatementList forEachStmList;
    private ParseTree parseTree;
    
    /**Constructor and entry point*/
    public Foreach(ParseTree tree)throws Exception{
        //record the param inside attributes
        parseTree=tree;
        //the next token must be the ( Left Paranthesis
        parseTree.currToken=parseTree.getNextToken();
        if(!parseTree.currToken.type.equals("LP")){
            System.out.println("( EXPECTED IN FOREACH STATEMENT");
            throw new Exception();
        }
        
        //then a list iteration variable
        parseTree.currToken=parseTree.getNextToken();
        listItem=new Reference(parseTree);
        
        //the next token must be the IN keyword
        parseTree.currToken=parseTree.getNextToken();
        if(!parseTree.currToken.type.equals("TEXT")|| !parseTree.currToken.value.trim().equals("in")){
            parserError("IN EXPECTED IN FOREACH STATEMENT");
            throw new Exception();
        }
        
        //then a LIST variable
        parseTree.currToken=parseTree.getNextToken();
        listName=new Reference(parseTree);
        
        //the next token must be the ) Right Paranthesis
        parseTree.currToken=parseTree.getNextToken();
        if(!parseTree.currToken.type.equals("RP")){
            parserError(") EXPECTED IN FOREACH STATEMENT");
            throw new Exception();
        }
        
        //then a bunch of statements
        forEachStmList=new StatementList(parseTree,listName.referenceName);
        
        //then the END keyword of foreach
        if(!parseTree.currToken.type.equals("END")){
            parserError("END EXPECTED IN FOREACH STATEMENT");
            return;
        }
        
    }
    
	/**error reporter for Parser*/
	public void parserError(String errorMessage)throws Exception{
	    String message="";
	    message+="PARSER_ERROR:Cant build the parse tree";
	    message+="The template file is not compatible with the Velocity Language\n";
	    message+="Line:"+parseTree.currToken.line;
	    
	    //System.out.println(message);
	    throw new Exception(message);
	}
    
	
	

    /** Useful for debugging */
    public String toString() {
        String str="";        
        str+="LIST_NAME:"+listName.toString()+"\n";
        str+="LIST_VARIABLE:"+listItem.toString()+"\n";
        str+="LIST_STATEMENTS:\n"+forEachStmList.toStringWithIndentation(2);
        
        return str;
    }    
}

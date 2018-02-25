package org.csvcompiler.parsetree;

import java.util.Iterator;
import java.util.Vector;


/**
 * A template can contain a StatementList
 * also other elements such as FOREACH can contain StatementList
 * StatementList includes list of statements to be processed sequentially 
 * by the compiler
 * 
 * 
 * @author Onur
 * 
 */
public class StatementList {
    public Vector statements = new Vector();

    public StatementList(ParseTree parseTree,String listName) throws Exception{
        parseTree.currToken=parseTree.getNextToken();
        try {
            while(true){
	            if (parseTree.currToken.type.equals("TEXT")) {
	                statements.add(new Text(parseTree));
	                parseTree.currToken = parseTree.getNextToken();
	            } else if (parseTree.currToken.type.equals("REFERENCE")) {
	                statements.add(new Reference(parseTree,parseTree.keyHashMap));
	                parseTree.currToken = parseTree.getNextToken();
	            } else if (parseTree.currToken.type.equals("FOREACH")) {
	                statements.add(new Foreach(parseTree));
	                parseTree.currToken = parseTree.getNextToken();
	            } else if (parseTree.currToken.type.equals("LIST_ITEM")) {
	                statements.add(new ListItem(parseTree,listName));
	                parseTree.currToken = parseTree.getNextToken();
	            }
	            else if (parseTree.currToken.type.equals("END") || 
	                    (parseTree.currToken.type.equals("EOF"))) {
	                return;
	            }
	            else{
	                System.out.println("PARSER_ERROR:When parsing template file");
	                System.out.println("Unknown token ("+parseTree.currToken.type+") line=("+parseTree.currToken.line+")");
	            }
            }
        } catch (Exception e) {
            //System.out.println("PARSER_ERROR:Unidentified error while parsing template file");
            throw new Exception(e);
        }
    }
    
    /**Useful for debugging*/
    public String toString() {
        String str="";
        Iterator it=statements.iterator();
        
        int statementIndex=0;
        while(it.hasNext()){
            Object statement=(Object)it.next();
            str+=statementIndex+")"+statement.toString()+"\n";
            statementIndex++;
        }
        return str;
    }   
    /**Helper method for toString*/
    public String toStringWithIndentation(int spaceNumber){
        String space="";
        for(int i=0;i<spaceNumber;i++)
            space+=" ";       
        
        String str="";
        Iterator it=statements.iterator();
        
        int statementIndex=0;
        while(it.hasNext()){
            Object statement=(Object)it.next();
            str+=space+statementIndex+")"+statement.toString()+"\n";
            statementIndex++;
        }
        return str;
        
    }

}

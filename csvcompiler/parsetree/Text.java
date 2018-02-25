package org.csvcompiler.parsetree;

/**
 * Includes the Static text (i.e. 'Bu dosyanin icerigi')
 * that will be matched to the static text coming as input.
 * 
 * @author Onur
 *
 */
public class Text {
    /**The static content text*/
    public String content="";
    
    /**The only constructor*/
    public Text(ParseTree parseTree)throws Exception{
        content=parseTree.currToken.value;
    }
    
    /**Useful for debugging*/
    public String toString() {
        return content;
    }    
}

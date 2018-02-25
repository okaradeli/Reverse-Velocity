package org.csvcompiler.parsetree;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A Variable or Fixed-Length string to be replaced with text coming as input
 * in the compilation phase
 * 
 * If this is fixed length key the following keys are applied
 * Example: the keyLength=15 keyAlignment=RIGHT and keyFillCharacter='*'
 *    and the following input comes '***TRAKBNK56789'
 *    then the actual key value is extracted to be TRAKBNK56789  
 * @author Onur
 *
 */
public class Reference {

    /**How many characters to read from the input stream to get the raw data of key*/
    public int keyLength;
    /**After reading the raw data, which part of the string the key resides,LEFT,RIGHT or none*/
    public String keyAlignment;
    /**What is the filling character? ('*','#'...) */
    public String keyFillCharacter;
    /**Is keyFixedLength or not*/
    public boolean keyFixedLength;
    /**The of the reference key, will be used when compiling i.e $TODAY*/
    public String referenceName;
    /**seperator for the reference*/
    public String seperator;
    
    
//    public Reference(ParseTree parseTree)throws Exception{
//        referenceName=parseTree.currToken.value;
//        
//        keyLength=parseTree.keyAttributes.get(referenceName,0,"LENGTH").toSimpleInt();
//        keyAlignment=parseTree.keyAttributes.get(referenceName,0,"ALIGNMENT").toString();
//        keyFillCharacter=parseTree.keyAttributes.get(referenceName,0,"FILL_CHAR").toString();
//        if(keyLength>0)keyFixed=true;
//        else keyFixed=false;
//        
//    }
    
    /**The default contructor when Reference does involve in compilation
     * i.e. listIterator name in the foreach loop
     */
    public Reference(ParseTree parseTree)throws Exception{
        referenceName=parseTree.currToken.value;
    }
    
    /**
     * key attributes of the Reference are defined at this point
     * i.e. LENGTH,ALIGNMENT,FILL_CHAR
     * 
     * @param parseTree to get the next token
     * @param keyHashMap HashMap containing the key attributes
     * @throws Exception
     */
    public Reference(ParseTree parseTree,HashMap keyHashMap)throws Exception{
        referenceName=parseTree.currToken.value;
        seperator=parseTree.currToken.seperator;
        
        //get the attributes of the reference
        HashMap item=null;
        try {
            ArrayList arrayList=(ArrayList)keyHashMap.get(referenceName);
            item=(HashMap)arrayList.get(0);
            //item = (ArrayList)keyHashMap.get(referenceName);
        } catch (Exception e) {            
            e.printStackTrace();//throw new CSException(0,referenceName+" isimli anahtar Transfer tan?m?nda mevcut de?ildir.");
        }
        try {
            keyLength=Integer.parseInt(item.get("COLUMN_LENGTH").toString());
            keyAlignment=item.get("COLUMN_ALIGNMENT").toString();
            keyFillCharacter=item.get("FILL_CHARACTER").toString();
            if(keyLength>0)keyFixedLength=true;
            else keyFixedLength=false;
        } catch (Exception e1) {
            throw new Exception(e1);
            //throw new CSException(0,referenceName+" kolonun tan?m?nda hata mevcuttur.\nTransfer Kolon tan?mlar?n? kontrol ediniz.");         
        }
        
    }
    
    /**Useful for debugging*/
    public String toString() {
        return "$"+referenceName;
    }
    

}

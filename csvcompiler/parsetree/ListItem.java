package org.csvcompiler.parsetree;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * If this is fixed length key the following keys are applied Example: the
 * keyLength=15 keyAlignment=RIGHT and keyFillCharacter='*' and the following
 * input comes '***TRAKBNK56789' then the actual key value is extracted to be
 * TRAKBNK56789
 * 
 * @author Onur
 * 
 */
public class ListItem {
    /** Is this key variable length or fixed length */
    public boolean keyFixedLength;

    /** How many characters to read from the input stream to get the raw data of key */
    public int keyLength;

    /**After reading the raw data, which part of the string the key resides,LEFT,RIGHT or none*/
    public String keyAlignment;

    /** What is the filling character? ('*','#'...) */
    public String keyFillCharacter;

    /** The name of the item */
    public String itemName = "";

    /**The name of the container i.e. /**$listItem.$TODAY implies itemName=TODAY,listName=LIST */
    public String listName = "";
    /***/
    public String seperator=null;
    

    /**
     * It creates and initializes the properties (i.e. FIXED/VARIABLE LENGTH,ALIGNMENT)
     * of the List_Item
     * 
     *  @param parseTree Including the Item Attributes :keyHashMap
     *  @param listNameParam Is the name of the list (note it has to be given as parameter, this method does not know the lists name)
     */
    public ListItem(ParseTree parseTree, String listNameParam) throws Exception {
        itemName = parseTree.currToken.value;
        seperator= parseTree.currToken.seperator;
        listName = listNameParam;

        // get the attributes of the list item
        ArrayList detailsArray = (ArrayList) parseTree.keyHashMap.get(listName);
        if(detailsArray==null){
            //throw new CSException(0,listName+" isimli anahtar tan?m? bulunamad?");
            throw new Exception(listName+" isimli anahtar tan?m? bulunamad?");
        }

        for (int i = 0; i < detailsArray.size(); i++) {
        //for (int i = 0; i < 100; i++) {
            HashMap item = (HashMap) detailsArray.get(i);
            if (item.get("COLUMN_NAME").toString().equals(itemName)) { // we found the columnAttribute index
                keyLength =  Integer.parseInt(item.get("COLUMN_LENGTH").toString());
                keyAlignment = (String) item.get("COLUMN_ALIGNMENT").toString();
                keyFillCharacter = (String) item.get("FILL_CHARACTER").toString();
                if (keyLength > 0)
                    keyFixedLength = true;
                else
                    keyFixedLength = false;
                break;//no more to continue
            }
        }
    }

    /**
     * Useful for debugging
     */
    public String toString() {
        return "$" + itemName;
    }

}

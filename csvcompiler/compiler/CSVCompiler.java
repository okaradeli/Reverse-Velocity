package org.csvcompiler.compiler;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.velocity.VelocityContext;
import org.csvcompiler.lexical.LexicalAnalayzer;
import org.outersystemtransfer.csvcompiler.parsetree.ParseTree;
import org.csvcompiler.parsetree.Reference;

/**
 * CSVCompiler v:1.0 -javadocs and HashMap key attributes added
 * <br>This utulity packages compiles a given Input file
 * <br>and creates a HashMap
 * <br>
 * <br>The language of the text must comform to the given Template file
 * <br>  written in Velocity Template Engine format  
 * <br>  For more information refer to Velocity Template Engine Guide
 * <br>  and Velocity Reverse Compiler Documentation 
 * 
 *@author Onur Karadeli
 *  
 */
public class CSVCompiler {
	public HashMap outBag=null;//the extracted outBag from the given template & input file
	public Compiler compiler=null;
	
	/**
	 * The test method 
	 * @param args
	 * @throws Exception
	 */
    public static void main(String args[])throws Exception{
    }
    
	/**
     * @return
     */
    private static HashMap createHashMapAttributes() {
        HashMap keyHashMap=new HashMap();
        
        HashMap hm11=new HashMap();
        hm11.put("COLUMN_CONTAINER_NAME","");
        hm11.put("COLUMN_NAME","MY_HEADER");
        hm11.put("COLUMN_LENGTH",new Integer(0));
        hm11.put("COLUMN_ALIGNMENT",ParseTree.KEY_ALIGN_NONE);
        hm11.put("FILL_CHARACTER","");
        
        HashMap hm12=new HashMap();
        hm12.put("COLUMN_CONTAINER_NAME","");
        hm12.put("COLUMN_NAME","MY_FOOTER");
        hm12.put("COLUMN_LENGTH",new Integer(0));
        hm12.put("COLUMN_ALIGNMENT",ParseTree.KEY_ALIGN_NONE);
        hm12.put("FILL_CHARACTER","");
        

        HashMap hm1A=new HashMap();
        hm1A.put("COLUMN_CONTAINER_NAME","MY_TABLE");
        hm1A.put("COLUMN_NAME","MY_DETAIL1");
        hm1A.put("COLUMN_LENGTH",new Integer(3));
        hm1A.put("COLUMN_ALIGNMENT",ParseTree.KEY_ALIGN_RIGHT);
        hm1A.put("FILL_CHARACTER"," ");

        
        HashMap hm1B=new HashMap();
        hm1B.put("COLUMN_CONTAINER_NAME","MY_TABLE");
        hm1B.put("COLUMN_NAME","MY_DETAIL2");
        hm1B.put("COLUMN_LENGTH",new Integer(3));
        hm1B.put("COLUMN_ALIGNMENT",ParseTree.KEY_ALIGN_LEFT);
        hm1B.put("FILL_CHARACTER","*");
        
        
        ArrayList detailsArray=new ArrayList();
        detailsArray.add(hm1A);
        detailsArray.add(hm1B);
        
        ArrayList headerArray=new ArrayList();
        headerArray.add(hm11);

        ArrayList footerArray=new ArrayList();
        footerArray.add(hm12);

        
        keyHashMap.put("MY_HEADER",headerArray);
        keyHashMap.put("MY_FOOTER",footerArray);
        keyHashMap.put("MY_TABLE",detailsArray);
        
        return keyHashMap;
    }

    /**The constructor and the entry point  of CSVCompiler
	 * <br>1)Creates a {@link LexicalAnalayzer}
	 * <br>2)Then {@link ParseTree}
	 * <br>3)Then {@link Compiler}
	 * <br>4)Convert it to a HashMap
	 * @param templateInput
	 * @param textInput
	 * @throws Exception
	 * 
	 */
	//public CSVCompiler(DataInputStream templateInput,DataInputStream textInput,HashMap keyAttributes)throws Exception{
    public CSVCompiler(Reader templateInput,Reader textInput,HashMap keyAttributes)throws Exception{
		try{
			System.out.println("[OUTER_COMPILER]Started");
			
		        HashMap keyAttributesHashMap=keyAttributes;
		    
			LexicalAnalayzer lexAnalyzer=new LexicalAnalayzer(templateInput);//tokenize the template file (VM)
			
			
			ParseTree parseTree=new ParseTree(lexAnalyzer,keyAttributesHashMap);//create the template files(VM) parse Tree
			String test=parseTree.toString();
			compiler=new Compiler(textInput,parseTree);//use the created parse tree and analyze the input (CSV) file
			System.out.println("[OUTER_COMPILER]Finished");
			
			System.out.println("bag ready");
			//System.out.println(compiler.compilerOutput);
			int a=5;
		}catch(Exception e){
			int a=5;
			//throw new CSException(e);
		}
	}

    /**The method for displaying a HashMap on the debugger and so on
     * @param compilerOutput
     * @return formattedString
     */
    private Object toFormattedString(HashMap map) {
        String formattedString="";
		Iterator mapIterator=map.keySet().iterator();
		
		while(mapIterator.hasNext()){
			Object container=(Object)mapIterator.next();
			//This is a list of Items with row,key,value triples
			if(container instanceof Reference){
				String listName=((Reference)container).referenceName;
				
				HashMap listMap=(HashMap)map.get(container);
				Iterator listIterator=listMap.keySet().iterator();
				
				//iterator for rows of the list
				
				int listIndex=0;
				while(listIterator.hasNext()){
					
					Integer listItemIndex=(Integer)listIterator.next();
					HashMap listItemDetails=(HashMap)listMap.get(listItemIndex);

					//iterator for the Keys of the row
					Iterator keyIterator=listItemDetails.keySet().iterator();
					while(keyIterator.hasNext()){
						String key=(String)keyIterator.next();
						String value=(String)listItemDetails.get(key);
						//finally display the triple (LIST_NAME,ITEM_NAME,ITEM_VALUE) to the bag
						formattedString+=listName+"["+listItemIndex.intValue()+"]"+"["+key+"]="+value+"\n";				
					}
					listIndex++;
				}
								
			}
			//this is a Header-Footer type single variable
			else{
				String value=(String)map.get(container);
				formattedString+=(String)container+"="+value+"\n";				
				int a=5;
			}
			int a=5;			
		}
		return formattedString;
        
    }

    /**
     * This method takes parameters from the inBag and puts
     * them into the VelocityContext to be given the 
     * VelocityContextEngine. 
     * 
     * @param inBag	contains the KEYS,LISTS
     * @param context	The velocity context to be filled with KEYS,LISTS
     * @param keyAttributes How the fields are written to output file (i.e. Alignment,Fill Characters...)
     * @param transferDefOid If an object(OID) with the given transferDefOid is already transfered filter it out  
     * @throws CSException
     */
    public static void convertHashMapToVelocityHashMap(HashMap inBag,VelocityContext context,HashMap keyAttributes)throws CSException{
    		HashMap outBag=new HashMap();
		HashMap serviceBag = new HashMap();
		
		Enumeration keyEnum = inBag.getAllKeys();
		int index = 0;
		//for all keys (i.e. single keys such as TODAY or list type keys such as LIST,SUMMARY...)
		while (keyEnum.hasMoreElements()){
			String key = (String)keyEnum.nextElement();
			int dim = inBag.getDim(key);
			
			//if this is Single Key (i.e. TODAY,FILE_NAME)
			if (dim==0){
			    //get the properties of the column
			    int keyLength=keyAttributes.get(key,0,"COLUMN_LENGTH").toSimpleInt();
			    String keyAlignment=keyAttributes.get(key,0,"COLUMN_ALIGNMENT").toString();
			    String fillCharacter=keyAttributes.get(key,0,"FILL_CHARACTER").toString();
				String coreString=inBag.get(key).toString();
				String processedString=getAlignedAndFilledString(key,coreString,keyLength,keyAlignment,fillCharacter);
				context.put(key,processedString);
				index++;
			}
			//if this is an item in a List (i.e. in DETAILS list items: TYPE,DESCRIPTION,PRICE...)
			else if (dim==2){	
				ArrayList rowArrayList=new ArrayList();
							
				int size = inBag.getSize(key);
				HashMap keysInOutput=new HashMap();
				for (int rowIndex=0;rowIndex<size;rowIndex++){
					HashMap tableHashMap=new HashMap();
					
					//find columns for this table
					Hashtable columnsHT = inBag.getColumns(key, rowIndex);
					Enumeration columnEnum = columnsHT.keys();
					
				    int keyLength=0;
				    String keyAlignment="",keyFillCharacter="";
				    boolean isIncludedInVelocityOutput=false;
					
					while (columnEnum.hasMoreElements()){

					    String column = (String)columnEnum.nextElement();

					  	//only for once , find the properties of the column
					    //find the position of the key attribute
					    if(rowIndex==0){
					        for(int j=0;j<keyAttributes.getSize(key);j++){
					            String columnName=keyAttributes.get(key,j,"COLUMN_NAME").toString();
					            if(columnName.equals(column)){
					                keyLength=keyAttributes.get(key,j,"COLUMN_LENGTH").toSimpleInt();
					                if(keyLength>0){//dont look for alignment operations for keys that has Length<=0
						                keyAlignment=keyAttributes.get(key,j,"COLUMN_ALIGNMENT").toString();
						                keyFillCharacter=keyAttributes.get(key,j,"FILL_CHARACTER").toString();
					                }
					                //put to HashMap the key properties
					                KeyProperties keyProperties=new KeyProperties(key,columnName,keyLength,keyAlignment,keyFillCharacter);
					                keysInOutput.put(key+"_"+column,keyProperties);
					                break;
					            }
					        }
					    }
					    if(keysInOutput.containsKey(key+"_"+column)){
					        KeyProperties keyProperties=(KeyProperties)keysInOutput.get(key+"_"+column);
							//before putting calculate any alignment, fillment characters...
							String coreString=inBag.get(key,rowIndex,column).toString();
							String processedString=getAlignedAndFilledString(column,coreString,keyProperties.keyLength,keyProperties.keyAlignment,keyProperties.fillCharacter);
							tableHashMap.put(column,processedString);
					    }
					    else{//no need to put this item to the Velocity output, as it will not be rendered
					    }					    
					}
					rowArrayList.add(tableHashMap);
				}
				//finally put the hasmap into outBag
				context.put(key,rowArrayList);
			}
		}
    	return ;
    }

    /**
     * @param coreString
     * @param key
     * @param column
     * @return
     */
    private static String getAlignedAndFilledString(String key,String coreString,int keyLength,String keyAlignment,String keyFillCharacter)throws CSException {
        String processedString=coreString;
        if(coreString==null||coreString.equals("")){
            return "";//return an empty string for null or empty core strings(i.e. nothing to process)
        }
        if(keyLength<=0){
            return coreString;//return the coreString as is, when there is no fixed length (i.e. variable length)
        }
        
        if(keyAlignment.equals(ParseTree.KEY_ALIGN_LEFT)||keyAlignment.equals(ParseTree.KEY_ALIGN_NONE)){
            if(coreString.length()>keyLength){
                throw new CSException(0,"Maksimumum uzunluktan daha büyük bir veri ile karþýlaþýldý.\n" +
                		"Veri="+key+"\n"+ 
                		"Tanýmlanmýþ Uzunluk="+keyLength+
						"Gelen Deðer="+coreString
						);
            }
            for(int i=coreString.length();i<keyLength;i++){
                processedString=processedString+keyFillCharacter;
            }
        }
        if(keyAlignment.equals(ParseTree.KEY_ALIGN_RIGHT)){
            if(coreString.length()>keyLength){
                throw new CSException(0,"Maksimumum uzunluktan daha büyük bir veri ile karþýlaþýldý.\n" +
                		"Veri="+key+"\n"+ 
                		"Tanýmlanmýþ Uzunluk="+keyLength+"\n"+
						"Gelen Deðer="+coreString
						);
            }
            for(int i=keyLength-coreString.length()-1;i>=0;i--){
                processedString=keyFillCharacter+processedString;
            }
        }
        
        
        
        //first get the properties of the Key
        
        return processedString;
    }

	


	
	
    /**The generic version of the CSVCompiler constructor
     * 
	 * @param templateInput -A valid Velocity template file (vm)
	 * @param textInput     -An input file conforming to the above Velocity template
	 * @param keyAttributes -To define if keys are fixed or vaiable length,left-aligned and so on 
	 * @throws Exception
	 * 
	 */
	
	public CSVCompiler(Reader templateInput,Reader textInput,HashMap keyAttributes)throws Exception{
	}	
	
}


/**
 * A Temporary class to store elements inside a hashMap
 * When looking for Key Additonal Attributes
 */
class KeyProperties{
    String keyContainerName="";
    String keyName="";
    int keyLength=0;
    String keyAlignment=""; 
    String fillCharacter="";
    
    public KeyProperties(String containerName,String name,int length,String alignment,String fillChar){
        keyContainerName=containerName;
        keyName=name;
        keyLength=length;
        keyAlignment=alignment;
        fillCharacter=fillChar;
    }
}


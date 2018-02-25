# Reverse-Velocity
A text to Hashmap API ( reverse ) for the popular Apache Velocity API 






Reverse Compiler 
for
Velocity Template Engine
1.0










Onur Karadeli

January 2005



Table of Contents

1.	Introduction	3
1.1	What is Reverse Compiler?	3
1.2	Who should use?	4
2.	The Reverse Compiler Language	5
3.	Reverse Compiler	5
3.1	Lexical Analyzer	5
3.2	Parser	6
3.3	Compiler	6
Text Statement:	6
List Statement	8
Foreach Statement	9
4.	Additional Key Format Parameters	10
5.	File Types and Reverse Compiler	11
5.1	XML	11
6	A Complete Example	12
7.	References	14

1.	Introduction

        1.1 What is Reverse Compiler?

You use the Velocity Template Engine to create templates and fill this template with data dynamically.



	




Reverse Compiler lets you retrieve the contents of this data by extracting the contents with a given Velocity Template and an InputStream (File etc)


	





Before understanding how reverse compiler works , you should be familiar with the Velocity Template Engine.

A Guide and a Short Tutorial to Velocity
http://www.javaworld.com/javaworld/jw-12-2001/jw-1228-velocity.html

Full Documentation of Velocity
http://jakarta.apache.org/velocity/user-guide.html

Community Mailing Lists of Velocity
http://mail-archives.apache.org/eyebrowse/SummarizeList?listId=103
1.2	Who should use?
If you somehow transfer your internal data to an outer system (i.e. an other institution, partner) as a file (XML,CSV...) and you need the same type of file to be an input to your system , you need a parser. 

Following are some messages (files including information) that are parsed in classical way.




    2 






    3 





If you write a parser Code (i.e a class, a method...). Whenever a simple change in these messages occur (i.e. Customer Code is now 10 characters instead of 8 characters) you have to revise the code and build up your project again.

Furthermore you have to write a parser for each of these message types.

The Reverse Compiler creates a layer between the input file and the actual operation method (i.e. Customer Creator Method) and frees this operation method from the parse operation of data.






2.	The Reverse Compiler Language

The velocity template engine is based on a language called Velocity Template Language (VTL) below is the detailed specification in BNF of VTL.

http://jakarta.apache.org/velocity/specification-bnf.html

The Reverse compiler is based on a small subset of VTL and only includes the following entities.






















3.	Reverse Compiler


Reverse compilation is the process of getting data from an input file and extracting the usable information from it.

It is composed of 3 stages; Lexical Analyze , Parse and Compilation.
3.1	Lexical Analyzer 

Lexical Analyze is the process of getting texts, variables and language materials so that a parsing and logical validation can take place. After Lexical Analyze all the Template text are converted into Tokens that can be one of the following.
REFERENCE, LIST_ITEM, STATIC_TEXT, FOREACH, IN, LP, RP, EOF, NULL.

TokenList is a list of tokens that will contain all the tokens.
In case of syntactic errors (i.e. empty identifer names... ) the error line is reported and no further processing takes place.

3.2	Parser

Parser takes input of tokenList (also optionally additional key attributes), validates and creates a tree like structure to be compiled and evaluated values at run time. 

It also takes a hashmap containing the list of additonal key attributes so that in compile time the text is parsed accordingly. The additional key attributes will be discussed later.

The parser validates logically the language elements to conform to RCTL (Reverse Compiler Template Language) as defined in section 3.

Any kind of error is reported along with the reason and no further processing takes place. 

When parser successfully completes it produces a StatementList structure (refer to RCTL in section 3) to be used by the compiler.

3.3	Compiler

Compiler takes input of a StatementList structure and an input source file.

The process is to retrieve each statements from the statementList one by one than matching/filling the contents of the statements by extracting data from the input source file.

Text Statement: Static text that is defined in the template and should match exactly to the input source file. 





When a mismatch occurs it is reported as " X text is expected and Y text is found at line N". (Note a mismatch may be caused from other language elements look below)

After Text statements are compiled they are completely discarded and are not put into the result(output)
Reference Statement: The value of the reference statement could only be a text alpha-numeric text which can not include TEMPLATE_CHARACTERS as defined in appendix.
The reference statements are converted to 2-pair items in the resultbag.

Example: A reference TODAY can be compiled and the following structure may be created in compile time.



 






After a reference statement is compiled the following entry is put the output.

output.put("TODAY","20050201"); 



List Statement: List Item statements can be inside foreach statements only. They are compiled to 3-pair of items in the result (output). (4-pair items when index no is included)

Example: A listItem $item.AMOUNT can be compiled and the following structure may be created in compile time.











After a list Item is compiled the following entry is put to the output.

output.put("DETAILS",(0,AMOUNT,25))
Foreach Statement: For each statements are used to parse and compile repeating items, lines and so on. The terminating condition of a foreach loop is mismatch (i.e. when expecting a text cant find the appropriate text) when a mismatch occurs , the foreach statement is ended.

Example: A foreach has several attributes.






































4.	Additional Key Format Parameters

When extracting data from the input file, sometimes it is good to further parse the extraced text block. Such additional parsing lets trimming white spaces, identify fixed length texts, get texts that are aligned to right and so on.

Lets say a Reference TRANSFER_AMOUNT  is defined to be maximum 10 characters wide, it is aligned to left and the empty characters are filled with '*' character.

We can define the properties of this Key as

Container Name
Key Name
Length
Alignment
Fill Character
-
TRANSFER_AMOUNT
10
LEFT
*

While parsing lets say we encounter the following text

*****15000	

Without this additional key format parameters the Reverse Compiler will put the text as is (i.e. with the prefix '*' characters)

With this additonal key format parameters the Reverse Compiler will further process the text *****15000 and convert it to 15000

Typically when the length of the variables are fixed it is called fixed-length(i.e. it must reside in 12 chars place) the additional key format parameters are used, when there is no key format parameter exists for this variable it is called variable-length key and will match from the input as much text as it can (refer to Reference structrure in Reverse Compiler section)

ContainerName	:Name of the list (typically for items in foreach loops)
KeyName		:Name of the key (i.e. TRANSFER_AMOUNT)
Length		:Numeric value if >0 it is fixed length if <=0 it is variable length
Alignment		:For fixed-length keys only. Where to look for characters to trim 			possible values  (LEFT,RIGHT,NONE)
Fill-Character		:For fixed-length keys only which characters to trim.
			
			
5.	File Types and Reverse Compiler

Theoritically Reverse Compiler parses every type of file as long as they are well-defined in the template file.  (CSV,XML,TXT,DAT...) and the input file conforms to it.

There are however a few key notes to consider when parsing files of type, 

5.1	XML

    • XML files are hierarchical structured elements thus the same XML file may be composed with different orders.

example the following are both valid and equivalent XML structures.

<header>
   <detail1>A</detail1>
   <detail2>B</details2>
<header/>
<header>
   <detail2>B</detail2>
   <detail1>A</details1>
<header/>

In reverse compiler the order of elements are also important thus , only one of them can be defined and parsed accordingly.

    • XML files may contain elements that are optional (i.e. may not exists)

<header>
  <mandatory>A<mandatory>
<header>
<header>
   <mandatory>A</mandatory>
    <optional>B</optiaonal>
<header>

For Reverse Compiler the mandatory elements must exists in the input file when they are defined in the template file and must not exist in the input file when they are not defined in the template file. There is not an automatic way of eliminating optional elements.

    • XML files may contain whitespace that are totally ignored (i.e. tab,space,line feed)

For Reverse Compiler they are important and is a part of Text  element (refer to Section Compiler->Text)

In the next verison of Reverse Compiler however this property will be added. (i.e. if Template Type is XML, all template and input file white characters will be discarded automatically)
6	A Complete Example

Remember the Compilation (Velocity Engine) and Reverse Compilation charts in section 1. Below are the elements and contents of these processes. Note that the HashMap structure in Java is depicted as a table. 





















































































7.	References

Below are some usable links to Velocity Template engine

A Guide and a Short Tutorial to Velocity
http://www.javaworld.com/javaworld/jw-12-2001/jw-1228-velocity.html

Full Documentation of Velocity
http://jakarta.apache.org/velocity/user-guide.html

Community Mailing Lists of Velocity
http://mail-archives.apache.org/eyebrowse/SummarizeList?listId=103

Specification of Velocity Template Language (VTL) in BNF.
http://jakarta.apache.org/velocity/specification-bnf.html


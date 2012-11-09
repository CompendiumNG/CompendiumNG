/********************************************************************************
 *                                                                              *
 *  (c) Copyright 2010 Verizon Communications USA and The Open University UK    *
 *                                                                              *
 *  This software is freely distributed in accordance with                      *
 *  the GNU Lesser General Public (LGPL) license, version 3 or later            *
 *  as published by the Free Software Foundation.                               *
 *  For details see LGPL: http://www.fsf.org/licensing/licenses/lgpl.html       *
 *               and GPL: http://www.fsf.org/licensing/licenses/gpl-3.0.html    *
 *                                                                              *
 *  This software is provided by the copyright holders and contributors "as is" *
 *  and any express or implied warranties, including, but not limited to, the   *
 *  implied warranties of merchantability and fitness for a particular purpose  *
 *  are disclaimed. In no event shall the copyright owner or contributors be    *
 *  liable for any direct, indirect, incidental, special, exemplary, or         *
 *  consequential damages (including, but not limited to, procurement of        *
 *  substitute goods or services; loss of use, data, or profits; or business    *
 *  interruption) however caused and on any theory of liability, whether in     *
 *  contract, strict liability, or tort (including negligence or otherwise)     *
 *  arising in any way out of the use of this software, even if advised of the  *
 *  possibility of such damage.                                                 *
 *                                                                              *
 ********************************************************************************/

package com.compendium.io.questmap;

import java.io.*;

import com.compendium.io.questmap.util.*;
import com.compendium.ui.*;
import com.compendium.ProjectCompendium;

/**
 * class Lexer
 *
 * Description:
 *      Lexer is a lexical analyser for Brahms models. The lexer is
 *      used by the Yacc parser to retrieve tokens from the input. 
 *      The lexer skips white spaces. The lexer can return the
 *      following tokens:
 *          INT             token for an integer
 *          STRING          token for a string starting with '"' and ending with '"'
 *          ID              token for identifiers
 *          num             token for keywords and single character symbols (like ';')
 *      each token type has a unique code associated with it. Each keyword and
 *      character symbol is also uniquely identified with a code. The codes for
 *      keywords and characters symbols are loaded in the tables KeywordTable
 *      and CharSymbolTable. The token codes are loaded in the table TokenTable.
 *
 * @see     com.compendium.io.questmap.TokenTable
 * @see     com.compendium.io.questmap.KeywordTable
 * @see		com.compendium.io.questmap.CharSymbolTable
 * @see     com.compendium.io.questmap.TokenTable
 * @author  Ron van Hoof
 */
public class Lexer {

  // constants
  protected static final int EOF_CHAR = -1;
  protected static final int EOF = 0;
  protected static final int ERROR = -1;

  // attributes
  protected TokenTable tokens = null;               // tokens INT, DOUBLE, ID, STRING, BOOLEAN, RELOP
  protected KeywordTable keywords = null;           // all keywords
  protected CharSymbolTable charSymbols = null;     // character symbols ('(', ')', etc)
  protected String file = "";                       // file to be analysed
  protected FileInputStream input = null;
  protected int nextChar = EOF_CHAR;                // first lookahead character
  protected int nextChar2 = EOF_CHAR;               // second lookahead character
  protected int line = 1;                           // line currently analysed
  protected int pos = 1;                            // character position in line
  protected String yytext = "";                     // The last token read
  protected int yyleng = 0;                         // the length of yytext

  /**
   * Lexer(TokenTable tkns,
   *       KeywordTable kt,
   *       CharSymbolTable cst,
   *       String fileName,
   *       ErrorLog el) throws IOException
   *
   * Description:
   *    Creates a new lexical analyser to analyse the given file using
   *    the given tables. Errors will be added to the given ErrorLog.
   */
  public Lexer( TokenTable tkns,
				KeywordTable kt,
								CharSymbolTable cst,
				String fileName) throws IOException {
	// initialize attributes
	tokens = tkns;
	keywords = kt;
		charSymbols = cst;
	file = fileName;
	// initialize the file
	init();
  } // Lexer  

  public void close() {
	try {
	  input.close();
	} catch (IOException e) {
		ProjectCompendium.APP.displayError(
			"Error closing file: "+file +
			"." + e.getMessage());
	} // end try
  } // close    

  /**
   * void init() throws IOException
   *
   * Description:
   *    Opens the file for analysis and reads the lookahead characters
   */
  protected void init() throws IOException {

	// open the file to analyse
	input = new FileInputStream(file);

	// read two lookahead characters
	nextChar = input.read();
	if (nextChar == EOF_CHAR) {
	  nextChar2 = EOF_CHAR;
	} else {
	  nextChar2 = input.read();
	} // end if
  } // init  

  /**
   * void advance() throws IOException
   *
   * Description:
   *    Reads the next character from the input file and updates
   *    the position counters for the line and character position
   */
  protected void advance() throws IOException {
	int curChar;

	// update the two lookahead characters, the second lookahead will
	// contain the new character.
	curChar = nextChar;
	nextChar = nextChar2;
	if (nextChar == EOF_CHAR) {
	  nextChar2 = EOF_CHAR;
	  close();
	} else {
	  nextChar2 = input.read();
	} // end if

	// set the file position
	pos++;
	if (curChar == '\n') {
	  line++;
	  pos = 1;
			// log.addMessage(new Message("Parser line: "+line));
	} // end if
  } // advance  

  /**
   * int getLine()
   *
   * Description:
   *    Returns the line number of the line being analysed.
   */
  public int getLine() {
	return line;
  } // getLine  

  /**
   * int getPos()
   *
   * Description:
   *    Returns the character position in the line being analysed.
   */
  public int getPos() {
	return pos;
  } // getPos  

  public String getYYText() {
	return yytext;
  } // getYYText  
  
  public int getYYLeng() {
	return yyleng;
  } // getYYLeng  

  /**
   * int nextToken() throws IOException
   *
   * Description:
   *    Returns the next token in the input. This method skips white spaces
   *    and comments and can return one of the following tokens:
   *        INT
   *        DOUBLE
   *        BOOLEAN
   *        STRING
   *        ID
   *        num     // representing a keyword or character
   */
  public int nextToken(Union yylval) throws IOException {
	int symNum;     // symbol number

	for(;;) {
	  // skip white spaces
	  if (isWhiteSpace(nextChar)) {
		skipWhiteSpace();
	  } // end if

	  // check for string
	  if (nextChar == '"') {
		return readString(yylval);
	  } // end if

	  // check for numbers (unsigned, integer, float)
	  if (isDigit(nextChar) ||
		  ((nextChar == '+' || nextChar == '-' ||
		  nextChar == '.') && isDigit(nextChar2))) {
		return readNumber(yylval);
	  } // end if

	  // check for a single character symbol
	  symNum = charSymbols.isCharSymbol(nextChar);
	  if (symNum != -1) {
		// it is a character symbol, advance and return token
		yytext = (new Character((char)nextChar)).toString();
		yyleng = 1;
		advance();
		return symNum;
	  } // end if

	  // check for keyword, identifier
	  if (isLetter(nextChar)) {
		return readIdentifier(yylval);
	  } // end if

	  // check for EOF
	  if (nextChar == EOF_CHAR) {
		return EOF;
	  } // end if
		
		ProjectCompendium.APP.displayError("Error reading file: " + file +  " (Cant Recognize Format)", "File Import.. ");
	
	  // unidentified character, generate error and ignore it
	  System.out.println("Unidentified character '" +
		new Character((char)nextChar) + "'(" + nextChar +
						 ")" + " line:" + line + " pos:" + pos + " file:" + file);
	  advance();
	} // end for
  } // nextToken  

  /**
   * boolean isWhiteSpace(int ch)
   *
   * Description:
   *    Returns whether or not the given character is a white space, meaning
   *    one of space, linefeed, carriage return, newline, or tab
   */
  protected boolean isWhiteSpace(int ch) {
	return (ch == ' ' || ch == '\n' ||
			ch == '\f' || ch == '\t' || ch == '\r');
  } // isWhiteSpace  

  /**
   * boolean isLetter(int ch)
   *
   * Description:
   *    Returns whether or not the given character is a letter, meaning
   *    one of 'a'-'z', 'A'-'Z', or '_'
   */
  protected boolean isLetter(int ch) {
	return (ch >= 'a' && ch <= 'z') ||
		   (ch >= 'A' && ch <= 'Z') ||
		   (ch == '_');
  } // isLetter  

  /**
   * boolean isDigit(int ch)
   *
   * Description:
   *    Returns whether or not the given character is a digit, meaning
   *    one of '0'-'9'.
   */
  protected boolean isDigit(int ch) {
	return (ch >= '0' && ch <= '9');
  } // isDigit  

  /**
   * boolean isIdChar(int ch)
   *
   * Description:
   *    Returns whether or not the given character is allowed to be
   *    in an identifier or keyword. Id characters can be a
   *    letter (see isLetter), digit (see isDigit), or '-'
   *
   * @see #isLetter
   * @see #isDigit
   */
  protected boolean isIdChar(int ch) {
	return isLetter(ch) || isDigit(ch) || ch == '-';
  } // isIdChar  

  /**
   * void skipWhiteSpace() throws IOException
   *
   * Description:
   *    Reads and skips all white space characters until a non
   *    white space character is read.
   */
  protected void skipWhiteSpace() throws IOException {
	// at entrance of this method nextChar == <whitespace>

	// read all whitespace
	do {
	  advance();
	} while (isWhiteSpace(nextChar));
  } // skipWhiteSpace  

  /**
   * int readString() throws IOException
   *
   * Description:
   *    Reads a string starting and ending with '"'. Returns a
   *    STRING token if it succeeded. It can return a token
   *    representing an EOF when the end of file is read and no
   *    closing '"' is found.
   */
  protected int readString(Union yylval) throws IOException {
	StringBuffer str = new StringBuffer();
	int startLine;
	int startPos;

	// read past the string opener '"'
	advance();

	startLine = line;
	startPos = pos;
	// store the string characters until the end of string marker
	while (!(nextChar == '"')) {
	  // if we read an EOF, then no end of string, error
	  if (nextChar == EOF_CHAR) {
		  System.out.println("String not terminated at end of file" + " line:" + startLine
							 + " pos:" + startPos + " file: " + file);
		yylval.sval = "";
		return EOF;
	  } // end if

			// check if we are dealing a double quote that is part of the string '\"'
			if (nextChar == '\\' && nextChar2 == '"') {
				str.append((char)nextChar);
				str.append((char)nextChar2);
				advance();
				advance();
			} else {
				// valid character, append to string, read next
				str.append((char)nextChar);
				advance();
			} // end if
	} // end while

	// read past the string closer '"'
	advance();

	// return the string token
	yylval.sval = str.toString();
	yytext = str.toString();
	yyleng = yytext.length();
	return tokens.getCode("STRING");
  } // readString  

  /**
   * int readNumber() throws IOException
   *
   * Description:
   *    Reads a number. Must be an integer.
   *    integer ::= {+|-}[digit]+
   *    Returns either an INT token or a token representing an
   *    error (ERROR).
   */
  protected int readNumber(Union yylval) throws IOException {
	StringBuffer num = new StringBuffer();

	// at entrance nextChar is one of <digit>, '+', '-', or '.'

	// first read the '+' or '-' sign if one exists
	if (nextChar == '+' || nextChar == '-') {
	  num.append((char)nextChar);
	  advance();
	} // end if

	// read digits if there are any
	while (isDigit(nextChar)) {
	  num.append((char)nextChar);
	  advance();
	} // end while

	// now we'll check if a white space follows, if not we
	// have an error in the number. We'll read the characters
	// till a whitespace or EOF is encountered.
	if (isWhiteSpace(nextChar) || nextChar == EOF_CHAR ||
		charSymbols.isCharSymbol(nextChar) >= 0) {
	  // return int token
	  yytext = num.toString();
	  yyleng = yytext.length();
	  yylval.ival = (new Integer(num.toString())).intValue();
	  return tokens.getCode("INT");
	} else {
	  // error, other characters found in number
	  System.out.println("Invalid character in integer" +
						 " line:" + line + " pos:" + pos +
						 " file:" + file);

	  // skip invalid characters
	  while (!isWhiteSpace(nextChar) && nextChar != EOF_CHAR &&
			 charSymbols.isCharSymbol(nextChar) == -1) {
		advance();
	  } // end while

	  return ERROR;
	} // end if
  } // readNumber  

  /**
   * int readIdentifier() throws IOException
   *
   * Description:
   *    Reads an identifier, which can either be a keyword or
   *    a real identifier.
   *        identifier ::= [letter][letter|digit|'-']*
   *    Returns either an ID token (in case of an identifier) or an integer
   *    representing the keyword.
   */
  protected int readIdentifier(Union yylval) throws IOException {
	StringBuffer str = new StringBuffer();
	String id;
	int keywordNum;

	// at entrance of this method nextChar is a letter
	str.append((char)nextChar);
	advance();

	// read identifier characters
	while(isIdChar(nextChar)) {
	  str.append((char)nextChar);
	  advance();
	} // end while

	// check if identifier is a keyword
	id = str.toString();
	keywordNum = keywords.isKeyword(id);
	if (keywordNum >= 0) {
	  // keyword
	  yytext = id;
	  yyleng = id.length();
	  return keywordNum;
	} else {
	  // is an identifier
	  yytext = id;
	  yyleng = id.length();
	  yylval.sval = id;
	  return tokens.getCode("ID");
	} // end if
  } // readIdentifier  

}

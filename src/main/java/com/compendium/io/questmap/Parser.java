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

// This file created by BYACC 1.8(/Java extension  0.1)
// Java capabilities added 7 Jan 97, Bob Jamison
// Please send bug reports to rjamison@lincom-asg.com
// static char yysccsid[] = "@(#)Parser	1.8 (Berkeley) 01/20/90";

//line 50 "D:\Java\nynexst\projectcompendium\parser\parser.y"

package com.compendium.io.questmap;

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

import com.compendium.core.datamodel.*;
import com.compendium.core.ICoreConstants;

import com.compendium.*;
import com.compendium.io.questmap.util.*;
import com.compendium.ui.*;
import com.compendium.ui.dialogs.UIProgressDialog;
import com.compendium.ui.plaf.*;
import com.compendium.core.db.*;

//********* TO DO: Redo the parser to work with the remote datamodel ********

// class: Parser
// does : encapsulates yacc() parser functionality in a Java
//        class for quick code development

public class Parser extends Thread {

	boolean yydebug;        //do I want debug output?
	int yynerrs;            //number of errors so far
	int yyerrflag;          //was there an error?
	int yychar;             //the current working character
	String yytext;          //string buffer for use by yylex()

	final static int YYSTACKSIZE = 500;  //maximum stack size
	int statestk[],stateptr;             //state stack

	// SEMANTIC VALUES type:int
	Union yyval;           //used to return semantic vals from action routines
	Union yylval;          //the 'lval' (result) I got from yylex()
	Union valstk[];
	int valptr;

	public final static short ID=257;
	public final static short STRING=258;
	public final static short INT=259;
	public final static short POSNODE=260;
	public final static short ISSUENODE=261;
	public final static short ARGNODE=262;
	public final static short DECISIONNODE=263;
	public final static short NOTENODE=264;
	public final static short REFNODE=265;
	public final static short MAPVIEW=266;
	public final static short LISTVIEW=267;
	public final static short IDENT=268;
	public final static short AUTHOR=269;
	public final static short CRDATE=270;
	public final static short MODDATE=271;
	public final static short LABEL=272;
	public final static short DETAIL=273;
	public final static short REFPATH=274;
	public final static short XPOS=275;
	public final static short YPOS=276;
	public final static short POS=277;
	public final static short FROMID=278;
	public final static short TOID=279;
	public final static short YYERRCODE=256;

	final static short yylhs[] = {                           -1,
		0,    0,   24,   25,   25,   25,   25,   25,   25,   25,
	   25,   25,    6,   16,    1,   17,   18,    2,    3,   15,
	   15,   19,   20,   21,    7,    8,    9,   10,   11,    4,
	   12,   13,   14,    5,   22,   23,
	};

	final static short yylen[] = {                            2,
		1,    2,    3,    1,    1,    1,    1,    1,    1,    1,
		1,    1,    8,    4,    4,    4,    4,    4,    4,    2,
		1,    4,    4,    4,    8,    8,    8,    8,    9,    4,
		8,    8,    4,    1,    4,    4,
	};

	final static short yydefred[] = {                         0,
		0,    0,    1,   34,    0,    0,    0,    0,    0,    0,
		0,    0,    0,    4,    5,    6,    7,    8,    9,   10,
	   11,   12,    0,    2,    0,    0,    0,    0,    0,    0,
		0,    0,    0,    0,    3,    0,    0,    0,    0,    0,
		0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
		0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
	   33,   14,    0,    0,    0,    0,    0,    0,    0,    0,
		0,    0,    0,    0,    0,   15,    0,    0,    0,    0,
		0,    0,    0,    0,    0,    0,    0,   35,    0,   16,
		0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
		0,    0,   36,   17,    0,    0,    0,   13,    0,   21,
	   25,   26,   27,   28,    0,    0,   31,   32,   18,    0,
		0,    0,    0,   20,    0,   29,   19,    0,    0,    0,
	   30,   22,   24,    0,   23,
	};

	final static short yydgoto[] = {                          2,
	   38,   80,   94,  100,   13,   14,   15,   16,   17,   18,
	   19,   20,   21,   22,  108,   26,   51,   66,  109,  124,
	  110,   47,   61,    3,   23,
	};

	final static short yysindex[] = {                       -36,
	 -232,  -36,    0,    0,  -35,  -35,  -35,  -35,  -35,  -35,
	  -35,  -35,  -35,    0,    0,    0,    0,    0,    0,    0,
		0,    0,  -29,    0, -252,  -22,  -22,  -22,  -22,  -22,
	  -22,  -22,  -22,  -21,    0, -239, -247,  -17,  -17,  -17,
	  -17,  -17,  -17,  -17,  -17, -254,  -14,   31, -185, -196,
	   35,   35,   35,   35,   35,   35,   35,   35, -183, -202,
		0,    0,   37, -180, -191,   41,   41,   41,   41,   41,
	   41,   41,   41,   42, -177,    0,   43, -174, -186,   47,
	   47,   47,   47,   47,   48,   47,   47,    0,   49,    0,
	   50, -169, -181,   53,   53,   53,   53,   53, -179,   47,
	   53,   53,    0,    0,   55, -164, -260,    0,   57,    0,
		0,    0,    0,    0, -160,   53,    0,    0,    0,   58,
	 -159, -158, -173,    0,   61,    0,    0,   63,   64, -153,
		0,    0,    0,   66,    0,
	};

	final static short yyrindex[] = {                         0,
		0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
		0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
		0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
		0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
		0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
		0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
		0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
		0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
		0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
		0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
		0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
		0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
		0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
		0,    0,    0,    0,    0,
	};

	final static short yygindex[] = {                         0,
	   17,  -16,  -73,    0,    0,    0,    0,    0,    0,    0,
		0,    0,    0,    0,  -95,   30,   19,   13,    0,    0,
		0,    0,    0,  106,    0,
	};

	final static int YYTABLESIZE=108;
	final static short yytable[] = {                        111,
	  112,  113,  114,    1,   25,  117,  118,   95,   96,   97,
	   98,   35,  101,  102,  121,   36,  122,   37,   46,   48,
	  126,   49,   50,   59,    4,   60,  116,    5,    6,    7,
		8,    9,   10,   11,   12,   27,   28,   29,   30,   31,
	   32,   33,   34,   39,   40,   41,   42,   43,   44,   45,
	   81,   82,   83,   84,   85,   86,   87,   52,   53,   54,
	   55,   56,   57,   58,   67,   68,   69,   70,   71,   72,
	   73,   62,   63,   64,   65,   74,   75,   76,   77,   78,
	   79,   89,   88,   90,   91,   92,   93,   99,  105,  103,
	  104,  106,  107,  120,  115,  119,  123,  125,  127,  128,
	  129,  131,  130,  132,  133,  134,  135,   24,
	};

	final static short yycheck[] = {                         95,
	   96,   97,   98,   40,   40,  101,  102,   81,   82,   83,
	   84,   41,   86,   87,  275,  268,  277,   40,   40,  259,
	  116,  269,   40,  278,  257,   40,  100,  260,  261,  262,
	  263,  264,  265,  266,  267,    6,    7,    8,    9,   10,
	   11,   12,   13,   27,   28,   29,   30,   31,   32,   33,
	   67,   68,   69,   70,   71,   72,   73,   39,   40,   41,
	   42,   43,   44,   45,   52,   53,   54,   55,   56,   57,
	   58,   41,  258,  270,   40,  259,  279,   41,  259,  271,
	   40,  259,   41,   41,  259,  272,   40,   40,  258,   41,
	   41,  273,   40,  258,  274,   41,   40,  258,   41,  259,
	  259,   41,  276,   41,   41,  259,   41,    2,
	};

	final static short YYFINAL=2;
	final static short YYMAXTOKEN=279;

	final static String yyname[] = {
	"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
	null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
	null,null,null,null,null,null,null,null,null,null,"'('","')'",null,null,null,
	null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
	null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
	null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
	null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
	null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
	null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
	null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
	null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
	null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
	null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
	null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
	null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
	null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
	null,null,null,null,"ID","STRING","INT","POSNODE","ISSUENODE","ARGNODE",
	"DECISIONNODE","NOTENODE","REFNODE","MAPVIEW","LISTVIEW","IDENT","AUTHOR",
	"CRDATE","MODDATE","LABEL","DETAIL","REFPATH","XPOS","YPOS","POS","FROMID",
	"TOID",
	};

	final static String yyrule[] = {
		"$accept : elements",
		"elements : element",
		"elements : elements element",
		"element : '(' elementtype ')'",
		"elementtype : positionnode",
		"elementtype : issuenode",
		"elementtype : argumentnode",
		"elementtype : decisionnode",
		"elementtype : notenode",
		"elementtype : referencenode",
		"elementtype : mapview",
		"elementtype : listview",
		"elementtype : link",
		"positionnode : POSNODE id author creationdate moddate label detail pos",
		"id : '(' IDENT INT ')'",
		"author : '(' AUTHOR STRING ')'",
		"creationdate : '(' CRDATE INT ')'",
		"moddate : '(' MODDATE INT ')'",
		"label : '(' LABEL STRING ')'",
		"detail : '(' DETAIL STRING ')'",
		"pos : xpos ypos",
		"pos : lpos",
		"xpos : '(' XPOS INT ')'",
		"ypos : '(' YPOS INT ')'",
		"lpos : '(' POS INT ')'",
		"issuenode : ISSUENODE id author creationdate moddate label detail pos",
		"argumentnode : ARGNODE id author creationdate moddate label detail pos",
		"decisionnode : DECISIONNODE id author creationdate moddate label detail pos",
		"notenode : NOTENODE id author creationdate moddate label detail pos",
		"referencenode : REFNODE id author creationdate moddate label refpath detail pos",
		"refpath : '(' REFPATH STRING ')'",
		"mapview : MAPVIEW id author creationdate moddate label detail pos",
		"listview : LISTVIEW id author creationdate moddate label detail pos",
		"link : linktype id fromid toid",
		"linktype : ID",
		"fromid : '(' FROMID INT ')'",
		"toid : '(' TOID INT ')'",
	};

	private static final int EOF = 0;

	private static  TokenTable			DEFAULT_TOKEN_TABLE;
	private static  KeywordTable		DEFAULT_KEYWORDS;
	private static  CharSymbolTable	DEFAULT_CHAR_SYMBOLS;

	static {
	  DEFAULT_TOKEN_TABLE = new TokenTable(5);
	  DEFAULT_TOKEN_TABLE.addCode("INT", INT);
	  DEFAULT_TOKEN_TABLE.addCode("ID", ID);
	  DEFAULT_TOKEN_TABLE.addCode("STRING", STRING);

	  DEFAULT_KEYWORDS = new KeywordTable(25);
	  DEFAULT_KEYWORDS.addCode("PositionNode",      POSNODE);
	  DEFAULT_KEYWORDS.addCode("IssueNode",         ISSUENODE);
	  DEFAULT_KEYWORDS.addCode("ArgumentNode",      ARGNODE);
	  DEFAULT_KEYWORDS.addCode("DecisionNode",		DECISIONNODE);
	  DEFAULT_KEYWORDS.addCode("NoteNode",          NOTENODE);
	  DEFAULT_KEYWORDS.addCode("ReferenceNode",     REFNODE);
	  DEFAULT_KEYWORDS.addCode("MapView",           MAPVIEW);
	  DEFAULT_KEYWORDS.addCode("ListView",          LISTVIEW);
	  DEFAULT_KEYWORDS.addCode("ID",                IDENT);
	  DEFAULT_KEYWORDS.addCode("Author",            AUTHOR);
	  DEFAULT_KEYWORDS.addCode("CreationDate",      CRDATE);
	  DEFAULT_KEYWORDS.addCode("ModificationDate",  MODDATE);
	  DEFAULT_KEYWORDS.addCode("Label",             LABEL);
	  DEFAULT_KEYWORDS.addCode("Detail",            DETAIL);
	  DEFAULT_KEYWORDS.addCode("ReferencePath",     REFPATH);
	  DEFAULT_KEYWORDS.addCode("XPos",              XPOS);
	  DEFAULT_KEYWORDS.addCode("YPos",              YPOS);
	  DEFAULT_KEYWORDS.addCode("Pos",				POS);
	  DEFAULT_KEYWORDS.addCode("FromID",            FROMID);
	  DEFAULT_KEYWORDS.addCode("ToID",              TOID);
	  DEFAULT_CHAR_SYMBOLS = new CharSymbolTable(5);
	  DEFAULT_CHAR_SYMBOLS.addCode('(', '(');
	  DEFAULT_CHAR_SYMBOLS.addCode(')', ')');
	}

	private ViewPaneUI			oViewPaneUI = null;
	private UIList				uiList = null;
	private String				file = "";
	private TokenTable			tokens = null;
	private KeywordTable		keywords = null;
	private CharSymbolTable		charSymbols = null;
	private Lexer				lex = null;
	private boolean				lexerReady = true;
	private IModel				model = null;
	private IView				view = null;
	private boolean				success = false;
	//Hashtable to store the UINodes ( mainly to be used in creating Links and UILinks)
	private Hashtable			htUINodes	= new Hashtable(51);
	private String				refpath = "";
	private static final int	SLEEP_TIME = 0; //SLEEP_TIME used to slow down overloading node creation process
	private boolean				bError = false;
	private boolean				bNormal = true; // boolean flag for normal inports
	private boolean				bInclude = true; //boolean flag to include QM author and dates in detail
	private int				numberOfNodes = 0;
	private int				numberOfLinks = 0;
	private JOptionPane			oOptionPane = null;
	private UIProgressDialog	oProgressDialog = null;
	private JProgressBar		oProgressBar = null;
	private ProgressThread		oThread = null;
	private JLabel				lblPercent = null;
	private int nNodeCount = 0;
	private int nLinkCount = 0;
	private Vector nodeList = new Vector();
	private Vector linkList = new Vector();
	private boolean isSmartImport = false;
	private boolean isListImport = false;


	//Constructor
	public Parser() {}

	public Parser(boolean debug_me) {
  		yydebug=debug_me;
	}

	/**
	 * Creates a parser to parse the file with the given filename.
	 * Uses the given model as the model and view to add the nodes and links
	 * to. The token, keyword, and character symbol
	 * tables are created for use in the parser. Error and warning
	 * messages are displayed on the given log.
	 */
	public Parser(boolean debug, String fileName, IModel model, IView view) {
		this(debug);
		if (view.getType() == ICoreConstants.LISTVIEW) {
			isListImport = true;
		}
		else {
			isListImport = false;
		}
  		init(fileName, model, view);
	}

	/**
	 * Creates a parser to parse the file with the given filename.
	 * Uses the given model and view o add the nodes and links
	 * to and uses the given token, keyword and character symbol table in the parser.
	 */
	public Parser(boolean debug, String fileName, IModel model, IView view,
							TokenTable tokens, KeywordTable keywords, CharSymbolTable charSymbols) {
		this(debug);
		file = fileName;
		this.model = model;
		this.view = view;
		this.tokens = tokens;
		this.keywords = keywords;
		this.charSymbols = charSymbols;
		//clear the UINode ht
		htUINodes.clear();
		if (view.getType() == ICoreConstants.LISTVIEW) {
			isListImport = true;
		}
		else {
			isListImport = false;
		}

  		// set error log
  		//Parser.setLog(log);
  		// initialize lexer
  		try {
			lex = new Lexer(tokens, keywords, charSymbols, file);
  		}
		catch (IOException e) {
			ProjectCompendium.APP.displayError("Error reading file: "+file +
				"." + e.getMessage());
			lexerReady = false;
  		}
	}

	public void run() {
		parse();
		ProjectCompendium.APP.scaleAerialToFit();
		DBNode.restoreImportSettings();
	}

	/**
	 * Do the actual parsing.
	 */
	public void parse() {

		if (isListImport) {
			uiList.deselectAll();
		}
		else {
			oViewPaneUI.getViewPane().setSelectedLink(null, ICoreConstants.DESELECTALL);
			oViewPaneUI.getViewPane().setSelectedNode(null, ICoreConstants.DESELECTALL);
		}
  		if (lexerReady) {
			// start parsing
			//		log.clear();
			//		log.addMessage(new Message("Project Compendium - Questmap Parser - Version "+ICoreConstants.sPARSERVERSION));
			//		log.addMessage(new Message("Copyright (c) Bell Atlantic Corporation 1998. All rights reserved."));
			//		log.addMessage(new Message(""));

			yyparse();

			//		log.displaySummaryReport();
		}

	  	lex.close();

  		oProgressDialog.setVisible(false);
  		oProgressDialog.dispose();
  		//inform user that import is done only if there are no errors
   		if(!bError) {

	  		if (oProgressDialog.isCancelled()) {
				ProjectCompendium.APP.displayMessage("Import of " + file + " into " + view.getLabel() + " is cancelled." ,"Import Cancelled");
	  		}
			else {
				ProjectCompendium.APP.displayMessage("Finished importing " + file + " into " + view.getLabel(), "Import Finished");
	  		}

			//ProjectCompendium.APP.setStatus("Finished importing " + file);
   		}
   		else {
			ProjectCompendium.APP.displayError("Error importing " + file + " into " + view.getLabel(), "Import Error ");
   		}

   		if (isListImport) {
	  		uiList.updateTable();
  		}

		ProjectCompendium.APP.setDefaultCursor();
		//System.gc();
	}

	// method: debug
	void debug(String msg) {
  		//if (yydebug)
  		//System.out.println(msg);
	}

	//########## STACK ##########

	void state_push(int state) {
		if (stateptr>=YYSTACKSIZE)         //overflowed?
			return;
  		statestk[++stateptr]=state;
	}

	int state_pop() {
  		if (stateptr<0)                    //underflowed?
			return -1;
  		return statestk[stateptr--];
	}

	void state_drop(int cnt) {
		int ptr;
  		ptr=stateptr-cnt;
  		if (ptr<0)
			return;
  		stateptr = ptr;
	}

	int state_peek(int relative) {
		int ptr;
  		ptr=stateptr-relative;
  		if (ptr<0)
			return -1;
  		return statestk[ptr];
	}

	boolean init_stacks() {
  		statestk = new int[YYSTACKSIZE];
  		stateptr = -1;
  		val_init();
  		return true;
	}

	// method: dump_stacks : show n levels of the stacks
	void dump_stacks(int count) {
		int i;
		//System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
		//  for (i=0;i<count;i++)
		//	System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
		// System.out.println("======================");
	}

	// methods: value stack push,pop,drop,peek.  semantic type=int
	void val_init()	{
  		valstk=new Union[YYSTACKSIZE];
  		valptr=-1;
	}

	void val_push(Union val) {
  		if (valptr>=YYSTACKSIZE)
			return;
  		valstk[++valptr]=val;
	}

	Union val_pop() {
  		if (valptr<0)
			return null;
  		return valstk[valptr--];
	}

	void val_drop(int cnt) {
		int ptr;
  		ptr=valptr-cnt;
 		if (ptr<0)
			return;
  		valptr = ptr;
	}

	Union val_peek(int relative) {
		int ptr;
  		ptr=valptr-relative;
		if (ptr<0)
			return null;
	  	return valstk[ptr];
	}

	// END STACK


	public void setViewPaneUI(ViewPaneUI viewpaneUI) {
		oViewPaneUI = viewpaneUI;
	}

	public void setUIList(UIList list) {
		uiList = list;
	}


	/**
	 * Initializes the parser, creating the various tables required
	 * for the parser and initializing the lexical analyzer.
	 */
	private void init(String fileName, IModel model, IView view) {
		file = fileName;
		this.model = model;
		this.view = view;
  		// initialize tables

		/*
  		initTokens();
  		initKeywords();
  		initCharSymbols();
		*/

		this.tokens = DEFAULT_TOKEN_TABLE;
		this.keywords = DEFAULT_KEYWORDS;
		this.charSymbols = DEFAULT_CHAR_SYMBOLS;
  		// initialize lexer
  		try {
			lex = new Lexer(tokens, keywords, charSymbols, file);
  		}
		catch (IOException e) {
			ProjectCompendium.APP.displayError("Error reading file: "+file +
					"." + e.getMessage());
			lexerReady = false;
  		}

  		//set the import profile variables
  		//ie., normal import and 'include in detail' flags

  		Vector profiles = ProjectCompendium.APP.getImportProfile();

  		//first element is a boolean flag for normal import
  		bNormal = ((Boolean)profiles.elementAt(0)).booleanValue();
  		bInclude = ((Boolean)profiles.elementAt(1)).booleanValue();

  		//get the count of the nodes and links for progressbar
  		scanFile(fileName);

  		//an optionpane to hold the progressbar
  		oProgressBar = new JProgressBar();
  		oProgressBar.setMinimum(0);
  		oProgressBar.setMaximum(numberOfNodes+numberOfLinks);
  		//System.out.println("Number of Nodes and links:" +
		//		 (numberOfNodes+numberOfLinks));
  		//oOptionPane = new JOptionPane(oProgressBar);

		lblPercent = new JLabel("");
		lblPercent.setBounds(50,50,100,25);
		oThread = new ProgressThread();
		oThread.start();
	}

	private class ProgressThread extends Thread {
		public ProgressThread() {
	  		oProgressDialog = new UIProgressDialog(ProjectCompendium.APP,"Import Progress..", "Import completed");
	  		oProgressDialog.showDialog(oProgressBar);
	  		oProgressDialog.setModal(true);
	  		//oProgressDialog.getContentPane().add(lblPercent);
		}

		public void run() {
	  		//oProgressDialog = oOptionPane.createDialog(ProjectCompendium.APP.getContentPane(),"Import Progress..");
	  		oProgressDialog.setVisible(true);
		}
	}

	public void setSmartImport(boolean doSmartImport) {
		isSmartImport = doSmartImport;
	}


	/**
	 * Read the next token from the lexical analyzer.
	 */
	private int yylex() {
		int val = Lexer.ERROR;
		try {
			yylval = new Union();
			val = lex.nextToken(yylval);
		}
		catch (IOException e) {
			ProjectCompendium.APP.displayError("Error reading file: "+file + "." + e.getMessage());
			val = Lexer.ERROR;
  		}
		return val;
	}

	/**
	 * Display error message in log using the line number and position from the
	 * lexical analyzer.
	 */
	private void yyerror(String s) {
		//  log.addError(new CodeError(s, lex.getLine(), lex.getPos()-lex.getYYLeng(), file));
		oThread.interrupt();
 		oProgressDialog.setVisible(false);
 		oProgressDialog.dispose();

  		if(!bError) {
			bError = true;
			ProjectCompendium.APP.displayError("Error reading file: " + file +  " (Cant Recognize Format)", "File Import.. ");
			ProjectCompendium.APP.setDefaultCursor();
  		}
	}

	/**
	 * Display error message in log using the given line number and position.
	 */
	private void yyerror(String s, int line, int pos) {
		//	log.addError(new CodeError(s, line, pos, file));
		oThread.interrupt();
	 	oProgressDialog.setVisible(false);
	 	oProgressDialog.dispose();

		if(!bError) {
			bError = true;
			ProjectCompendium.APP.displayError("Error reading file: " + file +  " (Cant Recognize Format)", "File Import.. ");
	    	ProjectCompendium.APP.setDefaultCursor();
		}
	}

	/**
	 * Display warning message in log using the line number and position from the
	 * lexical analyzer.
	 */
	private void yywarning(String s) {
		System.out.println(s + " line:" + lex.getLine() +  " pos:" + (lex.getPos()-lex.getYYLeng()) + " file:" + file);
	}

	/**
	 * Display warning message in log using the given line number and position.
	 */
	private void yywarning(String s, int line, int pos) {
		System.out.println(s + " line:" + line + " pos:" + pos + " file:" + file);
	}

	/**
	 * Load the file on the client
	 * The file is scanned to get number of Nodes and Links since
	 * The QM output file
	 * Ex., PositionNode String means a Node in the file
	 *      RespondsToLink String means a link in the file
	 * Based on this display the Progress Bar
	 */
	private void scanFile(String file) {
		//clean the vector
		numberOfNodes = 0;
		numberOfLinks = 0;

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			ProjectCompendium.APP.displayError(
				"File '"+file+"' not found." +
				e.getMessage());
			return;
		} // end try

		try {
			while (reader.ready()) {

				String line = reader.readLine();
				if (line == null)
					break;

				if ((line.endsWith("Node")) || (line.endsWith("View"))) {
					//a node found!
					numberOfNodes++;
				}
				else if (line.endsWith("Link")) {
					//a link found!
					numberOfLinks++;
				}
			}
			reader.close();
		}
		catch (IOException e) {}
	}


	/**
	 * Creates an INodeSummary object and adds it to the model and view
	 */
	private INodeSummary createNode( IModel model, IView  view,
													 int		nType,
													 int		nId,
													 String author,
													 long		lCreationDate,
													 long		lModDate,
													 String label,
													 String detail,
													 Point	ptPos) throws Exception {

		INodeSummary node = null;
		//Adjust the x and y coordinates so node close to the border can be fully seen
		if(ptPos.x < 0){
			ptPos.x = -ptPos.x;
		}
		else if(ptPos.x < 25) {
			ptPos.x = ptPos.x + 50;
		}

		if(ptPos.y < 0) {
			ptPos.y = -ptPos.y;
		}
		else if(ptPos.y < 25) {
			ptPos.y = ptPos.y + 50;
		}

		String qmAuthor = author;
		//create node according to the import profile
		//if(bNormal) {

		//include the QM details only if import profile says so..
		if(bInclude) {
			//set the QM author and QM dates in the detail
			detail += "\n(QM Author: " + author + ")\n";

			//Overwrite the system creation and mod date with the original Questmap dates
			//The Dates are mulitplied by 1000 to get real time in milliseconds since Jan 1, 1970
			//Dates from Questmap are 3 digits short in lenght!.. so added 3 digits, who cares about milliseconds
			detail += "\n(Creation Date: " + (new Date(lCreationDate*1000)).toString() + ")\n";
			detail += "\n(Modification Date: " + (new Date(lModDate*1000)).toString() + ")\n";
		}

		//set the author as the Current User
		author = ProjectCompendium.APP.getModel().getUserProfile().getUserName();
		String userID = ProjectCompendium.APP.getModel().getUserProfile().getId();
		//}

		// IDENTIFY QUESTMAP IDs SEPARATELY FROM OTHER IMPORTED IDs
		String sOriginalID = new Integer(nId).toString();
		if (sOriginalID.equals("-1"))
			sOriginalID = "";
		else if (!sOriginalID.equals(""))
			sOriginalID = "QM"+sOriginalID;

		UINode uinode = oViewPaneUI.createNode(nType, sOriginalID,
											 author, label,
											 detail,
											 ptPos.x, ptPos.y, userID);

		if(nType == ICoreConstants.REFERENCE)
			uinode.getNode().setSource(refpath, "", author);

		Date date = new Date();
		if (isSmartImport) {
			uinode.getNode().setCreationDate(new Date(lCreationDate*1000), author);
			//uinode.getNode().setModificationDate(new Date(lModDate*1000), userID);
			uinode.getNode().setAuthor(qmAuthor, author);
			//System.out.println("Smart import selected");
		}
		else {
			uinode.getNode().setCreationDate(date, author);
			//uinode.getNode().setModificationDate(date, author, userID);
			//System.out.println("Normal import selected");
		}
		uinode.setRollover(false);
		nodeList.addElement(uinode);

		htUINodes.put(new Integer(nId),uinode);

		/*
		try {
		  sleep(SLEEP_TIME);
		}
		catch(InterruptedException ex) {
			ex.printStackTrace();
		}
		*/

		//set the node count for progress bar
		nNodeCount++;
		//System.out.println(nNodeCount + " nodes created");
		//System.out.println("Memory available after " +
		//				   nNodeCount + " nodes is " + Runtime.getRuntime().freeMemory());
		oProgressBar.setValue(nNodeCount+nLinkCount);
		oProgressDialog.setStatus(nNodeCount+nLinkCount);
		//lblPercent.setText(String.valueOf(oProgressBar.getValue()));
		uinode.setSelected(true);
		oViewPaneUI.getViewPane().setSelectedNode(uinode,ICoreConstants.MULTISELECT);
		return node;

	} // createNode


	/**
	 * Creates an INodeSummary object and adds it to the model and view
	 */
	private INodeSummary createListNode( IModel model, IView  view,
													 int		nType,
													 int		nId,
													 String author,
													 long		lCreationDate,
													 long		lModDate,
													 String label,
													 String detail,
													 Point	ptPos) throws Exception {
		INodeSummary node = null;
		String userID = "";
		//Adjust the x and y coordinates so node close to the border can be fully seen
		if(ptPos.x < 0) {
			ptPos.x = -ptPos.x;
		}
		else if(ptPos.x < 25) {
			ptPos.x = ptPos.x + 50;
		}

		if(ptPos.y < 0) {
			ptPos.y = -ptPos.y;
		}
		else if(ptPos.y < 25) {
			ptPos.y = ptPos.y + 50;
		}

		String qmAuthor = author;
		//if(bNormal)
		{
			if(bInclude) //include the QM details only if import profile says so..
			{
				//set the QM author and QM dates in the detail
				detail += "\n(QM Author: " + author + ")\n";
				//Overwrite the system creation and mod date with the original Questmap dates
				//The Dates are mulitplied by 1000 to get real time in milliseconds since Jan 1, 1970
				//Dates from Questmap are 3 digits short in lenght!.. so added 3 digits, who cares about milliseconds
				detail += "\n(Creation Date: " + (new Date(lCreationDate*1000)).toString() + ")\n";
				detail += "\n(Modification Date: " + (new Date(lModDate*1000)).toString() + ")\n";
			}

			//set the author as the Current User
			author = ProjectCompendium.APP.getModel().getUserProfile().getUserName();
		}

		// IDENTIFY QUESTMAP IDs SEPARATELY FROM OTHER IMPORTED IDs
		String sOriginalID = new Integer(nId).toString();
		if (sOriginalID.equals("-1"))
			sOriginalID = "";
		else if (!sOriginalID.equals(""))
			sOriginalID = "QM"+sOriginalID;

		NodePosition npTemp = uiList.getListUI().createNode (nType, sOriginalID,
											 author, label,
											 detail,
											 ptPos.x,
											 (uiList.getNumberOfNodes() + nodeList.size() + 1) * 10);

		node = npTemp.getNode();

		if(nType == ICoreConstants.REFERENCE)
			node.setSource(refpath, "", author);

		Date date = new Date();
		if (isSmartImport) {
			node.setCreationDate(new Date(lCreationDate*1000), author);
			node.setAuthor(qmAuthor, author);
		}
		else {
			node.setCreationDate(date, author);
		}
		nodeList.addElement(node);

		//set the node count for progress bar
		nNodeCount++;
		oProgressBar.setValue(nNodeCount);
		oProgressDialog.setStatus(nNodeCount);

		return node;
	}


	/**
	 * Creates a IView object and adds it to the model and view
	 */
	private IView createView( IModel model, IView  view,
													 int		nType,
													 int		nId,
													 String author,
													 long		lCreationDate,
													 long		lModDate,
													 String label,
													 String detail,
													 Point	ptPos) throws Exception {
		IView node = null;
		String userID = "" ;
		//Adjust the x and y coordinates so node close to the border can be fully seen
		if(ptPos.x < 0) {
			ptPos.x = -ptPos.x;
		}
		else if(ptPos.x < 25) {
			ptPos.x = ptPos.x + 50;
		}

		if(ptPos.y < 0) {
			ptPos.y = -ptPos.y;
		}
		else if(ptPos.y < 25) {
			ptPos.y = ptPos.y + 50;
		}

		String qmAuthor = author;
		//if(bNormal)
		{
			if(bInclude) //include the QM details only if import profile says so..
			{
				//set the QM author and QM dates in the detail
				detail += "\n(QM Author: " + author + ")\n";
				//Overwrite the system creation and mod date with the original Questmap dates
				//The Dates are mulitplied by 1000 to get real time in milliseconds since Jan 1, 1970
				//Dates from Questmap are 3 digits short in lenght!.. so added 3 digits, who cares about milliseconds
				detail += "\n(Creation Date: " + (new Date(lCreationDate*1000)).toString() + ")\n";
				detail += "\n(Modification Date: " + (new Date(lModDate*1000)).toString() + ")\n";
			}

			//set the author as the Current User
			author = ProjectCompendium.APP.getModel().getUserProfile().getUserName();
			userID = ProjectCompendium.APP.getModel().getUserProfile().getId();
			
		}

		// IDENTIFY QUESTMAP IDs SEPARATELY FROM OTHER IMPORTED IDs
		String sOriginalID = new Integer(nId).toString();
		if (sOriginalID.equals("-1"))
			sOriginalID = "";
		else if (!sOriginalID.equals(""))
			sOriginalID = "QM"+sOriginalID;

		UINode uinode = oViewPaneUI.createNode(nType, sOriginalID, author, label,
											 detail, ptPos.x, ptPos.y, userID);
		Date date = new Date();
		if (isSmartImport) {
			uinode.getNode().setCreationDate(new Date(lCreationDate*1000), author);
			uinode.getNode().setAuthor(qmAuthor, author);
		}
		else {
			uinode.getNode().setCreationDate(date, author);
		}

		uinode.setRollover(false);
		nodeList.addElement(uinode);

		htUINodes.put(new Integer(nId),uinode);

		//set the node count for progress bar
		nNodeCount++;

		oProgressBar.setValue(nNodeCount+nLinkCount);
		oProgressDialog.setStatus(nNodeCount+nLinkCount);
		uinode.setSelected(true);
		oViewPaneUI.getViewPane().setSelectedNode(uinode,ICoreConstants.MULTISELECT);

		return node;
	}

	/**
	 * Creates a IView object and adds it to the model and view
	 */
	private IView createListView( IModel model, IView  view,
													 int		nType,
													 int		nId,
													 String author,
													 long		lCreationDate,
													 long		lModDate,
													 String label,
													 String detail,
													 Point	ptPos) throws Exception {
		IView node = null;
		String userID = "";
		//Adjust the x and y coordinates so node close to the border can be fully seen
		if(ptPos.x < 0) {
			ptPos.x = -ptPos.x;
		}
		else if(ptPos.x < 25) {
			ptPos.x = ptPos.x + 50;
		}

		if(ptPos.y < 0) {
			ptPos.y = -ptPos.y;
		}
		else if(ptPos.y < 25) {
			ptPos.y = ptPos.y + 50;
		}

		String qmAuthor = author;
		//create node according to the import profile

		//if(bNormal)
		{
			if(bInclude) //include the QM details only if import profile says so..
			{
				//set the QM author and QM dates in the detail
				detail += "\n(QM Author: " + author + ")\n";
				//Overwrite the system creation and mod date with the original Questmap dates
				//The Dates are mulitplied by 1000 to get real time in milliseconds since Jan 1, 1970
				//Dates from Questmap are 3 digits short in lenght!.. so added 3 digits, who cares about milliseconds
				detail += "\n(Creation Date: " + (new Date(lCreationDate*1000)).toString() + ")\n";
				detail += "\n(Modification Date: " + (new Date(lModDate*1000)).toString() + ")\n";
			}

			//set the author as the Current User
			author = ProjectCompendium.APP.getModel().getUserProfile().getUserName();
			userID = ProjectCompendium.APP.getModel().getUserProfile().getId();
		}

		// IDENTIFY QUESTMAP IDs SEPARATELY FROM OTHER IMPORTED IDs
		String sOriginalID = new Integer(nId).toString();
		if (sOriginalID.equals("-1"))
			sOriginalID = "";
		else if (!sOriginalID.equals(""))
			sOriginalID = "QM"+sOriginalID;

		NodePosition npTemp = uiList.getListUI().createNode (nType, sOriginalID,
											 author, label,
											 detail,
											 ptPos.x,
											 (uiList.getNumberOfNodes() + nodeList.size() + 1) * 10
											 );
		node = (View)npTemp.getNode();
		Date date = new Date();
		if (isSmartImport) {
			node.setCreationDate(new Date(lCreationDate*1000), author);
			node.setAuthor(qmAuthor, author);
			//System.out.println("Smart import selected");
		}
		else {
			node.setCreationDate(date, author);
			//System.out.println("Normal import selected");
		}
		nodeList.addElement(node);

		//set the node count for progress bar
		nNodeCount++;
		oProgressBar.setValue(nNodeCount);
		oProgressDialog.setStatus(nNodeCount);
		//oViewPaneUI.getViewPane().setSelectedNode(uinode,ICoreConstants.MULTISELECT);
		return node;
	}


	/**
	 * Creates a ILink object and adds it to the model and view
	 */
	private ILink createLink( IModel model, IView  view,
													 String sType,
													 int		nId,
													 int		nFromId,
													 int		nToId) throws Exception {
		ILink link = null;

		//only Mapviews have links!
		if(view.getType() == ICoreConstants.MAPVIEW) {

			//get the 'From' UINode
			UINode fromUINode = (UINode)htUINodes.get(new Integer(nFromId));

			//get the 'To' UINode
			UINode toUINode = (UINode)htUINodes.get(new Integer(nToId));

			String type = UILink.getLinkType(sType);
			
			LinkProperties props = UIUtilities.getLinkProperties(type);
			UILink uilink = toUINode.getUI().createLink(fromUINode,toUINode, type, props);
			linkList.addElement(uilink);
			uilink.setSelected(true);
			oViewPaneUI.getViewPane().setSelectedLink(uilink, ICoreConstants.MULTISELECT);
		}

		//set the node count for progress bar
		nLinkCount++;
		oProgressBar.setValue(nNodeCount+nLinkCount);
		oProgressDialog.setStatus(nNodeCount+nLinkCount);

		return link;
	}

	// method: yylexdebug : check lexer state
	void yylexdebug(int state,int ch) {
		String s=null;
  		if (ch < 0)
			ch=0;
  		if (ch <= YYMAXTOKEN) //check index bounds
	 		s = yyname[ch];    //now get it
  		if (s==null)
			s = "illegal-symbol";
  		debug("state "+state+", reading "+ch+" ("+s+")");
	}

	int yyparse() {

		int yyn;       //next next thing to do
		int yym;       //
		int yystate;   //current parsing state from state table
		String yys;    //current token string
		boolean doaction;
		init_stacks();
		nodeList.removeAllElements();
		linkList.removeAllElements();
		yynerrs = 0;
		yyerrflag = 0;
		yychar = -1;          //impossible char forces a read
		yystate=0;            //initial state
		state_push(yystate);  //save it

	 	ProjectCompendium.APP.setWaitCursor();

  		while (true) //until parsing is done, either correctly, or w/error
		{
	  		if (oProgressDialog.isCancelled()) {
		  		int result = JOptionPane.showConfirmDialog(oProgressDialog,
										"Do you want to Cancel the import?",
										"Cancel Import",
										JOptionPane.YES_NO_OPTION);
		  		if (result == JOptionPane.YES_OPTION) {
			  		cancelImport();
			  		break;
		  		}
		  		else {
			  		oProgressDialog.setCancelled(false);
		  		}
	  		}

			doaction=true;

			if (yydebug)
				debug("loop");

			//#### NEXT ACTION (from reduction table)
			for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
	  		{
	  			if (yydebug)
	  				debug("yyn:"+yyn+"  state:"+yystate+"  char:"+yychar);

	  			if (yychar < 0) {     //we want a char?
					yychar = yylex();  //get next token
					//#### ERROR CHECK ####
					if (yychar < 0)    //it it didn't work/error
		 			{
		 				yychar = 0;      //change it to default string (no -1!)
		 				if (yydebug)
							yylexdebug(yystate,yychar);
		  			}
				}//yychar<0

	  			yyn = yysindex[yystate];  //get amount to shift by (shift index)
	  			if ((yyn != 0) && (yyn += yychar) >= 0 &&
		  			yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
				{
					if (yydebug)
		  				debug("state "+yystate+", shifting to state "+yytable[yyn]+"");

					//#### NEXT STATE ####
					yystate = yytable[yyn];//we are in a new state
					state_push(yystate);   //save it
					val_push(yylval);      //push our lval as the input for next rule
					yychar = -1;           //since we have 'eaten' a token, say we need another
					if (yyerrflag > 0)     //have we recovered an error?
		   				--yyerrflag;        //give ourselves credit
					doaction=false;        //but don't process yet
					break;   //quit the yyn=0 loop
				}

				yyn = yyrindex[yystate];  //reduce
				if ((yyn !=0 ) && (yyn += yychar) >= 0 &&
						yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
				{   //we reduced!
	  				if (yydebug) debug("reduce");
	  					yyn = yytable[yyn];
					doaction=true; //get ready to execute
					break;         //drop down to actions
				}
				else //ERROR RECOVERY
	  			{
					if (yyerrflag==0) {
						yyerror("syntax error");
						yynerrs++;
					}
				  	if (yyerrflag < 3) //low error count?
					{
						yyerrflag = 3;
						while (true)   //do until break
					  	{
					  		if (stateptr<0)   //check for under & overflow here
							{
								yyerror("stack underflow. aborting...");  //note lower case 's'
								return 1;
							}
					  		yyn = yysindex[state_peek(0)];
					  		if ((yyn != 0) && (yyn += YYERRCODE) >= 0 &&
								yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
							{
								if (yydebug)
						  			debug("state "+state_peek(0)+", error recovery shifting to state "+yytable[yyn]+" ");
								yystate = yytable[yyn];
								state_push(yystate);
								val_push(yylval);
								doaction=false;
								break;
							}
					  		else {
								if (yydebug)
								  	debug("error recovery discarding state "+state_peek(0)+" ");

								if (stateptr<0)   //check for under & overflow here
							  	{
							  		yyerror("Stack underflow. aborting...");  //capital 'S'
							  		return 1;
							  	}
								state_pop();
								val_pop();
							}
						}
					}
					else            //discard this token
					{
						if (yychar == 0)
							return 1; //yyabort
						if (yydebug)
						{
							yys = null;
						  	if (yychar <= YYMAXTOKEN)
						  		yys = yyname[yychar];
						  	if (yys == null)
						  		yys = "illegal-symbol";
						  	debug("state "+yystate+", error recovery discards token "+yychar+" ("+yys+")");
						}
						yychar = -1;  //read another
					}
				}//end error recovery
			}//yyn=0 loop

			if (!doaction)   //any reason not to proceed?
	  			continue;      //skip action
			yym = yylen[yyn];          //get count of terminals on rhs
			if (yydebug)
	  			debug("state "+yystate+", reducing "+yym+" by rule "+yyn+" ("+yyrule[yyn]+")");
			yyval = val_peek(yym-1);   //get current semantic value
			if (yyval == null)
 				yyval = new Union();

			switch(yyn)
			  {
				//########## USER-SUPPLIED ACTIONS ##########
				case 13:
				//#line 137 "D:\Java\nynexst\projectcompendium\parser\parser.y"
				{ /* positionnode:*/
					try	{
						if (isListImport) {
							yyval.oval = createListNode(model, view,
								ICoreConstants.POSITION, val_peek(6).ival, val_peek(5).sval, val_peek(4).ival, val_peek(3).ival, val_peek(2).sval, val_peek(1).sval, (Point)val_peek(0).oval);
						} else {
							yyval.oval = createNode(model, view,
								ICoreConstants.POSITION, val_peek(6).ival, val_peek(5).sval, val_peek(4).ival, val_peek(3).ival, val_peek(2).sval, val_peek(1).sval, (Point)val_peek(0).oval);
						}
					} catch	(Exception e)	{
						ProjectCompendium.APP.displayError("Error in 'Parser.createNode' (position)" + e.getMessage());
					}
				}
				break;
				case 14:
				//#line 143 "D:\Java\nynexst\projectcompendium\parser\parser.y"
				{ /* id: '(' IDENT INT ')'*/
					yyval.ival = val_peek(1).ival;
				}
				break;
				case 15:
				//#line 148 "D:\Java\nynexst\projectcompendium\parser\parser.y"
				{ /* author: '(' AUTHOR STRING ')'*/
					yyval.sval = val_peek(1).sval;
				}
				break;
				case 16:
				//#line 153 "D:\Java\nynexst\projectcompendium\parser\parser.y"
				{ /* creationdate:	'(' CRDATE INT ')'*/
					yyval.ival = val_peek(1).ival;
				}
				break;
				case 17:
				//#line 158 "D:\Java\nynexst\projectcompendium\parser\parser.y"
				{ /* moddate:	'(' MODDATE INT ')'*/
					yyval.ival = val_peek(1).ival;
				}
				break;
				case 18:
				//#line 163 "D:\Java\nynexst\projectcompendium\parser\parser.y"
				{ /* label:	'(' LABEL STRING ')'*/
					yyval.sval = val_peek(1).sval;
				}
				break;
				case 19:
				//#line 168 "D:\Java\nynexst\projectcompendium\parser\parser.y"
				{ /* detail:	'('DETAIL STRING ')'*/
					yyval.sval = val_peek(1).sval;
				}
				break;
				case 20:
				//#line 173 "D:\Java\nynexst\projectcompendium\parser\parser.y"
				{ /* pos: xpos ypos*/
					yyval.oval = new Point(val_peek(1).ival, val_peek(0).ival);
				}
				break;
				case 21:
				//#line 176 "D:\Java\nynexst\projectcompendium\parser\parser.y"
				{ /* pos: lpos*/
					yyval.oval = new Point(0, val_peek(0).ival);
				}
				break;
				case 22:
				//#line 181 "D:\Java\nynexst\projectcompendium\parser\parser.y"
				{ /* xpos:	'(' XPOS INT ')'*/
					yyval.ival = val_peek(1).ival;
				}
				break;
				case 23:
				//#line 186 "D:\Java\nynexst\projectcompendium\parser\parser.y"
				{ /* ypos:	'(' YPOS INT ')'*/
					yyval.ival = val_peek(1).ival;
				}
				break;
				case 24:
				//#line 191 "D:\Java\nynexst\projectcompendium\parser\parser.y"
				{ /* lpos: '(' POS INT ')'*/
					yyval.ival = val_peek(1).ival;
				}
				break;
				case 25:
				//#line 205 "D:\Java\nynexst\projectcompendium\parser\parser.y"
				{ /* issuenode:*/
					try {
						if (isListImport) {
							yyval.oval = createListNode(model, view,
								ICoreConstants.ISSUE, val_peek(6).ival, val_peek(5).sval, val_peek(4).ival, val_peek(3).ival, val_peek(2).sval, val_peek(1).sval, (Point)val_peek(0).oval);
						} else {
							yyval.oval = createNode(model, view,
								ICoreConstants.ISSUE, val_peek(6).ival, val_peek(5).sval, val_peek(4).ival, val_peek(3).ival, val_peek(2).sval, val_peek(1).sval, (Point)val_peek(0).oval);
						}
					} catch (Exception e) {
						ProjectCompendium.APP.displayError("Error in 'Parser.createNode' (issue)" + e.getMessage());
					}
				}
				break;
				case 26:
				//#line 220 "D:\Java\nynexst\projectcompendium\parser\parser.y"
				{ /* argumentnode:*/
					try {
						if (isListImport) {
							yyval.oval = createListNode(model, view,
								ICoreConstants.ARGUMENT, val_peek(6).ival, val_peek(5).sval, val_peek(4).ival, val_peek(3).ival, val_peek(2).sval, val_peek(1).sval, (Point)val_peek(0).oval);
						} else {
							yyval.oval = createNode(model, view,
								ICoreConstants.ARGUMENT, val_peek(6).ival, val_peek(5).sval, val_peek(4).ival, val_peek(3).ival, val_peek(2).sval, val_peek(1).sval, (Point)val_peek(0).oval);
						}
					} catch (Exception e) {
						ProjectCompendium.APP.displayError("Error in 'Parser.createNode' (argument)" + e.getMessage());
					}
				}
				break;
				case 27:
				//#line 235 "D:\Java\nynexst\projectcompendium\parser\parser.y"
				{ /* decisionnode:*/
					try {
						if (isListImport) {
							yyval.oval = createListNode(model, view,
								ICoreConstants.DECISION, val_peek(6).ival, val_peek(5).sval, val_peek(4).ival, val_peek(3).ival, val_peek(2).sval, val_peek(1).sval, (Point)val_peek(0).oval);
						} else {
							yyval.oval = createNode(model, view,
								ICoreConstants.DECISION, val_peek(6).ival, val_peek(5).sval, val_peek(4).ival, val_peek(3).ival, val_peek(2).sval, val_peek(1).sval, (Point)val_peek(0).oval);
						}
					} catch (Exception e) {
						ProjectCompendium.APP.displayError("Error in 'Parser.createNode' (decision)" + e.getMessage());
					}
				}
				break;
				case 28:
				//#line 250 "D:\Java\nynexst\projectcompendium\parser\parser.y"
				{ /* notenode:*/
					try {
						if (isListImport) {
							yyval.oval = createListNode(model, view,
								ICoreConstants.NOTE, val_peek(6).ival, val_peek(5).sval, val_peek(4).ival, val_peek(3).ival, val_peek(2).sval, val_peek(1).sval, (Point)val_peek(0).oval);
						} else {
							yyval.oval = createNode(model, view,
								ICoreConstants.NOTE, val_peek(6).ival, val_peek(5).sval, val_peek(4).ival, val_peek(3).ival, val_peek(2).sval, val_peek(1).sval, (Point)val_peek(0).oval);
						}
					} catch (Exception e) {
						ProjectCompendium.APP.displayError("Error in 'Parser.createNode' (note)" +e.getMessage());
					}
				}
				break;
				case 29:
				//#line 266 "D:\Java\nynexst\projectcompendium\parser\parser.y"
				{ /* referencenode:*/
					try {
						if (isListImport) {
							yyval.oval = createListNode(model, view,
								ICoreConstants.REFERENCE, val_peek(7).ival, val_peek(6).sval, val_peek(5).ival, val_peek(4).ival, val_peek(3).sval, val_peek(1).sval, (Point)val_peek(0).oval);
						} else {
							yyval.oval = createNode(model, view,
								ICoreConstants.REFERENCE, val_peek(7).ival, val_peek(6).sval, val_peek(5).ival, val_peek(4).ival, val_peek(3).sval, val_peek(1).sval, (Point)val_peek(0).oval);
						}
					} catch (Exception e) {
						ProjectCompendium.APP.displayError("Error in 'Parser.createNode' (reference)" +	e.getMessage());
					}
				}
				break;
				case 30:
				//#line 272 "D:\Java\nynexst\projectcompendium\parser\parser.y"
				{ /* refpath: '(' REFPATH STRING ')'*/
					yyval.sval = val_peek(1).sval;
					refpath = val_peek(1).sval;
				}
				break;
				case 31:
				//#line 286 "D:\Java\nynexst\projectcompendium\parser\parser.y"
				{ /* mapview:*/
					try {
						if (isListImport) {
							yyval.oval = createListView(model, view,
								ICoreConstants.MAPVIEW, val_peek(6).ival, val_peek(5).sval, val_peek(4).ival, val_peek(3).ival, val_peek(2).sval, val_peek(1).sval, (Point)val_peek(0).oval);
						} else {
							yyval.oval = createView(model, view,
								ICoreConstants.MAPVIEW, val_peek(6).ival, val_peek(5).sval, val_peek(4).ival, val_peek(3).ival, val_peek(2).sval, val_peek(1).sval, (Point)val_peek(0).oval);
						}
					} catch (Exception e) {
						ProjectCompendium.APP.displayError("Error: in 'Parser.createView' (mapview)" + e.getMessage());
					}
				}
				break;
				case 32:
				//#line 301 "D:\Java\nynexst\projectcompendium\parser\parser.y"
				{ /* listview:*/
					try {
						if (isListImport) {
							yyval.oval = createListView(model, view,
								ICoreConstants.LISTVIEW, val_peek(6).ival, val_peek(5).sval, val_peek(4).ival, val_peek(3).ival, val_peek(2).sval, val_peek(1).sval, (Point)val_peek(0).oval);
						} else {
							yyval.oval = createView(model, view,
								ICoreConstants.LISTVIEW, val_peek(6).ival, val_peek(5).sval, val_peek(4).ival, val_peek(3).ival, val_peek(2).sval, val_peek(1).sval, (Point)val_peek(0).oval);
						}
					} catch (Exception e) {
						ProjectCompendium.APP.displayError("Error in 'Parser.createView' (listview)" + e.getMessage());
					}
				}
				break;
				case 33:
				//#line 312 "D:\Java\nynexst\projectcompendium\parser\parser.y"
				{ /* link:*/
					try {
						if (!isListImport) {
							yyval.oval = createLink(model, view,
								 val_peek(3).sval, val_peek(2).ival, val_peek(1).ival, val_peek(0).ival);
						}
					} catch (Exception e) {
						ProjectCompendium.APP.displayError("Error in 'Parser.createLink'" + e.getMessage());
					}
				}
				break;
				case 35:
				//#line 321 "D:\Java\nynexst\projectcompendium\parser\parser.y"
				{ /* fromid: '(' FROMID INT ')'*/
					yyval.ival = val_peek(1).ival;
				}
				break;
				case 36:
				//#line 326 "D:\Java\nynexst\projectcompendium\parser\parser.y"
				{ /* toid: '(' TOID INT ')'*/
					yyval.ival = val_peek(1).ival;
				}
				break;
				//#line 939 "Parser.java"
				//########## END OF USER-SUPPLIED ACTIONS ##########
			}

			//#### Now let's reduce... ####
			if (yydebug) debug("reduce");
				state_drop(yym);             //we just reduced yylen states
			yystate = state_peek(0);     //get new state
			val_drop(yym);               //corresponding value drop
			yym = yylhs[yyn];            //select next TERMINAL(on lhs)
			if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
			{
				debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
				yystate = YYFINAL;         //explicitly say we're done
				state_push(YYFINAL);       //and save it
				val_push(yyval);           //also save the semantic value of parsing
				if (yychar < 0)            //we want another character?
				{
					yychar = yylex();        //get next character
					if (yychar<0)
						yychar=0;  //clean, if necessary
					if (yydebug)
						yylexdebug(yystate,yychar);
				}
				if (yychar == 0)          //Good exit (if lex returns 0 ;-)
					break;                 //quit the loop--all DONE
			}
			else {                        //else not done yet
			    //get next state and push, for next yydefred[]
				yyn = yygindex[yym];      //find out where to go
				if ((yyn != 0) && (yyn += yystate) >= 0 &&
						yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
					yystate = yytable[yyn]; //get new state
				else
					yystate = yydgoto[yym]; //else go to new defred
				debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
				state_push(yystate);     //going again, so push state & val...
				val_push(yyval);         //for next action
			}
		}

		ProjectCompendium.APP.setDefaultCursor();

		return 0;//yyaccept!!
	}

	private void cancelImport() {
		if (isListImport) {
			for (int i = 0; i < nodeList.size(); i++) {
				uiList.deleteNode(uiList.getIndexOf((NodeSummary)nodeList.elementAt(i)));
			}
		}
		else {
			for (int i = 0; i < linkList.size(); i++) {
				UILink uiLink = (UILink)linkList.elementAt(i);
				LinkUI linkUI = (LinkUI)uiLink.getUI();
				linkUI.deleteLink(uiLink);
			}
			for (int i = 0; i < nodeList.size(); i++) {
				UINode uiNode = (UINode)nodeList.elementAt(i);
				NodeUI nodeUI = uiNode.getUI();
				nodeUI.deleteNodeAndLinks(uiNode, null);
			}
		}
	}

}

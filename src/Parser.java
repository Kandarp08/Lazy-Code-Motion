//### This file created by BYACC 1.8(/Java extension  1.15)
//### Java capabilities added 7 Jan 97, Bob Jamison
//### Updated : 27 Nov 97  -- Bob Jamison, Joe Nieten
//###           01 Jan 98  -- Bob Jamison -- fixed generic semantic constructor
//###           01 Jun 99  -- Bob Jamison -- added Runnable support
//###           06 Aug 00  -- Bob Jamison -- made state variables class-global
//###           03 Jan 01  -- Bob Jamison -- improved flags, tracing
//###           16 May 01  -- Bob Jamison -- added custom stack sizing
//###           04 Mar 02  -- Yuval Oren  -- improved java performance, added options
//###           14 Mar 02  -- Tomas Hurka -- -d support, static initializer workaround
//### Please send bug reports to tom@hukatronic.cz
//### static char yysccsid[] = "@(#)yaccpar	1.8 (Berkeley) 01/20/90";






//#line 2 "parser.y"
import java.io.*;
import java.util.*;

//#line 21 "Parser.java"




public class Parser
{

boolean yydebug;        //do I want debug output?
int yynerrs;            //number of errors so far
int yyerrflag;          //was there an error?
int yychar;             //the current working character

//########## MESSAGES ##########
//###############################################################
// method: debug
//###############################################################
void debug(String msg)
{
  if (yydebug)
    System.out.println(msg);
}

//########## STATE STACK ##########
final static int YYSTACKSIZE = 500;  //maximum stack size
int statestk[] = new int[YYSTACKSIZE]; //state stack
int stateptr;
int stateptrmax;                     //highest index of stackptr
int statemax;                        //state when highest index reached
//###############################################################
// methods: state stack push,pop,drop,peek
//###############################################################
final void state_push(int state)
{
  try {
		stateptr++;
		statestk[stateptr]=state;
	 }
	 catch (ArrayIndexOutOfBoundsException e) {
     int oldsize = statestk.length;
     int newsize = oldsize * 2;
     int[] newstack = new int[newsize];
     System.arraycopy(statestk,0,newstack,0,oldsize);
     statestk = newstack;
     statestk[stateptr]=state;
  }
}
final int state_pop()
{
  return statestk[stateptr--];
}
final void state_drop(int cnt)
{
  stateptr -= cnt; 
}
final int state_peek(int relative)
{
  return statestk[stateptr-relative];
}
//###############################################################
// method: init_stacks : allocate and prepare stacks
//###############################################################
final boolean init_stacks()
{
  stateptr = -1;
  val_init();
  return true;
}
//###############################################################
// method: dump_stacks : show n levels of the stacks
//###############################################################
void dump_stacks(int count)
{
int i;
  System.out.println("=index==state====value=     s:"+stateptr+"  v:"+valptr);
  for (i=0;i<count;i++)
    System.out.println(" "+i+"    "+statestk[i]+"      "+valstk[i]);
  System.out.println("======================");
}


//########## SEMANTIC VALUES ##########
//public class ParserVal is defined in ParserVal.java


String   yytext;//user variable to return contextual strings
ParserVal yyval; //used to return semantic vals from action routines
ParserVal yylval;//the 'lval' (result) I got from yylex()
ParserVal valstk[];
int valptr;
//###############################################################
// methods: value stack push,pop,drop,peek.
//###############################################################
void val_init()
{
  valstk=new ParserVal[YYSTACKSIZE];
  yyval=new ParserVal();
  yylval=new ParserVal();
  valptr=-1;
}
void val_push(ParserVal val)
{
  if (valptr>=YYSTACKSIZE)
    return;
  valstk[++valptr]=val;
}
ParserVal val_pop()
{
  if (valptr<0)
    return new ParserVal();
  return valstk[valptr--];
}
void val_drop(int cnt)
{
int ptr;
  ptr=valptr-cnt;
  if (ptr<0)
    return;
  valptr = ptr;
}
ParserVal val_peek(int relative)
{
int ptr;
  ptr=valptr-relative;
  if (ptr<0)
    return new ParserVal();
  return valstk[ptr];
}
final ParserVal dup_yyval(ParserVal val)
{
  ParserVal dup = new ParserVal();
  dup.ival = val.ival;
  dup.dval = val.dval;
  dup.sval = val.sval;
  dup.obj = val.obj;
  return dup;
}
//#### end semantic value section ####
public final static short NUM=257;
public final static short ID=258;
public final static short IF=259;
public final static short ELSE=260;
public final static short ENDIF=261;
public final static short DO=262;
public final static short DONE=263;
public final static short WHILE=264;
public final static short THEN=265;
public final static short BEGIN=266;
public final static short END=267;
public final static short FUNCTION=268;
public final static short RETURN=269;
public final static short LE=270;
public final static short GE=271;
public final static short ASSIGN=272;
public final static short YYERRCODE=256;
final static short yylhs[] = {                           -1,
    0,    1,    1,    2,    2,    2,    2,    3,    4,    5,
    6,    6,    6,    6,    7,    7,    7,    7,    7,    7,
    7,    7,    7,    7,    7,    8,    9,    9,    9,   10,
   11,   11,   11,   11,   11,
};
final static short yylen[] = {                            2,
    3,    0,    2,    2,    2,    2,    1,    3,    9,    7,
    1,    1,    1,    1,    3,    3,    3,    3,    3,    3,
    3,    3,    3,    3,    3,   11,    0,    1,    3,    4,
    0,    1,    3,    1,    3,
};
final static short yydefred[] = {                         0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    7,    0,    0,    0,    0,    1,    3,    4,    5,
    6,   11,    0,    0,    0,   13,   14,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   25,
    0,    0,    0,    0,    0,    0,    0,   17,   18,   19,
    0,    0,    0,    0,    0,    0,   30,    0,    0,   29,
    0,   35,   33,    0,   10,    0,    0,    0,    9,    0,
    0,   26,
};
final static short yydgoto[] = {                          2,
    7,    8,    9,   10,   11,   25,   26,   12,   46,   27,
   49,
};
final static short yysindex[] = {                      -250,
 -119,    0, -254,  -14,  -13, -230, -229, -119,  -16,  -12,
  -11,    0,  -39,  -39,  -39,    5,    0,    0,    0,    0,
    0,    0,    9,  -39,   27,    0,    0,  -30,   -8, -208,
 -255,   -1,  -39,  -39,  -39,  -39,  -39,  -39,  -39,  -39,
  -39,  -39, -214, -207,   12,   17,   22,   24,   30,    0,
  126,  126,  126,  126,  126,  -28,  -28,    0,    0,    0,
 -119, -119, -208, -190, -255, -255,    0, -182, -178,    0,
 -119,    0,    0, -119,    0, -183, -165,  -39,    0,   20,
 -169,    0,
};
final static short yyrindex[] = {                         0,
 -161,    0,    0,    0,    0,    0,    0,  -86,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,  -37,    0,   48,    0,    0,    0,    0,   81,
   86,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,   87,    0,   88,   95,    0,    0,
   42,   49,   59,   64,   71,   32,   54,    0,    0,    0,
 -143, -122,   81,    0,   86,   86,    0,    0,    0,    0,
 -127,    0,    0, -118,    0,    0,    0,    0,    0,    0,
    0,    0,
};
final static short yygindex[] = {                         0,
   76,    0,    0,    0,    0,  120,    0,    0,   83,    0,
  -45,
};
final static int YYTABLESIZE=342;
static short yytable[];
static { yytable();}
static void yytable(){
yytable = new short[]{                         12,
   24,   47,   48,   12,   12,   12,   42,   12,   42,   12,
   43,   40,   38,   40,   39,    1,   41,   13,   41,   72,
   73,   12,   12,   12,   12,   14,   15,   16,   42,   35,
   37,   36,   44,   40,   38,   42,   39,   17,   41,   50,
   40,   38,   19,   39,   30,   41,   20,   21,   31,   45,
   61,   35,   37,   36,   62,   63,   42,   64,   35,   37,
   36,   40,   38,   42,   39,   65,   41,   66,   40,   38,
   67,   39,   15,   41,   15,   71,   15,   74,   81,   35,
   37,   36,   20,   18,   75,   78,   35,   37,   36,   21,
   15,   15,   15,   15,   16,   79,   16,   82,   16,   22,
   20,   20,   20,   20,   23,    2,    8,   21,   21,   21,
   21,   24,   16,   16,   16,   16,    2,   22,   22,   22,
   22,   27,   23,   23,   23,   23,   31,   28,   34,   24,
   24,   24,   24,   28,   29,   32,   68,   69,    3,    4,
    2,    2,    2,   32,    5,   70,   76,    0,    6,   77,
    0,    0,   51,   52,   53,   54,   55,   56,   57,   58,
   59,   60,   42,    0,    0,    0,    0,   40,   38,    0,
   39,    0,   41,    2,    2,    0,    2,    0,    0,    0,
    2,    0,    2,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   80,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,   22,   23,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,   12,   12,    0,    0,    0,    0,    0,   33,
   34,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,   33,   34,    0,    0,    0,    0,    0,   33,   34,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   33,
   34,    0,    0,    0,    0,    0,   33,   34,    0,    0,
    0,   15,   15,    0,    0,    0,    0,    0,    0,    0,
    0,   20,   20,    0,    0,    0,    0,    0,   21,   21,
    0,    0,    0,   16,   16,    0,    0,    0,   22,   22,
    0,    0,    0,   23,   23,    0,    0,    0,    0,    0,
   24,   24,
};
}
static short yycheck[];
static { yycheck(); }
static void yycheck() {
yycheck = new short[] {                         37,
   40,  257,  258,   41,   42,   43,   37,   45,   37,   47,
   41,   42,   43,   42,   45,  266,   47,  272,   47,   65,
   66,   59,   60,   61,   62,   40,   40,  258,   37,   60,
   61,   62,   41,   42,   43,   37,   45,  267,   47,   41,
   42,   43,   59,   45,   40,   47,   59,   59,   40,  258,
  265,   60,   61,   62,  262,   44,   37,   41,   60,   61,
   62,   42,   43,   37,   45,   44,   47,   44,   42,   43,
   41,   45,   41,   47,   43,  266,   45,  260,   59,   60,
   61,   62,   41,    8,  263,  269,   60,   61,   62,   41,
   59,   60,   61,   62,   41,  261,   43,  267,   45,   41,
   59,   60,   61,   62,   41,  267,   59,   59,   60,   61,
   62,   41,   59,   60,   61,   62,  260,   59,   60,   61,
   62,   41,   59,   60,   61,   62,   41,   41,   41,   59,
   60,   61,   62,   14,   15,   41,   61,   62,  258,  259,
  263,  269,  261,   24,  264,   63,   71,   -1,  268,   74,
   -1,   -1,   33,   34,   35,   36,   37,   38,   39,   40,
   41,   42,   37,   -1,   -1,   -1,   -1,   42,   43,   -1,
   45,   -1,   47,  260,  261,   -1,  263,   -1,   -1,   -1,
  267,   -1,  269,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   78,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,  257,  258,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,  270,  271,   -1,   -1,   -1,   -1,   -1,  270,
  271,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,  270,  271,   -1,   -1,   -1,   -1,   -1,  270,  271,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,   -1,  270,
  271,   -1,   -1,   -1,   -1,   -1,  270,  271,   -1,   -1,
   -1,  270,  271,   -1,   -1,   -1,   -1,   -1,   -1,   -1,
   -1,  270,  271,   -1,   -1,   -1,   -1,   -1,  270,  271,
   -1,   -1,   -1,  270,  271,   -1,   -1,   -1,  270,  271,
   -1,   -1,   -1,  270,  271,   -1,   -1,   -1,   -1,   -1,
  270,  271,
};
}
final static short YYFINAL=2;
final static short YYMAXTOKEN=272;
final static String yyname[] = {
"end-of-file",null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,null,
null,null,null,null,null,null,null,"'%'",null,null,"'('","')'","'*'","'+'",
"','","'-'",null,"'/'",null,null,null,null,null,null,null,null,null,null,null,
"';'","'<'","'='","'>'",null,null,null,null,null,null,null,null,null,null,null,
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
null,null,null,null,null,null,null,"NUM","ID","IF","ELSE","ENDIF","DO","DONE",
"WHILE","THEN","BEGIN","END","FUNCTION","RETURN","LE","GE","ASSIGN",
};
final static String yyrule[] = {
"$accept : program",
"program : BEGIN seq END",
"seq :",
"seq : stmt seq",
"stmt : assignment ';'",
"stmt : if1 ';'",
"stmt : loop ';'",
"stmt : funcdef",
"assignment : ID ASSIGN expr",
"if1 : IF '(' expr ')' THEN seq ELSE seq ENDIF",
"loop : WHILE '(' expr ')' DO seq DONE",
"expr : NUM",
"expr : ID",
"expr : binexpr",
"expr : funccall",
"binexpr : expr '+' expr",
"binexpr : expr '-' expr",
"binexpr : expr '*' expr",
"binexpr : expr '/' expr",
"binexpr : expr '%' expr",
"binexpr : expr LE expr",
"binexpr : expr GE expr",
"binexpr : expr '<' expr",
"binexpr : expr '>' expr",
"binexpr : expr '=' expr",
"binexpr : '(' expr ')'",
"funcdef : FUNCTION ID '(' params ')' BEGIN seq RETURN expr ';' END",
"params :",
"params : ID",
"params : ID ',' params",
"funccall : ID '(' args ')'",
"args :",
"args : ID",
"args : ID ',' args",
"args : NUM",
"args : NUM ',' args",
};

//#line 220 "parser.y"

private Yylex lexer;
public Seq program;

private int yylex() {
    int yyl_return = -1;
    try {
        yyl_return = lexer.yylex();
    } catch (IOException e) {
        System.err.println("IO error : " + e);
    }
    return yyl_return;
}

public Parser(Reader r) {
    lexer = new Yylex(r, this);
}

/* error handler */
public void yyerror(String s) {
    System.err.println("Parse error: " + s);
}

public static void main(String args[]) throws IOException {
    Parser yyparser = new Parser(new FileReader(args[0]));
    
    if (yyparser.yyparse() == 0)
    {
        ASTNode root = yyparser.program;

        System.out.println("Printing the CFG First");
        root.print(0);
        System.out.println("Done printing the CFG");

        CFG cfgObj = new CFG();
        CFGNode cfg = cfgObj.createCFG(root).begin;

        System.out.println("NODES:\n");
        System.out.println(cfg.printNodes());

        System.out.println("\nEDGES:\n");
        System.out.println(cfg.printEdges());

        lcm_worklist lcm_worklist_imp = new lcm_worklist();
        lcm_worklist_imp.init(cfg, cfgObj.getTotalNodes());
        lcm_worklist_imp.apply_lcm();

        //System.out.println("\nPrinting the Topological sorted order\n");
        //lcm_impl lcm_imp = new lcm_impl();
        //lcm_imp.pass1(cfg, cfgObj.getTotalNodes());
    }  
}
//#line 357 "Parser.java"
//###############################################################
// method: yylexdebug : check lexer state
//###############################################################
void yylexdebug(int state,int ch)
{
String s=null;
  if (ch < 0) ch=0;
  if (ch <= YYMAXTOKEN) //check index bounds
     s = yyname[ch];    //now get it
  if (s==null)
    s = "illegal-symbol";
  debug("state "+state+", reading "+ch+" ("+s+")");
}





//The following are now global, to aid in error reporting
int yyn;       //next next thing to do
int yym;       //
int yystate;   //current parsing state from state table
String yys;    //current token string


//###############################################################
// method: yyparse : parse input and execute indicated items
//###############################################################
int yyparse()
{
boolean doaction;
  init_stacks();
  yynerrs = 0;
  yyerrflag = 0;
  yychar = -1;          //impossible char forces a read
  yystate=0;            //initial state
  state_push(yystate);  //save it
  val_push(yylval);     //save empty value
  while (true) //until parsing is done, either correctly, or w/error
    {
    doaction=true;
    if (yydebug) debug("loop"); 
    //#### NEXT ACTION (from reduction table)
    for (yyn=yydefred[yystate];yyn==0;yyn=yydefred[yystate])
      {
      if (yydebug) debug("yyn:"+yyn+"  state:"+yystate+"  yychar:"+yychar);
      if (yychar < 0)      //we want a char?
        {
        yychar = yylex();  //get next token
        if (yydebug) debug(" next yychar:"+yychar);
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
          debug("state "+yystate+", shifting to state "+yytable[yyn]);
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
      if (yyerrflag==0)
        {
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
          else
            {
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
          if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
          if (yys == null) yys = "illegal-symbol";
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
    if (yym>0)                 //if count of rhs not 'nil'
      yyval = val_peek(yym-1); //get current semantic value
    yyval = dup_yyval(yyval); //duplicate yyval if ParserVal is used as semantic value
    switch(yyn)
      {
//########## USER-SUPPLIED ACTIONS ##########
case 1:
//#line 26 "parser.y"
{ program = (Seq)val_peek(1).obj; }
break;
case 2:
//#line 30 "parser.y"
{ yyval.obj = new Seq(new ArrayList<Stmt>()); }
break;
case 3:
//#line 33 "parser.y"
{
        ArrayList<Stmt> stmts = ((Seq)val_peek(0).obj).statements;
        stmts.add(0, (Stmt)val_peek(1).obj);
    
        yyval.obj = new Seq(stmts);
      }
break;
case 8:
//#line 47 "parser.y"
{ 
    Id id = new Id(val_peek(2).sval);
    yyval.obj = new Assignment(id, (Expression)val_peek(0).obj);
}
break;
case 9:
//#line 56 "parser.y"
{ yyval.obj = new If1((Expression)val_peek(6).obj, (Stmt)val_peek(3).obj, (Stmt)val_peek(1).obj); }
break;
case 10:
//#line 62 "parser.y"
{ yyval.obj = new Loop((Expression)val_peek(4).obj, (Stmt)val_peek(1).obj); }
break;
case 11:
//#line 66 "parser.y"
{ yyval.obj = new Num(val_peek(0).ival); }
break;
case 12:
//#line 69 "parser.y"
{ yyval.obj = new Id(val_peek(0).sval); }
break;
case 15:
//#line 76 "parser.y"
{
        Operator op = new Operator("+");
        yyval.obj = new BinaryExpression((Expression)val_peek(2).obj, (Expression)val_peek(0).obj, op);
      }
break;
case 16:
//#line 82 "parser.y"
{
        Operator op = new Operator("-");
        yyval.obj = new BinaryExpression((Expression)val_peek(2).obj, (Expression)val_peek(0).obj, op);
      }
break;
case 17:
//#line 88 "parser.y"
{
        Operator op = new Operator("*");
        yyval.obj = new BinaryExpression((Expression)val_peek(2).obj, (Expression)val_peek(0).obj, op);
      }
break;
case 18:
//#line 94 "parser.y"
{
        Operator op = new Operator("/");
        yyval.obj = new BinaryExpression((Expression)val_peek(2).obj, (Expression)val_peek(0).obj, op);
      }
break;
case 19:
//#line 100 "parser.y"
{
        Operator op = new Operator("%");
        yyval.obj = new BinaryExpression((Expression)val_peek(2).obj, (Expression)val_peek(0).obj, op);
      }
break;
case 20:
//#line 106 "parser.y"
{
        Operator op = new Operator("<=");
        yyval.obj = new BinaryExpression((Expression)val_peek(2).obj, (Expression)val_peek(0).obj, op);
      }
break;
case 21:
//#line 112 "parser.y"
{
        Operator op = new Operator(">=");
        yyval.obj = new BinaryExpression((Expression)val_peek(2).obj, (Expression)val_peek(0).obj, op);
      }
break;
case 22:
//#line 118 "parser.y"
{
        Operator op = new Operator("<");
        yyval.obj = new BinaryExpression((Expression)val_peek(2).obj, (Expression)val_peek(0).obj, op);
      }
break;
case 23:
//#line 124 "parser.y"
{
        Operator op = new Operator(">");
        yyval.obj = new BinaryExpression((Expression)val_peek(2).obj, (Expression)val_peek(0).obj, op);
      }
break;
case 24:
//#line 130 "parser.y"
{
        Operator op = new Operator("=");
        yyval.obj = new BinaryExpression((Expression)val_peek(2).obj, (Expression)val_peek(0).obj, op);
      }
break;
case 25:
//#line 136 "parser.y"
{ yyval.obj = val_peek(1).obj; }
break;
case 26:
//#line 143 "parser.y"
{
    Id id = new Id(val_peek(9).sval);
    yyval.obj = new FuncDef(id, new Params((ArrayList)val_peek(7).obj), (Stmt)val_peek(4).obj, (Expression)val_peek(2).obj);
}
break;
case 27:
//#line 150 "parser.y"
{ yyval.obj = new ArrayList<Id>(); }
break;
case 28:
//#line 153 "parser.y"
{  
        Id id = new Id(val_peek(0).sval);

        ArrayList<Id> p = new ArrayList<Id>();
        p.add(id);

        yyval.obj = p;
      }
break;
case 29:
//#line 163 "parser.y"
{
        Id id = new Id(val_peek(2).sval);
        ArrayList<Id> p = (ArrayList)val_peek(0).obj;
        p.add(0, id);

        yyval.obj = p;
      }
break;
case 30:
//#line 172 "parser.y"
{
    Id id = new Id(val_peek(3).sval);
    yyval.obj = new FuncCall(id, new Args((ArrayList)val_peek(1).obj));
}
break;
case 31:
//#line 179 "parser.y"
{ yyval.obj = new ArrayList<Expression>(); }
break;
case 32:
//#line 182 "parser.y"
{  
        Id id = new Id(val_peek(0).sval);

        ArrayList<Id> p = new ArrayList<Id>();
        p.add(id);

        yyval.obj = p;
      }
break;
case 33:
//#line 192 "parser.y"
{
        Id id = new Id(val_peek(2).sval);
        ArrayList<Expression> p = (ArrayList)val_peek(0).obj;
        p.add(0, id);

        yyval.obj = p;
      }
break;
case 34:
//#line 201 "parser.y"
{  
        Num num = new Num(val_peek(0).ival);

        ArrayList<Expression> p = new ArrayList<Expression>();
        p.add(num);

        yyval.obj = p;
      }
break;
case 35:
//#line 211 "parser.y"
{
        Num num = new Num(val_peek(2).ival);
        ArrayList<Expression> p = (ArrayList)val_peek(0).obj;
        p.add(0, num);

        yyval.obj = p;
      }
break;
//#line 705 "Parser.java"
//########## END OF USER-SUPPLIED ACTIONS ##########
    }//switch
    //#### Now let's reduce... ####
    if (yydebug) debug("reduce");
    state_drop(yym);             //we just reduced yylen states
    yystate = state_peek(0);     //get new state
    val_drop(yym);               //corresponding value drop
    yym = yylhs[yyn];            //select next TERMINAL(on lhs)
    if (yystate == 0 && yym == 0)//done? 'rest' state and at first TERMINAL
      {
      if (yydebug) debug("After reduction, shifting from state 0 to state "+YYFINAL+"");
      yystate = YYFINAL;         //explicitly say we're done
      state_push(YYFINAL);       //and save it
      val_push(yyval);           //also save the semantic value of parsing
      if (yychar < 0)            //we want another character?
        {
        yychar = yylex();        //get next character
        if (yychar<0) yychar=0;  //clean, if necessary
        if (yydebug)
          yylexdebug(yystate,yychar);
        }
      if (yychar == 0)          //Good exit (if lex returns 0 ;-)
         break;                 //quit the loop--all DONE
      }//if yystate
    else                        //else not done yet
      {                         //get next state and push, for next yydefred[]
      yyn = yygindex[yym];      //find out where to go
      if ((yyn != 0) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn]; //get new state
      else
        yystate = yydgoto[yym]; //else go to new defred
      if (yydebug) debug("after reduction, shifting from state "+state_peek(0)+" to state "+yystate+"");
      state_push(yystate);     //going again, so push state & val...
      val_push(yyval);         //for next action
      }
    }//main loop
  return 0;//yyaccept!!
}
//## end of method parse() ######################################



//## run() --- for Thread #######################################
/**
 * A default run method, used for operating this parser
 * object in the background.  It is intended for extending Thread
 * or implementing Runnable.  Turn off with -Jnorun .
 */
public void run()
{
  yyparse();
}
//## end of method run() ########################################



//## Constructors ###############################################
/**
 * Default constructor.  Turn off with -Jnoconstruct .

 */
public Parser()
{
  //nothing to do
}


/**
 * Create a parser, setting the debug to true or false.
 * @param debugMe true for debugging, false for no debug.
 */
public Parser(boolean debugMe)
{
  yydebug=debugMe;
}
//###############################################################



}
//################### END OF CLASS ##############################

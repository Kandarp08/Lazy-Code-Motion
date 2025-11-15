%%

%byaccj

%{
  /* store a reference to the parser object */
  private Parser yyparser;

  /* constructor taking an additional parser object */
  public Yylex(java.io.Reader r, Parser yyparser) {
    this(r);
    this.yyparser = yyparser;
  }
%}

/* Macros */
DIGIT = [0-9]
ID    = [a-zA-Z_][a-zA-Z0-9_]*
NUM   = {DIGIT}+

%%

/* keywords */
"begin"                    { return Parser.BEGIN; }
"end"                      { return Parser.END; }
"if"                       { return Parser.IF; }
"else"                     { return Parser.ELSE; }
"endif"                    { return Parser.ENDIF; }
"while"                    { return Parser.WHILE; }
"do"                       { return Parser.DO; }
"done"                     { return Parser.DONE; }
"then"                     { return Parser.THEN; }
"function"                 { return Parser.FUNCTION; }
"return"                   { return Parser.RETURN; }

{NUM}                      { yyparser.yylval = new ParserVal(Integer.parseInt(yytext()));  return Parser.NUM; }
{ID}                       { yyparser.yylval = new ParserVal(yytext());                    return Parser.ID; }

/* multi-char operators */
"<="                       { return Parser.LE; }
">="                       { return Parser.GE; }
":="                       { return Parser.ASSIGN; }

/* single-char tokens */
";"            { return ';'; }
"("            { return '('; }
")"            { return ')'; }
","            { return ','; }
"+"            { return '+'; }
"-"            { return '-'; }
"*"            { return '*'; }
"/"            { return '/'; }
"%"            { return '%'; }
"<"            { return '<'; }
">"            { return '>'; }
"="            { return '='; }

/* whitespace */
[ \t\r\n]+                 { /* skip */ }

/* unknown characters */
.                          { System.err.println("Unknown token: " + yytext()); }

/* end of input */
<<EOF>>                    { return 0; }

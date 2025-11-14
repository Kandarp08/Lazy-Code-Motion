%{
import java.util.ArrayList;
import java.io.*;
%}

%token <ival> NUM
%token <sval> ID TRUE FALSE

%token IF ELSE WHILE BEGIN END FUNCTION RETURN INT BOOL
%token LE GE ASSIGN      /* <=, >=, := */

%type <obj> seq stmt assignment if1 loop expr binexpr funcdef params funccall args decl block funcdefs scope
%type <sval> LE GE ASSIGN '<' '>' '=' '+' '-' '*' '/' '%'

/* Operator precedences (low to high) */
%left '<' '>' LE GE '='
%left '+' '-'
%left '*' '/' '%'

%%

program: BEGIN
         funcdefs
         scope
         END
{ 
    this.mainScope = (Scope)$3;
    this.funcDefs = (FuncDefs)$2;
}

scope:
      '{'
      block
      seq
      '}'
{
  Block block = (Block)$2;
  Seq seq = (Seq)$3;

  $$ = new Scope(block, seq);
}

block:
      /* empty */
      { $$ = new Block(); }
    
    | decl ';'
      block
      {
        Block block = (Block)$3;
        block.addDecl((Declaration)$1);

        $$ = block;
      }

decl:
      INT ID
      { 
        Id id = new Id($2);
        $$ = new Declaration("int", id);
      }

    | BOOL ID
      { 
        Id id = new Id($2);
        $$ = new Declaration("bool", id);
      }

funcdefs:
          /* empty */
          { $$ = new FuncDefs(); }
        
        | funcdef funcdefs
          {
            FuncDefs funcDefs = (FuncDefs)$2;
            FuncDef funcDef = (FuncDef)$1;

            funcDefs.addFuncDef(funcDef);

            $$ = funcDefs;
          }
  
seq:
      /* empty */
      { 
        $$ = new Seq();
      }

    | stmt seq
      {
        Seq seq = (Seq)$2;
        Stmt stmt = (Stmt)$1;

        seq.addStmt(stmt);

        $$ = seq;
      }

stmt:
      assignment
    | if1
    | loop
    | scope

assignment: ID ASSIGN expr ';'
{ 
  Id id = new Id($1);
  Expression expr = (Expression)$3;

  $$ = new Assignment(id, expr);
}

if1: IF '(' expr ')'
     scope
     ELSE
     scope
{ 
  Expression condition = (Expression)$3;
  Scope ifStmt = (Scope)$5;
  Scope elseStmt = (Scope)$7;

  $$ = new If1(condition, ifStmt, elseStmt);
}

loop: WHILE '(' expr ')'
      scope
{ 
  Expression condition = (Expression)$3;
  Scope loopBody = (Scope)$5;

  $$ = new Loop(condition, loopBody);
}

expr:
      NUM   
      { $$ = new Num($1); }

    | ID        
      { $$ = new Id($1); }

    | TRUE
      { $$ = new Bool(true); }

    | FALSE
      { $$ = new Bool(false); }
    
    | '(' expr ')'   
      { $$ = (Expression)$2; }

    | binexpr
    | funccall

binexpr:
      expr '+' expr               
      {
        Expression expr1 = (Expression)$1;
        Expression expr2 = (Expression)$3;
        Operator op = new Operator("+");

        $$ = new BinaryExpression(expr1, expr2, op);
      }
    
    | expr '-' expr               
      {
        Expression expr1 = (Expression)$1;
        Expression expr2 = (Expression)$3;
        Operator op = new Operator("-");

        $$ = new BinaryExpression(expr1, expr2, op);
      }
    
    | expr '*' expr               
      {
        Expression expr1 = (Expression)$1;
        Expression expr2 = (Expression)$3;
        Operator op = new Operator("*");

        $$ = new BinaryExpression(expr1, expr2, op);
      }
    
    | expr '/' expr                      
      {
        Expression expr1 = (Expression)$1;
        Expression expr2 = (Expression)$3;
        Operator op = new Operator("/");

        $$ = new BinaryExpression(expr1, expr2, op);
      }
    
    | expr '%' expr               
      {
        Expression expr1 = (Expression)$1;
        Expression expr2 = (Expression)$3;
        Operator op = new Operator("%");

        $$ = new BinaryExpression(expr1, expr2, op);
      }
    
    | expr LE expr                
      {
        Expression expr1 = (Expression)$1;
        Expression expr2 = (Expression)$3;
        Operator op = new Operator("<=");

        $$ = new BinaryExpression(expr1, expr2, op);
      }
    
    | expr GE expr                
      {
        Expression expr1 = (Expression)$1;
        Expression expr2 = (Expression)$3;
        Operator op = new Operator(">=");

        $$ = new BinaryExpression(expr1, expr2, op);
      }
    
    | expr '<' expr               
      {
        Expression expr1 = (Expression)$1;
        Expression expr2 = (Expression)$3;
        Operator op = new Operator("<");

        $$ = new BinaryExpression(expr1, expr2, op);
      }

    | expr '>' expr               
      {
        Expression expr1 = (Expression)$1;
        Expression expr2 = (Expression)$3;
        Operator op = new Operator(">");

        $$ = new BinaryExpression(expr1, expr2, op);
      }
    
    | expr '=' expr               
      {
        Expression expr1 = (Expression)$1;
        Expression expr2 = (Expression)$3;
        Operator op = new Operator("=");

        $$ = new BinaryExpression(expr1, expr2, op);
      }            

funcdef: FUNCTION ID '(' params ')'
         '{'
            block
            seq
            RETURN expr ';'
         '}'
{
  Id id = new Id($2);
  Params params = new Params((ArrayList<Declaration>)$4);
  Block block = (Block)$7;
  Seq seq = (Seq)$8;
  Expression expr = (Expression)$10;

  $$ = new FuncDef(id, params, block, seq, expr);
}

params: 
      /* empty */
      { $$ = new ArrayList<Declaration>(); }
    
    | decl
      {  
        ArrayList<Declaration> arr = new ArrayList<Declaration>();
        Declaration decl = (Declaration)$1;

        arr.add(0, decl);

        $$ = arr; 
      }

    | decl ',' params
      {
        ArrayList<Declaration> arr = (ArrayList<Declaration>)$3;
        Declaration decl = (Declaration)$1;

        arr.add(0, decl);

        $$ = arr;
      }

funccall: ID '(' args ')'
{
  Id id = new Id($1);
  Args args = new Args((ArrayList<Expression>)$3);

  $$ = new FuncCall(id, args);
}

args:
      /* empty */
      { $$ = new ArrayList<Expression>(); }

    | expr
      {  
        ArrayList<Expression> arr = new ArrayList<Expression>();
        Expression expr = (Expression)$1;

        arr.add(0, expr);

        $$ = arr;
      }

    | expr ',' args
      {
        ArrayList<Expression> arr = (ArrayList<Expression>)$3;
        Expression expr = (Expression)$1;

        arr.add(0, expr);

        $$ = arr;
      }

%%

private Yylex lexer;
private Scope mainScope;
private FuncDefs funcDefs;

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

public Scope getScope()
{
    return mainScope;
}

public FuncDefs getFuncDefs()
{
    return funcDefs;
}
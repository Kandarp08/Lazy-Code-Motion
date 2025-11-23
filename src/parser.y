%{
import java.io.*;
import java.util.*;

%}

%token <ival> NUM
%token <sval> ID

%token IF ELSE ENDIF DO DONE WHILE THEN BEGIN END FUNCTION RETURN
%token LE GE ASSIGN      /* <=, >=, := */

%type <obj> seq stmt assignment if1 loop expr binexpr funcdef params funccall args
%type <sval> LE GE ASSIGN '<' '>' '=' '+' '-' '*' '/' '%'

/* Operator precedences (low to high) */
%left '<' '>' LE GE '='
%left '+' '-'
%left '*' '/' '%'

%%

program: BEGIN
         seq
         END  
{ program = (Seq)$2; }

seq:
      /* empty */
      { $$ = new Seq(new ArrayList<Stmt>()); }

    | stmt seq
      {
        ArrayList<Stmt> stmts = ((Seq)$2).statements;
        stmts.add(0, (Stmt)$1);
    
        $$ = new Seq(stmts);
      }

stmt:
      assignment ';'
    | if1 ';'
    | loop ';'
    | funcdef

assignment: ID ASSIGN expr  
{ 
    Id id = new Id($1);
    $$ = new Assignment(id, (Expression)$3);
}

if1: IF '(' expr ')' THEN seq
     ELSE seq
     ENDIF

{ $$ = new If1((Expression)$3, (Stmt)$6, (Stmt)$8); }

loop: WHILE '(' expr ')' DO
      seq
      DONE

{ $$ = new Loop((Expression)$3, (Stmt)$6); }

expr:
      NUM   
      { $$ = new Num($1); }

    | ID        
      { $$ = new Id($1); }

    | binexpr
    | funccall

binexpr:
      expr '+' expr               
      {
        Operator op = new Operator("+");
        $$ = new BinaryExpression((Expression)$1, (Expression)$3, op);
      }
    
    | expr '-' expr               
      {
        Operator op = new Operator("-");
        $$ = new BinaryExpression((Expression)$1, (Expression)$3, op);
      }
    
    | expr '*' expr               
      {
        Operator op = new Operator("*");
        $$ = new BinaryExpression((Expression)$1, (Expression)$3, op);
      }
    
    | expr '/' expr                      
      {
        Operator op = new Operator("/");
        $$ = new BinaryExpression((Expression)$1, (Expression)$3, op);
      }
    
    | expr '%' expr               
      {
        Operator op = new Operator("%");
        $$ = new BinaryExpression((Expression)$1, (Expression)$3, op);
      }
    
    | expr LE expr                
      {
        Operator op = new Operator("<=");
        $$ = new BinaryExpression((Expression)$1, (Expression)$3, op);
      }
    
    | expr GE expr                
      {
        Operator op = new Operator(">=");
        $$ = new BinaryExpression((Expression)$1, (Expression)$3, op);
      }
    
    | expr '<' expr               
      {
        Operator op = new Operator("<");
        $$ = new BinaryExpression((Expression)$1, (Expression)$3, op);
      }

    | expr '>' expr               
      {
        Operator op = new Operator(">");
        $$ = new BinaryExpression((Expression)$1, (Expression)$3, op);
      }
    
    | expr '=' expr               
      {
        Operator op = new Operator("=");
        $$ = new BinaryExpression((Expression)$1, (Expression)$3, op);
      }

    | '(' expr ')'   
      { $$ = $2; }            

funcdef: FUNCTION ID '(' params ')'
         BEGIN
            seq
            RETURN expr ';'
         END
{
    Id id = new Id($2);
    $$ = new FuncDef(id, new Params((ArrayList)$4), (Stmt)$7, (Expression)$9);
}

params: 
      /* empty */
      { $$ = new ArrayList<Id>(); }
    
    | ID
      {  
        Id id = new Id($1);

        ArrayList<Id> p = new ArrayList<Id>();
        p.add(id);

        $$ = p;
      }

    | ID ',' params
      {
        Id id = new Id($1);
        ArrayList<Id> p = (ArrayList)$3;
        p.add(0, id);

        $$ = p;
      }

funccall: ID '(' args ')'
{
    Id id = new Id($1);
    $$ = new FuncCall(id, new Args((ArrayList)$3));
}

args:
      /* empty */
      { $$ = new ArrayList<Expression>(); }

    | ID
      {  
        Id id = new Id($1);

        ArrayList<Id> p = new ArrayList<Id>();
        p.add(id);

        $$ = p;
      }

    | ID ',' args
      {
        Id id = new Id($1);
        ArrayList<Expression> p = (ArrayList)$3;
        p.add(0, id);

        $$ = p;
      }

    | NUM
      {  
        Num num = new Num($1);

        ArrayList<Expression> p = new ArrayList<Expression>();
        p.add(num);

        $$ = p;
      }

    | NUM ',' args
      {
        Num num = new Num($1);
        ArrayList<Expression> p = (ArrayList)$3;
        p.add(0, num);

        $$ = p;
      }

%%

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
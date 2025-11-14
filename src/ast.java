import java.util.ArrayList;

interface ASTNode
{

}

abstract class Expression implements ASTNode
{

}

class Id extends Expression
{
    private String id;

    public Id(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        return id;
    }
}

class Num extends Expression
{
    private int num;

    public Num(int num)
    {
        this.num = num;
    }

    public int getNum()
    {
        return num;
    }
}

class Bool extends Expression
{
    private boolean bool;

    public Bool(boolean bool)
    {
        this.bool = bool;
    }

    public boolean getBool()
    {
        return bool;
    }
}

class Operator implements ASTNode
{
    private String op;

    public Operator(String op)
    {
        this.op = op;
    }

    public String getOperator()
    {
        return op;
    }
}

class BinaryExpression extends Expression
{
    private Expression expression1;
    private Expression expression2;
    private Operator op;

    public BinaryExpression(Expression expression1, Expression expression2, Operator op)
    {
        this.expression1 = expression1;
        this.expression2 = expression2;
        this.op = op;
    }

    public Expression getLeftExpression()
    {
        return expression1;
    }

    public Expression getRightExpression()
    {
        return expression2;
    }

    public String getOperator()
    {
        return op.getOperator();
    }
}

abstract class Stmt implements ASTNode
{

}

class Seq extends Stmt
{
    private ArrayList<Stmt> statements;

    public Seq()
    {
        this.statements = new ArrayList<Stmt>();
    }

    public void addStmt(Stmt stmt)
    {
        statements.add(0, stmt);
    }

    public ArrayList<Stmt> getStatements()
    {
        return statements;
    }
}

class Scope extends Stmt
{
    private Block block;
    private Seq seq;

    public Scope(Block block, Seq seq)
    {
        this.block = block;
        this.seq = seq;
    }

    public Block getBlock()
    {
        return block;
    }

    public Seq getSeq()
    {
        return seq;
    }
}

class Assignment extends Stmt
{
    private Id id;
    private Expression expression;
    
    public Assignment(Id id, Expression expression)
    {
        this.id = id;
        this.expression = expression;
    }

    public String getId()
    {
        return id.getId();
    }

    public Expression getExpression()
    {
        return expression;
    }
}

class If1 extends Stmt
{
    private Expression condition;
    private Scope ifStmt;
    private Scope elseStmt;

    public If1(Expression condition, Scope ifStmt, Scope elseStmt)
    {
        this.condition = condition;
        this.ifStmt = ifStmt;
        this.elseStmt = elseStmt;
    }

    public Expression getCondition()
    {
        return condition;
    }

    public Scope getIfStmt()
    {
        return ifStmt;
    }

    public Scope getElseStmt()
    {
        return elseStmt;
    }
}

class Loop extends Stmt
{
    private Expression condition;
    private Scope stmt;

    public Loop(Expression condition, Scope stmt)
    {
        this.condition = condition;
        this.stmt = stmt;
    }

    public Expression getCondition()
    {
        return condition;
    }

    public Scope getStmt()
    {
        return stmt;
    }
}

class Params implements ASTNode
{
    private ArrayList<Declaration> params;

    public Params(ArrayList<Declaration> params)
    {
        this.params = params;
    }

    public ArrayList<Declaration> getParams()
    {
        return params;
    }
}

class Args implements ASTNode
{
    private ArrayList<Expression> args;

    public Args(ArrayList<Expression> args)
    {
        this.args = args;
    }

    public ArrayList<Expression> getArgs()
    {
        return args;
    }
}

class FuncDef extends Stmt
{
    private Id id;
    private Params params;
    private Block block;
    private Seq funcBody;
    private Expression returnExpr;

    public FuncDef(Id id, Params params, Block block, Seq funcBody, Expression expr)
    {
        this.id = id;
        this.params = params;
        this.block = block;
        this.funcBody = funcBody;
        this.returnExpr = expr;
    }

    public String getId()
    {
        return id.getId();
    }

    public Params getParams()
    {
        return params;
    }

    public Block getBlock()
    {
        return block;
    }

    public Seq getFuncBody()
    {
        return funcBody;
    }

    public Expression getReturnExpression()
    {
        return returnExpr;
    }
}

class FuncCall extends Expression
{
    private Id id;
    private Args args;

    public FuncCall(Id id, Args args)
    {
        this.id = id;
        this.args = args;
    }

    public String getId()
    {
        return id.getId();
    }

    public Args getArgs()
    {
        return args;
    }
}

class Declaration extends Stmt
{
    private String type;
    private Id id;

    public Declaration(String type, Id id)
    {
        this.type = type;
        this.id = id;
    }

    public String getType()
    {
        return type;
    }

    public String getId()
    {
        return id.getId();
    }
}

class Block
{
    private ArrayList<Declaration> decls;

    public Block()
    {
        this.decls = new ArrayList<Declaration>();
    }

    public void addDecl(Declaration decl)
    {
        decls.add(0, decl);
    }

    public ArrayList<Declaration> getDeclarations()
    {
        return decls;
    }
}

class FuncDefs
{
    private ArrayList<FuncDef> funcDefs;

    public FuncDefs()
    {
        this.funcDefs = new ArrayList<FuncDef>();
    }

    public void addFuncDef(FuncDef funcDef)
    {
        funcDefs.add(0, funcDef);
    }

    public ArrayList<FuncDef> getFuncDefs()
    {
        return funcDefs;
    }
}
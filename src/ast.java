// IMT2022115 - Kandarp Dave

import java.util.List;
import java.util.ArrayList;

interface ASTNode
{
    public String print(int indent); 
}

abstract class Expression implements ASTNode
{
    public void getVariablesUsed(ArrayList<String> varsUsed)
    {

    }
    public String toMathsString()
    {
        return "";   
    }
}

class Id extends Expression
{
    public String id;

    public Id(String id)
    {
        this.id = id;
    }

    public String toString()
    {
        return "(Id " + id + ")";
    }

    public String print(int indent)
    {
        String tabs = "\t".repeat(indent);
        return tabs + toString();
    }
    public void getVariablesUsed(ArrayList<String> varsUsed)
    {
        // varsUsed.add(id);
    }
    public String toMathsString()
    {
        return id;
    }
}

class Num extends Expression
{
    public int num;

    public Num(int num)
    {
        this.num = num;
    }

    public String toString()
    {
        return "(Num " + num + ")";
    }
    
    public String print(int indent)
    {
        String tabs = "\t".repeat(indent);
        return tabs + toString();
    }
    public void getVariablesUsed(ArrayList<String> varsUsed)
    {
        // varsUsed.add(id);
    }
    public String toMathsString()
    {
        return Integer.toString(num);
    }
}

class Operator implements ASTNode
{
    public String op;

    public Operator(String op)
    {
        this.op = op;
    }

    public String toString()
    {
        return "(Op " + op + ")"; 
    }
    
    public String print(int indent)
    {
        String tabs = "\t".repeat(indent);
        return tabs + toString();
    }
}

class BinaryExpression extends Expression
{
    public Expression expression1;
    public Expression expression2;
    public Operator op;

    public BinaryExpression(Expression expression1, Expression expression2, Operator op)
    {
        this.expression1 = expression1;
        this.expression2 = expression2;
        this.op = op;
    }
    
    public String print(int indent)
    {
        String tabs = "\t".repeat(indent);
        String str = "BinExpr\n" + tabs + "\tExpression 1\n" + expression1.print(indent + 2) + "\n" + op.print(indent+1) + "\n" + tabs + "\tExpression 2\n" + expression2.print(indent + 2) + "\n";

        return tabs + str;
    }
    public String toMathsString()
    {
        return "(" + expression1.toMathsString() + " " + op.op + " " + expression2.toMathsString() + ")";
    }
    public void getVariablesUsed(ArrayList<String> varsUsed)
    {
        // varsUsed.add(id);
        expression1.getVariablesUsed(varsUsed);
        expression2.getVariablesUsed(varsUsed);
        // String currBinExprStr = expression1.toMathsString() + " " + op.op + " " + expression2.toMathsString();
        String currBinExprStr = this.toMathsString();
        varsUsed.add(currBinExprStr);
    }
}

abstract class Stmt implements ASTNode
{

}

class Assignment extends Stmt
{
    public Id id;
    public Expression expression;
    
    public Assignment(Id id, Expression expression)
    {
        this.id = id;
        this.expression = expression;
    }
    
    public String print(int indent)
    {
        String tabs = "\t".repeat(indent);
        String str = "Assign(" + id + ")\n" + expression.print(indent + 2);

        return tabs + str;
    }
}

class If1 extends Stmt
{
    public Expression condition;
    public Stmt ifStmt;
    public Stmt elseStmt;

    public If1(Expression condition, Stmt ifStmt, Stmt elseStmt)
    {
        this.condition = condition;
        this.ifStmt = ifStmt;
        this.elseStmt = elseStmt;
    }
    
    public String print(int indent)
    {
        String tabs = "\t".repeat(indent);
        String str = "If1\n" + tabs + "\tCondition\n" + condition.print(indent + 2) + "\n" + tabs + "\t\tIfStmt\n" + ifStmt.print(indent + 2) + tabs + "\t\tElseStmt\n" + elseStmt.print(indent + 2);

        return tabs + str;
    }
}

class Loop extends Stmt
{
    public Expression condition;
    public Stmt stmt;

    public Loop(Expression condition, Stmt stmt)
    {
        this.condition = condition;
        this.stmt = stmt;
    }

    public String print(int indent)
    {
        String tabs = "\t".repeat(indent);
        String str = "Loop\n" + tabs + "Condition\n" + condition.print(indent + 1) + "\n" + tabs + "Stmt\n" + stmt.print(indent + 1);

        return tabs + str;
    }
}

class Params implements ASTNode
{
    public List<Id> params;

    public Params(List<Id> params)
    {
        this.params = params;
    }

    public String toString()
    {
        String str = "Params:";

        for (Id id : params)
            str += " " + id;

        return str;
    }

    public String print(int indent)
    {
        String tabs = "\t".repeat(indent);
        return tabs + toString();
    }
}

class Args implements ASTNode
{
    public List<Expression> args;

    public Args(List<Expression> args)
    {
        this.args = args;
    }

    public String toString()
    {
        String str = "Args:";

        for (Expression expr : args)
            str += " " + expr;

        return str;
    }
    
    public String print(int indent)
    {
        String tabs = "\t".repeat(indent);
        return tabs + toString();
    }
}

class FuncDef extends Stmt
{
    public Id id;
    public Params params;
    public Stmt stmt;
    public Expression returnExpr;

    public FuncDef(Id id, Params params, Stmt stmt, Expression expr)
    {
        this.id = id;
        this.params = params;
        this.stmt = stmt;
        this.returnExpr = expr;
    }
    
    public String print(int indent)
    {
        String tabs = "\t".repeat(indent);
        String str = "FuncDef\n" + tabs + "FuncId: " + id + "\n" + params.print(indent) + "\n" + tabs + "Stmt\n" + stmt.print(indent + 1) + "\n" + tabs + "ReturnExpr\n" + returnExpr.print(indent + 1);

        return tabs + str;
    }
}

class FuncCall extends Expression
{
    public Id id;
    public Args args;

    public FuncCall(Id id, Args args)
    {
        this.id = id;
        this.args = args;
    }
    
    public String print(int indent)
    {
        String tabs = "\t".repeat(indent);
        String str = "FuncCall\n" + tabs + "FuncId: " + id + "\n" + args.print(indent);

        return tabs + str;
    }
    public void getVariablesUsed(ArrayList<String> varsUsed)
    {

    }
    public String toMathsString()
    {
        return "";
    }
}

class Seq extends Stmt
{
    public ArrayList<Stmt> statements;

    public Seq(ArrayList<Stmt> statements)
    {
        this.statements = statements;
    }

    public String print(int indent)
    {
        String tabs = "\t".repeat(indent);
        String str = "Seq\n";

        for (Stmt stmt : statements)
            str += stmt.print(indent + 1) + "\n\n";

        return tabs + str;
    }
}

public class ast
{
    public static void main(String args[])
    {
        Expression num1 = new Num(1);
        Expression num2 = new Num(2);
        Expression num5 = new Num(5);
        
        Id x = new Id("x");
        Id y = new Id("y");
        Id f = new Id("f");
        Id sum = new Id("sum"); 

        Operator add = new Operator("+");
        Operator sub = new Operator("-");
        Operator lt = new Operator("<");
        Operator mod = new Operator("%");
        Operator eq = new Operator("=");

        Stmt assignx = new Assignment(x, num1);
        Stmt assigny = new Assignment(y, num5);

        Expression incx = new BinaryExpression(x, num1, add);
        Expression yminusx = new BinaryExpression(y, x, sub);
        Expression xplusy = new BinaryExpression(x, y, add);
        Expression xmod2 = new BinaryExpression(x, num2, mod);
        Expression ifCond = new BinaryExpression(xmod2, num1, eq);
        Expression whileCond = new BinaryExpression(x, num5, lt);

        Stmt assignx1 = new Assignment(x, incx);
        Stmt assigny1 = new Assignment(y, yminusx);
        Stmt assignsum = new Assignment(sum, xplusy);

        List<Id> params = new ArrayList<Id>();
        params.add(x);
        params.add(y);
        
        List<Expression> arg = new ArrayList<Expression>();
        arg.add(x);
        arg.add(y);

        Params par = new Params(params);
        Args ar = new Args(arg);

        FuncDef funcDef = new FuncDef(f, par, assignsum, sum);
        FuncCall funcCall = new FuncCall(f, ar);

        Stmt assigny2 = new Assignment(y, funcCall);

        ArrayList<Stmt> ifStmt = new ArrayList<Stmt>();
        ifStmt.add(assignx1);
        ifStmt.add(assigny1);
        Seq ifSeq = new Seq(ifStmt);

        If1 if1 = new If1(ifCond, ifSeq, assigny2);
        Loop loop = new Loop(whileCond, if1);

        ArrayList<Stmt> program = new ArrayList<Stmt>();
        program.add(funcDef);
        program.add(assignx);
        program.add(assigny);
        program.add(loop);

        Stmt programSeq = new Seq(program);

        System.out.println(programSeq.print(0));
    }
}
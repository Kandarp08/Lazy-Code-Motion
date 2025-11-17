import java.util.ArrayList;
import java.util.HashSet;
import java.io.*;

interface CFGNode
{
    public int getId();
    public int getColor();
    public String printNodes();
    public String printEdges();
    public void addEntry(CFGNode entry);
    public void addExit(CFGNode exit);
    public ArrayList<CFGNode> getEntries();
    public ArrayList<CFGNode> getExits();
}

class Pair 
{
    public CFGNode begin;
    public CFGNode end;

    public Pair(CFGNode begin, CFGNode end) 
    {
        this.begin = begin;
        this.end = end;
    }
}

class InstructionNode implements CFGNode
{
    public int id;
    public int color;
    public ArrayList<CFGNode> entries;
    public ArrayList<CFGNode> exits;
    public Stmt stmt;

    public InstructionNode(int id, Stmt stmt)
    {
        this.id = id;
        this.color = 0;
        this.stmt = stmt;
        this.entries = new ArrayList<CFGNode>();
        this.exits = new ArrayList<CFGNode>();
    }

    public int getId()
    {
        return id;
    }

    public int getColor()
    {
        return color;
    }

    public CFGNode getEntry()
    {
        return entries.get(0);
    }

    public ArrayList<CFGNode> getEntries()
    {
        return this.entries;
    }

    public void addEntry(CFGNode entry)
    {
        entries.add(entry);
    }

    public CFGNode getExit()
    {
        return exits.get(0);
    }

    public ArrayList<CFGNode> getExits()
    {
        return this.exits;
    }

    public void addExit(CFGNode exit)
    {
        exits.add(exit);
    }

    public String printNodes()
    {
        String str = "Block " + id + "\n{\n";
        str += stmt.print(0);
        str += "\n}\n\n";

        this.color = 1;

        for (CFGNode cfgNode : exits)
        {
            if (cfgNode.getColor() == 0)
                str += cfgNode.printNodes();    
        }

        this.color = 2;

        return str;
    }

    public String printEdges()
    {
        String str = "";
        this.color = 3;

        for (CFGNode cfgNode : exits)
        {
            if (cfgNode.getColor() == 3)
            {
                str += this.id + " --LOOP--> " + cfgNode.getId() + "\n";
                continue;
            }

            else if (cfgNode.getColor() == 4)
            {
                str += this.id + " -> " + cfgNode.getId() + "\n";
                continue;
            }

            str += this.id + " -> " + cfgNode.getId() + "\n";
            str += cfgNode.printEdges();
        }

        this.color = 4;

        return str;
    }
}

abstract class DecisionNode implements CFGNode
{

}

class IfNode extends DecisionNode
{
    public int id;
    public int color;
    public ArrayList<CFGNode> entries;
    public ArrayList<CFGNode> exits;
    public If1 if1;

    public IfNode(int id, If1 if1)
    {
        this.id = id;
        this.color = 0;
        this.if1 = if1;
        this.entries = new ArrayList<CFGNode>();
        this.exits = new ArrayList<CFGNode>();
    }

    public int getId()
    {
        return id;
    }

    public int getColor()
    {
        return color;
    }

    public CFGNode getEntry()
    {
        return entries.get(0);
    }

    public ArrayList<CFGNode> getEntries()
    {
        return this.entries;
    }

    public void addEntry(CFGNode entry)
    {
        entries.add(entry);
    }

    public CFGNode getExit()
    {
        return exits.get(0);
    }

    public ArrayList<CFGNode> getExits()
    {
        return this.exits;
    }

    public void addExit(CFGNode exit)
    {
        exits.add(exit);
    }

    public String printNodes()
    {
        String str = "Block " + id + "\n{\n";
        str += "If Condition\n\n";
        str += if1.condition.print(0);
        str += "\n}\n\n";

        this.color = 1;

        for (CFGNode cfgNode : exits)
        {
            if (cfgNode.getColor() == 0)
                str += cfgNode.printNodes();
        }

        this.color = 2;

        return str;
    }

    public String printEdges()
    {
        String str = "";
        int i = 1;

        this.color = 3;

        for (CFGNode cfgNode : exits)
        {
            if (cfgNode.getColor() == 3)
            {
                str += this.id + " --LOOP--> " + cfgNode.getId() + "\n";
                continue;
            }
            
            else if (cfgNode.getColor() == 4)
            {
                str += this.id + " -> " + cfgNode.getId() + "\n";
                continue;
            }

            if (i == 1)
                str += this.id + " --TRUE--> " + cfgNode.getId() + "\n";

            else
                str += this.id + " --FALSE--> " + cfgNode.getId() + "\n";
            
            str += cfgNode.printEdges();
            ++i;
        }

        this.color = 4;

        return str;
    }
}

class LoopNode extends DecisionNode
{
    public int id;
    public int color;
    public ArrayList<CFGNode> entries;
    public ArrayList<CFGNode> exits;
    public Loop loop;
    
    public LoopNode(int id, Loop loop)
    {
        this.id = id;
        this.color = 0;
        this.loop = loop;
        this.entries = new ArrayList<CFGNode>();
        this.exits = new ArrayList<CFGNode>();
    }

    public int getId()
    {
        return id;
    }
    
    public int getColor()
    {
        return color;
    }

    public CFGNode getEntry()
    {
        return entries.get(0);
    }

    public ArrayList<CFGNode> getEntries()
    {
        return this.entries;
    }

    public void addEntry(CFGNode entry)
    {
        entries.add(entry);
    }

    public CFGNode getExit()
    {
        return exits.get(0);
    }

    public ArrayList<CFGNode> getExits()
    {
        return this.exits;
    }

    public void addExit(CFGNode exit)
    {
        exits.add(exit);
    }

    public String printNodes()
    {
        String str = "Block " + id + "\n{\n";
        str += "Loop Condition\n\n";
        str += loop.condition.print(0);
        str += "\n}\n\n";

        this.color = 1;

        for (CFGNode cfgNode : exits)
        {
            if (cfgNode.getColor() == 0)
                str += cfgNode.printNodes();
        }

        this.color = 2;

        return str;
    }

    public String printEdges()
    {
        String str = "";
        int i = 1;

        this.color = 3;

        for (CFGNode cfgNode : exits)
        {
            if (cfgNode.getColor() == 3)
            {
                str += this.id + " --LOOP--> " + cfgNode.getId() + "\n";
                continue;
            }

            else if (cfgNode.getColor() == 4)
            {
                str += this.id + " -> " + cfgNode.getId() + "\n";
                continue;
            }

            if (i == 1)
                str += this.id + " --TRUE--> " + cfgNode.getId() + "\n";

            else
                str += this.id + " --FALSE--> " + cfgNode.getId() + "\n";
            
            str += cfgNode.printEdges();
            ++i;
        }

        this.color = 4;

        return str;
    }
}

class DummyNode implements CFGNode
{
    public int id;
    public int color;
    public ArrayList<CFGNode> entries;
    public ArrayList<CFGNode> exits;
    
    public DummyNode(int id)
    {
        this.id = id;
        this.color = 0;
        this.entries = new ArrayList<CFGNode>();
        this.exits = new ArrayList<CFGNode>();
    }

    public int getId()
    {
        return id;
    }
    
    public int getColor()
    {
        return color;
    }

    public CFGNode getEntry()
    {
        return entries.get(0);
    }

    public ArrayList<CFGNode> getEntries()
    {
        return this.entries;
    }

    public void addEntry(CFGNode entry)
    {
        entries.add(entry);
    }

    public CFGNode getExit()
    {
        return exits.get(0);
    }

    public ArrayList<CFGNode> getExits()
    {
        return this.exits;
    }

    public void addExit(CFGNode exit)
    {
        exits.add(exit);
    }

    public String printNodes()
    {
        String str = "Block " + id + "\n{\n";
        str += "Dummy Node";
        str += "\n}\n\n";

        this.color = 1;
        
        for (CFGNode cfgNode : exits)
        {
            if (cfgNode.getColor() == 0)
                str += cfgNode.printNodes();
        }

        this.color = 2;

        return str;
    }

    public String printEdges()
    {
        String str = "";
        this.color = 3;

        for (CFGNode cfgNode : exits)
        {
            if (cfgNode.getColor() == 3)
            {
                str += this.id + " --LOOP--> " + cfgNode.getId() + "\n";
                continue;
            }

            else if (cfgNode.getColor() == 4)
            {
                str += this.id + " -> " + cfgNode.getId() + "\n";
                continue;
            }

            str += this.id + " -> " + cfgNode.getId() + "\n";
            str += cfgNode.printEdges();
        }

        this.color = 4;

        return str;
    }
}

class CFG
{
    private static int nodeId = 0;

    public Pair createCFG(ASTNode node)
    {
        if (node instanceof Seq)
        {   
            ArrayList<Stmt> stmts = ((Seq)node).statements;
            
            Pair cfg = createCFG(stmts.get(0));
            CFGNode cfgBegin = cfg.begin;
            CFGNode cfgEnd = cfg.end;

            for (int i = 1; i < stmts.size(); ++i)
            {
                Stmt stmt = stmts.get(i);
                Pair newCFG = createCFG(stmt);

                cfgEnd.addExit(newCFG.begin);
                newCFG.begin.addEntry(cfgEnd);

                cfgEnd = newCFG.end;
            }

            return new Pair(cfgBegin, cfgEnd);
        }

        else if (node instanceof Assignment)
        {
            CFGNode cfgNode = new InstructionNode(nodeId++, (Stmt)node);

            return new Pair(cfgNode, cfgNode);
        }

        else if (node instanceof FuncDef)
        {
            CFGNode cfgNode = new InstructionNode(nodeId++, (Stmt)node);

            return new Pair(cfgNode, cfgNode);
        }

        else if (node instanceof If1)
        {
            CFGNode cfgNode = new IfNode(nodeId++, (If1)node);
         
            Pair trueNode = createCFG(((If1)node).ifStmt);
            Pair falseNode = createCFG(((If1)node).elseStmt);
            
            cfgNode.addExit(trueNode.begin);
            cfgNode.addExit(falseNode.begin);
            
            DummyNode dummyNode = new DummyNode(nodeId++);

            trueNode.end.addExit(dummyNode);
            falseNode.end.addExit(dummyNode);
            dummyNode.addEntry(trueNode.end);
            dummyNode.addEntry(falseNode.end);

            return new Pair(cfgNode, dummyNode);
        }

        else if (node instanceof Loop)
        {
            CFGNode cfgNode = new LoopNode(nodeId++, (Loop)node);
         
            Pair trueNode = createCFG(((Loop)node).stmt);
            DummyNode dummyNode = new DummyNode(nodeId++);

            cfgNode.addExit(trueNode.begin);
            trueNode.begin.addEntry(cfgNode);
            trueNode.end.addExit(cfgNode);
            cfgNode.addEntry(trueNode.end);

            cfgNode.addExit(dummyNode);
            dummyNode.addEntry(cfgNode);

            return new Pair(cfgNode, dummyNode);
        }

        System.out.println("Error: Unknown AST Node type.");
        return null;
    }

    public int getTotalNodes()
    {
        return nodeId;
    }
}
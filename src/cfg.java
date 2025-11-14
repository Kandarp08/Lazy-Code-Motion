import java.util.ArrayList;

interface CFGNode
{
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
    public ArrayList<CFGNode> entries;
    public ArrayList<CFGNode> exits;
    public Stmt stmt;

    public InstructionNode(int id, Stmt stmt)
    {
        this.id = id;
        this.stmt = stmt;
        this.entries = new ArrayList<CFGNode>();
        this.exits = new ArrayList<CFGNode>();
    }

    public int getId()
    {
        return id;
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
}

abstract class DecisionNode implements CFGNode
{

}

class IfNode extends DecisionNode
{
    public int id;
    public ArrayList<CFGNode> entries;
    public ArrayList<CFGNode> exits;
    public If1 if1;

    public IfNode(int id, If1 if1)
    {
        this.id = id;
        this.if1 = if1;
        this.entries = new ArrayList<CFGNode>();
        this.exits = new ArrayList<CFGNode>();
    }

    public int getId()
    {
        return id;
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
}

class LoopNode extends DecisionNode
{
    public int id;
    public ArrayList<CFGNode> entries;
    public ArrayList<CFGNode> exits;
    public Loop loop;
    
    public LoopNode(int id, Loop loop)
    {
        this.id = id;
        this.loop = loop;
        this.entries = new ArrayList<CFGNode>();
        this.exits = new ArrayList<CFGNode>();
    }

    public int getId()
    {
        return id;
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
}

class DummyNode implements CFGNode
{
    public int id;
    public ArrayList<CFGNode> entries;
    public ArrayList<CFGNode> exits;
    
    public DummyNode(int id)
    {
        this.id = id;
        this.entries = new ArrayList<CFGNode>();
        this.exits = new ArrayList<CFGNode>();
    }

    public int getId()
    {
        return id;
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
}

class CFG
{
    private static int nodeId = 1;

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
}
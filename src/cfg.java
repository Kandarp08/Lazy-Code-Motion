import java.util.ArrayList;
import java.util.HashSet;


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


class LCM
{
    CFGNode[] nodes;       //indexed by id.
    int totalNodes;

    HashSet<String>[] use;          //hashset so that we ignore duplicates in a block.
    HashSet<String>[] kill;
    HashSet<String> UniversalSet;  //set of all expressions in the program.

    //pass1 variables
    ArrayList<Integer> ToposortialOrder;
    HashSet<String>[] ANTin;
    HashSet<String>[] ANTout;

    //pass2 variables
    HashSet<String>[] AVin;
    HashSet<String>[] AVout;
    HashSet<String>[] earliest;

    public void pass1(CFGNode node, int totalNodes)
    {
        this.totalNodes = totalNodes;
        ToposortialOrder = new ArrayList<Integer>();
        nodes = new CFGNode[totalNodes];
        boolean visited[] = new boolean[totalNodes];

        //puts the nodes in topological order in ToposortialOrder, initializes the array list of cfg nodes indexed by id.
        findTopoSort(node, visited);
        // printing the topological order
        System.out.println("Topological Order:");
        for(int i = ToposortialOrder.size() - 1; i >= 0; i--)
        {
            System.out.print(ToposortialOrder.get(i) + " ");
        }

        //Defining the use, kill sets.
        use = (HashSet<String>[])new HashSet[totalNodes];
        kill = (HashSet<String>[])new HashSet[totalNodes];
        create_use_kill_sets();
        //printing use and kill sets.
        System.out.println("\nUse and Kill Sets:");
        for(int i=0;i<ToposortialOrder.size();i++)
        {
            int nodeId = i;
            System.out.print("Node " + nodeId + ":\nUse: { ");
            for(String var : use[nodeId])
            {
                System.out.print(var + ", ");
            }
            System.out.print("}\nKill: { ");
            for(String var : kill[nodeId])
            {
                System.out.print(var + ", ");
            }
            System.out.println("}\n");
        }

        UniversalSet = new HashSet<String>();
        for(int i=0;i<ToposortialOrder.size();i++)
        {
            if(use[i] != null)
            {
                UniversalSet.addAll(use[i]);
            }
        }
        System.out.println("Universal Set: " + UniversalSet);
        //Adding to the kill set all the expressions from the universal set which are killed.
        for(int i=0;i<ToposortialOrder.size();i++)
        {
            System.out.println("Before adding, kill set of node " + i + " is: " + kill[i]);
            for(String varKilled : kill[i])
            {
                for(String expr : UniversalSet)
                {
                    if(expr.contains(varKilled))
                    {
                        kill[i].add(expr);
                    }
                }
            }
        }
        System.out.println("\nKill Sets after adding killed expressions:");
        //printing use and kill sets.
        System.out.println("\nUse and Kill Sets:");
        for(int i=0;i<ToposortialOrder.size();i++)
        {
            int nodeId = i;
            System.out.print("Node " + nodeId + ":\nUse: { ");
            for(String var : use[nodeId])
            {
                System.out.print(var + ", ");
            }
            System.out.print("}\nKill: { ");
            for(String var : kill[nodeId])
            {
                System.out.print(var + ", ");
            }
            System.out.println("}\n");
        }
        ANTin = (HashSet<String>[])new HashSet[totalNodes];
        ANTout = (HashSet<String>[])new HashSet[totalNodes];
        for (int i = 0; i < totalNodes; i++)
        {
            ANTin[i] = new HashSet<String>();                   //initialized to empty set.
            ANTout[i] = new HashSet<String>(UniversalSet);      //initialized to universal set.
        }
        // System.out.println("ANTout sets:");
        // for (int i = 0; i < ANTout.length; i++)
        // {
        //     System.out.println("ANTout[" + i + "] = " + ANTout[i]);
        // }

        //Performing the While loop until convergence for Pass1.
        boolean convergence = false;
        while(!convergence)
        {
            boolean nochanges = true;
            boolean[] indicesChanged = new boolean[totalNodes];
            for(int i=ToposortialOrder.size()-1;i>=0;i--)
            {
                int nodeId = ToposortialOrder.get(i);
                // if(nodes[nodeId] instanceof DummyNode)
                //     continue;
                HashSet<String> oldANTin = (HashSet<String>)ANTin[nodeId].clone();
                HashSet<String> oldANTout = (HashSet<String>)ANTout[nodeId].clone();

                
                
                //ANTin[n] = use[n] Union (ANTout[n] - kill[n])
                ANTin[nodeId] = new HashSet<String>(use[nodeId]);
                HashSet<String> tempSet = new HashSet<String>(ANTout[nodeId]);
                tempSet.removeAll(kill[nodeId]);
                ANTin[nodeId].addAll(tempSet);

                //ANTout[n] = intersection of ANTin[s] for all successors s of n.
                ANTout[nodeId] = new HashSet<String>(UniversalSet);
                ArrayList<CFGNode> exits = nodes[nodeId].getExits();
                for(CFGNode succ : exits)
                {
                    ANTout[nodeId].retainAll(ANTin[succ.getId()]);
                }

                //check for convergence
                if(!oldANTin.equals(ANTin[nodeId]) || !oldANTout.equals(ANTout[nodeId]))
                {
                    // convergence = false;
                    nochanges = false;
                    indicesChanged[nodeId] = true;
                }
            }
            System.out.println("\n\nAfter iteration:");
            for(int i=0;i<totalNodes;i++)
            {
                if(indicesChanged[i])
                {
                    System.out.println(i);
                    // System.out.println("Node " + i + " changed:");
                    // System.out.println("ANTin[" + i + "] = " + ANTin[i]);
                    // System.out.println("ANTout[" + i + "] = " + ANTout[i]);
                }
            }
            if(nochanges)
            {
                convergence = true;
            }
            // convergence = true;
        }
        
        System.out.println("\nFinal ANTin and ANTout sets:");
        for(int i=0;i<ToposortialOrder.size();i++)
        {
            int nodeId = i;
            System.out.println("Node " + nodeId + ":");
            System.out.println("ANTin[" + nodeId + "] = " + ANTin[nodeId]);
            System.out.println("ANTout[" + nodeId + "] = " + ANTout[nodeId]);
        }

        System.out.println("Pass1 finished");
        pass2();
    }
    public void create_use_kill_sets()
    {
        for(int i=0;i<ToposortialOrder.size();i++)
        {
            int nodeId = ToposortialOrder.get(i);
            CFGNode node = nodes[nodeId];
            use[nodeId] = new HashSet<String>();
            kill[nodeId] = new HashSet<String>();

            //populate use and kill sets for this node.
            if(node instanceof InstructionNode)
            {
                Stmt stmt = ((InstructionNode)node).stmt;
                if(stmt instanceof Assignment)
                {
                    String varAssigned = ((Assignment)stmt).id.id;
                    kill[nodeId].add(varAssigned);

                    //get variables used in the expression.
                    ArrayList<String> varsUsed = new ArrayList<String>();
                    ((Assignment)stmt).expression.getVariablesUsed(varsUsed);

                    for(String var : varsUsed)
                    {
                        if(!kill[nodeId].contains(var))
                        {
                            use[nodeId].add(var);
                        }
                    }
                }
            }
            else if(node instanceof IfNode)
            {
                If1 if1 = ((IfNode)node).if1;
                ArrayList<String> varsUsed = new ArrayList<String>();
                if1.condition.getVariablesUsed(varsUsed);
                for(String var : varsUsed)
                {
                    use[nodeId].add(var);
                }
                // String varUsed = if1.condition.toMathsString();
                // use[nodeId].add(varUsed);
            }
            else if(node instanceof LoopNode)
            {
                Loop loop = ((LoopNode)node).loop;
                ArrayList<String> varsUsed = new ArrayList<String>();
                loop.condition.getVariablesUsed(varsUsed);
                for(String var : varsUsed)
                {
                    use[nodeId].add(var);
                }
                // String varUsed = loop.condition.toMathsString();
                // use[nodeId].add(varUsed);
            }
            else if(node instanceof DummyNode)
            {
                //do nothing.
            }
            else
            {
                System.out.println("Error: Unknown CFG Node type.");
            }
        }
    }
    public void findTopoSort(CFGNode node, boolean visited[])
    {
        if(visited[node.getId()] == true)
            return;
        nodes[node.getId()] = node;
        // nodes.set(node.getId(), node);
        visited[node.getId()] = true;
        for(CFGNode neighbor : node.getExits())
        {
            findTopoSort(neighbor, visited);
        }
        ToposortialOrder.add(node.getId());
    }

    public void pass2()
    {
        AVin = (HashSet<String>[])new HashSet[totalNodes];
        AVout = (HashSet<String>[])new HashSet[totalNodes];
        earliest = (HashSet<String>[])new HashSet[totalNodes];

        //Initialization of the sets.
        //AVout - empty
        //AVin - universal set (or) empty?
        //earliest - empty
        for(int i=0;i<totalNodes;i++)
        {
            AVout[i] = new HashSet<String>();                   //initialized to empty set.
            AVin[i] = new HashSet<String>(UniversalSet);       //initialized to universal set.
            // AVin[i] = new HashSet<String>();                    //initialized to empty set.
            earliest[i] = new HashSet<String>();                //initialized to empty set.
        }
        
        boolean convergence = false;
        while(!convergence)
        {
            boolean nochanges = true;
            boolean[] indicesChanged = new boolean[totalNodes];
            for(int i=0;i<ToposortialOrder.size();i++)
            {
                int nodeId = ToposortialOrder.get(i);
                HashSet<String> oldANTin = (HashSet<String>)ANTin[nodeId].clone();
                HashSet<String> oldANTout = (HashSet<String>)ANTout[nodeId].clone();

                //AVout[n] = (ANTin[n] Union AVIn[n]) - kill[n];
                AVout[nodeId] = new HashSet<String>(ANTin[nodeId]);
                AVout[nodeId].addAll(AVin[nodeId]);
                AVout[nodeId].removeAll(kill[nodeId]);

                //AVin[n] = intersection of AVout[p] for all predecessors p of n.
                AVin[nodeId] = new HashSet<String>(UniversalSet);
                ArrayList<CFGNode> entries = nodes[nodeId].getEntries();
                for(CFGNode pred : entries)
                {
                    AVin[nodeId].retainAll(AVout[pred.getId()]);
                }

                //earliest[n] = ANTin[n] - AVIn[n].
                earliest[nodeId] = new HashSet<String>(ANTin[nodeId]);
                earliest[nodeId].removeAll(AVin[nodeId]);

                //check for convergence
                if(!oldANTin.equals(ANTin[nodeId]) || !oldANTout.equals(ANTout[nodeId]))
                {
                    // convergence = false;
                    nochanges = false;
                    indicesChanged[nodeId] = true;
                }
            }

            System.out.println("\n\nAfter iteration:");
            for(int i=0;i<totalNodes;i++)
            {
                if(indicesChanged[i])
                {
                    System.out.println(i);
                    // System.out.println("Node " + i + " changed:");
                    // System.out.println("ANTin[" + i + "] = " + ANTin[i]);
                    // System.out.println("ANTout[" + i + "] = " + ANTout[i]);
                }
            }
            if(nochanges)
            {
                convergence = true;
            }
        }

        System.out.println("\nFinal AVin, AVout and earliest sets:");
        for(int i=0;i<ToposortialOrder.size();i++)
        {
            int nodeId = i;
            System.out.println("Node " + nodeId + ":");
            System.out.println("AVin[" + nodeId + "] = " + AVin[nodeId]);
            System.out.println("AVout[" + nodeId + "] = " + AVout[nodeId]);
            System.out.println("earliest[" + nodeId + "] = " + earliest[nodeId]);
        }

        utility_print();
    }

    public void utility_print()
    {
        int n = use.length;

    // Max width for each cell
    final int WIDTH = 25;

    System.out.println("===============================================================================================================================");
    System.out.printf("%-8s | %-25s | %-25s | %-25s | %-25s | %-25s | %-25s | %-25s%n",
            "BlockID", "use", "kill", "ANTin", "ANTout", "AVin", "AVout", "earliest");
    System.out.println("===============================================================================================================================");

    for (int i = 0; i < n; i++) {
        System.out.printf(
                "%-8d | %-25s | %-25s | %-25s | %-25s | %-25s | %-25s | %-25s%n",
                i,
                fit(use[i], WIDTH),
                fit(kill[i], WIDTH),
                fit(ANTin[i], WIDTH),
                fit(ANTout[i], WIDTH),
                fit(AVin[i], WIDTH),
                fit(AVout[i], WIDTH),
                fit(earliest[i], WIDTH)
        );
    }

        System.out.println("========================================================================================================================");

        }
    private static String fit(HashSet<String> set, int maxWidth) {
        if (set == null)
            return "null";

        String s = set.toString();

        if (s.length() <= maxWidth)
            return s;

        // truncate and append "..."
        return s.substring(0, maxWidth - 3) + "...";
    }
}
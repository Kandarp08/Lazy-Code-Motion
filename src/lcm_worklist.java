import java.util.ArrayList;
import java.util.HashSet;
import java.util.HashMap;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class lcm_worklist
{
    private CFGNode rootNode;
    private CFGNode[] nodes;       //indexed by id.
    private int totalNodes;
    private ArrayList<Integer> ToposortialOrder;

    private HashSet<String>[] use;          //hashset so that we ignore duplicates in a block.
    private HashSet<String>[] kill;
    private HashSet<String> UniversalSet;   //set of all expressions in the program.    

    //pass1 variables
    HashSet<String>[] ANTin;
    HashSet<String>[] ANTout;

    //pass2 variables
    HashSet<String>[] AVin;
    HashSet<String>[] AVout;
    HashSet<String>[] earliest;

    //pass3 variables
    HashSet<String>[] POSTin;
    HashSet<String>[] POSTout;
    HashSet<String>[] latest;

    //pass4 variables
    HashSet<String>[] Usedin;
    HashSet<String>[] Usedout;

    //code rewrite variables
    private HashMap<String,String> tempVarMap; //maps expression to temp variable name.
    private int newNodeID;

    public void init(CFGNode node, int totalNodes)
    {
        //basic initializations.
        this.rootNode = node;
        this.totalNodes = totalNodes;
        ToposortialOrder = new ArrayList<Integer>();
        nodes = new CFGNode[totalNodes];
        use = (HashSet<String>[])new HashSet[totalNodes];
        kill = (HashSet<String>[])new HashSet[totalNodes];
        UniversalSet = new HashSet<String>();
        
        //puts the nodes in topological order in ToposortialOrder, initializes the array list of cfg nodes indexed by id.
        boolean visited[] = new boolean[totalNodes];
        findTopoSort(node, visited);

        // printing the topological order
        System.out.println("Topological Order:");
        for(int i = ToposortialOrder.size() - 1; i >= 0; i--)
        {
            System.out.print(ToposortialOrder.get(i) + " ");
        }

        //creating use and kill sets for each node and then printing them.
        create_use_kill_sets();
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

        //Creating the universal set.
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

        //printing use and kill sets again. Kill sets have been updated now to include from universal set.
        System.out.println("\nKill Sets after adding killed expressions:");
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
        visited[node.getId()] = true;
        for(CFGNode neighbor : node.getExits())
        {
            findTopoSort(neighbor, visited);
        }
        ToposortialOrder.add(node.getId());
    }

    public void code_rewrite()
    {
        tempVarMap = new HashMap<String,String>();
        newNodeID = totalNodes;

        //Re-writing all the expression e in latest[n] Intersection UsedOut[n] with temp variables.
        for(int i=0;i<totalNodes;i++)
        {
            //latest[i] Intersection UsedOut[i]
            HashSet<String> expressionsToReplace = new HashSet<String>(latest[i]);
            expressionsToReplace.retainAll(Usedout[i]);
            // if(expressionsToReplace.isEmpty())
            // {
            //     continue;
            // }
            
            //Creating new temp variable for expressions which don't have one.
            for(String exprStr: expressionsToReplace)
            {
                if(!tempVarMap.containsKey(exprStr))
                {
                    String tempVarName = "t" + tempVarMap.size();
                    tempVarMap.put(exprStr, tempVarName);
                }
            }
            
            //Replacing all the expressions in the statements of the CFGNode with the temp variables if they exist.
            CFGNode node = nodes[i];
            if(node instanceof InstructionNode)
            {
                Stmt stmt = ((InstructionNode)node).stmt;
                if(stmt instanceof Assignment)
                {
                    //Get the expression string.
                    String exprStr = ((Assignment)stmt).expression.toMathsString();
                    Expression expr = ((Assignment)stmt).expression;
                    // System.out.println("In Node " + i + ", found expression to replace: " + exprStr + "has temp variable: " + tempVarMap.get(exprStr));


                    if(tempVarMap.get(exprStr) == null)
                    {
                        continue;   //no replacement needed.
                    }
                    //Replace with temp variable.
                    String tempVarName = tempVarMap.get(exprStr);
                    Id tempId = new Id(tempVarName);
                    ((Assignment)stmt).expression = tempId;

                    System.out.println("In Node " + i + ", replaced expression " + exprStr + " with temp variable " + tempVarName);
                    if(expressionsToReplace.contains(exprStr))
                    {
                        //creating computation of temp variable statement.
                        Assignment tempAssignStmt = new Assignment(tempId,expr);
                        Assignment originalAssignStmt = (Assignment)stmt;

                        //Creating a new Seq statement with tempAssignStmt followed by originalAssignStmt.
                        Seq seq = new Seq(new ArrayList<Stmt>());
                        seq.statements.add(tempAssignStmt);
                        seq.statements.add(originalAssignStmt);
                        ((InstructionNode)node).stmt = seq;

                    }
                }
            }
            else if(node instanceof IfNode)
            {

            }
            else if(node instanceof LoopNode)
            {
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

        //Setting 'color' of all the CFGNodes to '0'.
        reset_colors();

        // System.out.println("rootNode and nodes[0] are same or not" + (rootNode == nodes[0]));
        // System.out.println("rootNode.getExits[0]==nodes[1]" + (rootNode.getExits().get(0) == nodes[1]));
        // System.out.println("rootNode.getExits[0].getExits[0]==nodes[2]" + (rootNode.getExits().get(0).getExits().get(0) == nodes[2]));
        // System.out.println("rootNode.getExits[0].getExits[0].getExits[0]==nodes[3]" + (rootNode.getExits().get(0).getExits().get(0).getExits().get(0) == nodes[3]));
        // System.out.println("rootNode.getExits[0].getExits[0].getExits[1]==nodes[4]" + (rootNode.getExits().get(0).getExits().get(0).getExits().get(1) == nodes[4]));
        // System.out.println("rootNode.getExits[0].getExits[0].getExits[0].getExits[0]==nodes[5]" + (rootNode.getExits().get(0).getExits().get(0).getExits().get(0).getExits().get(0) == nodes[5]));
        // System.out.println("rootNode.getExits[0].getExits[0].getExits[1].getExits[0]==nodes[5]" + (rootNode.getExits().get(0).getExits().get(0).getExits().get(1).getExits().get(0) == nodes[5]));
        // System.out.println("rootNode.getExits[0].getExits[0].getExits[1].getExits[0]==nodes[5]" + (rootNode.getExits().get(0).getExits().get(0).getExits().get(1).getExits().get(0) == nodes[5]));
        // for(int i=0;i<totalNodes;i++)
        // {
        //     if(nodes[i] instanceof InstructionNode)
        //     {
        //         //check to see if the stmt variable of nodes[i] is an instance of Assignment and if it is the case, print the stmt.expression.toMathsString().
        //         InstructionNode instrNode = (InstructionNode)nodes[i];
        //         if(instrNode.stmt instanceof Assignment)
        //         {
        //             Assignment assignStmt = (Assignment)instrNode.stmt;
        //             System.out.println("Printing finally iterative, Node " + i + " Assignment expression: " + assignStmt.expression.toMathsString());
        //         }
        //     }
        // }
        System.out.println("Code rewriting finished. New CFG:");
        System.out.println(rootNode.printNodes());
        System.out.println(rootNode.printEdges());
        System.out.println("End of new CFG");

        reset_colors();

        try
        {
            Files.deleteIfExists(Paths.get("output.txt"));
               
            BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt", true));
            
            bw.write("begin");
            bw.newLine();
            bw.newLine();
            
            convert_to_code(rootNode, 0, bw);

            bw.newLine();
            bw.write("end");
            bw.newLine();

            bw.close();
        }

        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
    public void reset_colors()
    {
        for(int i=0;i<totalNodes;i++)
        {
            CFGNode node = nodes[i];
            if(node instanceof InstructionNode)
            {
                InstructionNode instrNode = (InstructionNode)node;
                instrNode.color = 0;
            }
            else if(node instanceof IfNode)
            {
                IfNode ifNode = (IfNode)node;
                ifNode.color = 0;
            }
            else if(node instanceof LoopNode)
            {
                LoopNode loopNode = (LoopNode)node;
                loopNode.color = 0;
            }
            else if(node instanceof DummyNode)
            {
                DummyNode dummyNode = (DummyNode)node;
                dummyNode.color = 0;
            }
            else
            {
                System.out.println("Error: Unknown CFG Node type.");
            }
        }
    }

    private String csvEscape(HashSet<String> set) {
        if (set == null || set.isEmpty())
            return "\"{}\"";
        
        String text = set.toString();
        // convert set → string like {a, b, c}
        // CSV special-character rule:
        // If data contains comma or quote, wrap inside quotes
        if (text.contains(",") || text.contains("\"")) {
            text = text.replace("\"", "\"\"");  //escape quotes
            text = text.replace(",", ", \n");   //comment this line to view the entire set in one line.
            return "\"" + text + "\"";
        }

        return text;
    }
    public void writeDataflowToCSV(String filename) 
    {
        try (FileWriter writer = new FileWriter(filename)) {

            // header row
            writer.write("BlockID,use,kill,ANTin,ANTout,AVin,AVout,earliest,POSTin,POSTout,latest,Usedin,Usedout\n");
            int total = use.length;

            for (int i = 0; i < total; i++) {
                writer.write(i + ",");
                writer.write(csvEscape(use[i]) + ",");
                writer.write(csvEscape(kill[i]) + ",");
                writer.write(csvEscape(ANTin[i]) + ",");
                writer.write(csvEscape(ANTout[i]) + ",");
                writer.write(csvEscape(AVin[i]) + ",");
                writer.write(csvEscape(AVout[i]) + ",");
                writer.write(csvEscape(earliest[i]) + ",");
                writer.write(csvEscape(POSTin[i]) + ",");
                writer.write(csvEscape(POSTout[i]) + ",");
                writer.write(csvEscape(latest[i]) + ",");
                writer.write(csvEscape(Usedin[i]) + ",");
                writer.write(csvEscape(Usedout[i]) + "\n");
            }

            System.out.println("CSV written to: " + filename);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void convert_to_code(CFGNode node, int indent, BufferedWriter bw)
    {
        if (node.getColor() == 3)
            return;

        String spaceString = "";

        for (int i = 1; i <= indent; ++i)
            spaceString += "\t";

        try
        {
            if (node instanceof InstructionNode)
            {
                InstructionNode instructionNode = (InstructionNode)node;
                Stmt stmt = instructionNode.stmt;

                if (stmt instanceof Assignment)
                {
                    Assignment assignment = (Assignment)stmt;

                    bw.write(spaceString);
                    bw.write(assignment.toString());
                    bw.newLine();
                }

                else if (stmt instanceof Seq)
                {
                    Seq seq = (Seq)stmt;
                    ArrayList<Stmt> statements = seq.statements;

                    Assignment assign1 = (Assignment)statements.get(0);
                    Assignment assign2 = (Assignment)statements.get(1);

                    bw.write(spaceString);
                    bw.write(assign1.toString());
                    bw.newLine();

                    bw.write(spaceString);
                    bw.write(assign2.toString());
                    bw.newLine();
                }

                ArrayList<CFGNode> exits = node.getExits();

                if (exits.size() > 0)
                    convert_to_code(exits.get(0), indent, bw);
            }

            else if (node instanceof DecisionNode)
            {
                DecisionNode decisionNode = (DecisionNode)node;

                if (decisionNode instanceof IfNode)
                {
                    IfNode ifNode = (IfNode)decisionNode;
                    Expression cond = ifNode.if1.condition;
                    CFGNode ifCFG = ifNode.getExits().get(0);
                    CFGNode elseCFG = ifNode.getExits().get(1);

                    bw.newLine();
                    bw.write(spaceString);
                    bw.write("if (" + cond.toString() + ") then");
                    bw.newLine();

                    convert_to_code(ifCFG, indent + 1, bw);

                    bw.newLine();
                    bw.write(spaceString);
                    bw.write("else");
                    bw.newLine();

                    convert_to_code(elseCFG, indent + 1, bw);
                }

                else if (decisionNode instanceof LoopNode)
                {
                    LoopNode loopNode = (LoopNode)decisionNode;
                    Expression cond = loopNode.loop.condition;

                    loopNode.color = 3;

                    bw.newLine();
                    bw.write(spaceString);
                    bw.write("while (" + cond.toString() + ") do");
                    bw.newLine();

                    ArrayList<CFGNode> exits = loopNode.getExits();

                    convert_to_code(exits.get(0), indent + 1, bw);
                    bw.write(spaceString);
                    bw.write("done;");
                    bw.newLine();

                    convert_to_code(exits.get(1), indent, bw);
                }
            }

            else if (node instanceof DummyNode)
            {
                ArrayList<CFGNode> exits = node.getExits();

                if (node.getEntries().size() == 2)
                {
                    DummyNode dummyNode = (DummyNode)node;
                    
                    if (dummyNode.color == 0)
                        dummyNode.color = 1;

                    else
                    {
                        spaceString = "";

                        for (int i = 1; i < indent; ++i)
                            spaceString += "\t";

                        bw.newLine();
                        bw.write(spaceString);
                        bw.write("endif;");
                        bw.newLine();

                        convert_to_code(exits.get(0), indent - 1, bw);
                    }
                }

                else if (exits.size() > 0)
                    convert_to_code(exits.get(0), indent - 1, bw);
            }
        }

        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    public void apply_lcm()
    {
        // PASS 1
        ANTin = (HashSet<String>[])new HashSet[totalNodes];
        ANTout = (HashSet<String>[])new HashSet[totalNodes];
        Pass pass1 = new Pass1(nodes, ANTin, ANTout, use, kill, UniversalSet);
        Worklist worklist1 = new Worklist(pass1, 1);

        worklist1.worklist_algorithm(nodes);
        ANTin = pass1.getIn();
        ANTout = pass1.getOut();

        // PASS 2
        AVin = (HashSet<String>[])new HashSet[totalNodes];
        AVout = (HashSet<String>[])new HashSet[totalNodes];
        Pass pass2 = new Pass2(nodes, AVin, AVout, use, kill, UniversalSet, ANTin);
        Worklist worklist2 = new Worklist(pass2, 0);

        worklist2.worklist_algorithm(nodes);
        AVin = pass2.getIn();
        AVout = pass2.getOut();    

        earliest = (HashSet<String>[])new HashSet[totalNodes];

        // Compute earliest
        for (int i = 0; i < nodes.length; ++i)
        {
            earliest[i] = new HashSet<String>(ANTin[i]);
            earliest[i].removeAll(AVin[i]);
        }

        // PASS 3
        POSTin = (HashSet<String>[])new HashSet[totalNodes];
        POSTout = (HashSet<String>[])new HashSet[totalNodes];
        Pass pass3 = new Pass3(nodes, POSTin, POSTout, use, kill, UniversalSet, earliest);
        Worklist worklist3 = new Worklist(pass3, 0);

        worklist3.worklist_algorithm(nodes);
        POSTin = pass3.getIn();
        POSTout = pass3.getOut();    

        latest = (HashSet<String>[])new HashSet[totalNodes];

        // Compute latest
        for (int i = 0; i < nodes.length; ++i)
        {    
            HashSet<String> S_succ = new HashSet<String>(UniversalSet);
            ArrayList<CFGNode> exits = nodes[i].getExits();

            for (CFGNode succ : exits)
            {
                HashSet<String> temp = new HashSet<String>(earliest[succ.getId()]);
                temp.addAll(POSTin[succ.getId()]);
                S_succ.retainAll(temp);
            }

            // latest[n] = (earliest[n] Union POSTin[n]) intersection (use[n] Union ~S_succ);
            latest[i] = new HashSet<String>(earliest[i]);
            latest[i].addAll(POSTin[i]);

            HashSet<String> s2 = new HashSet<String>(use[i]);
                
            // Calculate complement of S_succ
            HashSet<String> S_succ_comp = new HashSet<String>(UniversalSet);
            S_succ_comp.removeAll(S_succ);

            s2.addAll(S_succ_comp);

            latest[i].retainAll(s2);
        }

        // PASS 4
        Usedin = (HashSet<String>[])new HashSet[totalNodes];
        Usedout = (HashSet<String>[])new HashSet[totalNodes];
        Pass pass4 = new Pass4(nodes, Usedin, Usedout, use, kill, UniversalSet, latest);
        Worklist worklist4 = new Worklist(pass4, 1);

        worklist4.worklist_algorithm(nodes);
        Usedin = pass4.getIn();
        Usedout = pass4.getOut();    

        for (int i = 0; i < nodes.length; ++i)
        {
            System.out.println("\n" + i);
            System.out.println("Latest: " + latest[i]);
            System.out.println("Usedout: " + Usedout[i]);
        }

        code_rewrite();
    }
}

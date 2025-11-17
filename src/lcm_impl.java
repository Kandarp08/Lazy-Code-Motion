import java.util.ArrayList;
import java.util.HashSet;

import java.io.FileWriter;
import java.io.IOException;

class lcm_impl
{
    CFGNode[] nodes;       //indexed by id.
    int totalNodes;

    HashSet<String>[] use;          //hashset so that we ignore duplicates in a block.
    HashSet<String>[] kill;
    HashSet<String> UniversalSet;   //set of all expressions in the program.

    //pass1 variables
    ArrayList<Integer> ToposortialOrder;
    HashSet<String>[] ANTin;
    HashSet<String>[] ANTout;

    //pass2 variables
    HashSet<String>[] AVin;
    HashSet<String>[] AVout;
    HashSet<String>[] earliest;

    // pass3 variables
    HashSet<String>[] POSTin;
    HashSet<String>[] POSTout;
    HashSet<String>[] latest;

    // pass4 variables
    HashSet<String>[] Usedin;
    HashSet<String>[] Usedout;

    public void pass1(CFGNode node, int totalNodes)
    {
        //basic initializations.
        this.totalNodes = totalNodes;
        ToposortialOrder = new ArrayList<Integer>();
        nodes = new CFGNode[totalNodes];
        use = (HashSet<String>[])new HashSet[totalNodes];
        kill = (HashSet<String>[])new HashSet[totalNodes];
        UniversalSet = new HashSet<String>();
        ANTin = (HashSet<String>[])new HashSet[totalNodes];
        ANTout = (HashSet<String>[])new HashSet[totalNodes];
        
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
        
        //Initialization of ANTin and ANTout sets.
        for (int i = 0; i < totalNodes; i++)
        {
            ANTin[i] = new HashSet<String>();                   //initialized to empty set.
            ANTout[i] = new HashSet<String>(UniversalSet);      //initialized to universal set.
        }

        //Performing the While loop until convergence for Pass1.
        boolean convergence = false;
        while(!convergence)
        {
            boolean nochanges = true;
            boolean[] indicesChanged = new boolean[totalNodes];
            for(int i=ToposortialOrder.size()-1;i>=0;i--)
            {
                int nodeId = ToposortialOrder.get(i);
                
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
                if (exits.size() == 0)
                    ANTout[nodeId] = new HashSet<String>();
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
            System.out.println("\n\nPass1, After iteration:");
            for(int i=0;i<totalNodes;i++)
            {
                if(indicesChanged[i])
                {
                    System.out.println(i);
                }
            }
            if(nochanges)
            {
                convergence = true;
            }
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

    public void pass2()
    {
        AVin = (HashSet<String>[])new HashSet[totalNodes];
        AVout = (HashSet<String>[])new HashSet[totalNodes];
        earliest = (HashSet<String>[])new HashSet[totalNodes];

        //Initialization of the sets.
        //AVout - empty
        //AVin - universal set
        //earliest - empty
        for(int i=0;i<totalNodes;i++)
        {
            AVout[i] = new HashSet<String>();                       //initialized to empty set.
            AVin[i] = new HashSet<String>(UniversalSet);            //initialized to universal set.
            earliest[i] = new HashSet<String>();                    //initialized to empty set.
        }
        
        boolean convergence = false;
        while(!convergence)
        {
            boolean nochanges = true;
            boolean[] indicesChanged = new boolean[totalNodes];
            for(int i=0;i<ToposortialOrder.size();i++)
            {
                int nodeId = ToposortialOrder.get(i);
                HashSet<String> oldAVin = (HashSet<String>)AVin[nodeId].clone();
                HashSet<String> oldAVout = (HashSet<String>)AVout[nodeId].clone();

                //AVout[n] = (ANTin[n] Union AVIn[n]) - kill[n];
                AVout[nodeId] = new HashSet<String>(ANTin[nodeId]);
                AVout[nodeId].addAll(AVin[nodeId]);
                AVout[nodeId].removeAll(kill[nodeId]);

                //AVin[n] = intersection of AVout[p] for all predecessors p of n.
                AVin[nodeId] = new HashSet<String>(UniversalSet);
                ArrayList<CFGNode> entries = nodes[nodeId].getEntries();
                if (entries.size() == 0)
                    AVin[nodeId] = new HashSet<String>();
                for(CFGNode pred : entries)
                {
                    AVin[nodeId].retainAll(AVout[pred.getId()]);
                }

                //check for convergence
                if(!oldAVin.equals(AVin[nodeId]) || !oldAVout.equals(AVout[nodeId]))
                {
                    nochanges = false;
                    indicesChanged[nodeId] = true;
                }
            }

            System.out.println("\n\nPass2, After iteration:");
            for(int i=0;i<totalNodes;i++)
            {
                if(indicesChanged[i])
                {
                    System.out.println(i);
                }
            }
            if(nochanges)
            {
                convergence = true;
            }
        }

        for (int i = 0; i < ToposortialOrder.size(); i++)
        {
            int nodeId = ToposortialOrder.get(i);

            //earliest[n] = ANTin[n] - AVIn[n].
            earliest[nodeId] = new HashSet<String>(ANTin[nodeId]);
            earliest[nodeId].removeAll(AVin[nodeId]);
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

        System.out.println("Pass2 finished");
        pass3();
    }

    public void pass3()
    {
        POSTin = (HashSet<String>[])new HashSet[totalNodes];
        POSTout = (HashSet<String>[])new HashSet[totalNodes];
        latest = (HashSet<String>[])new HashSet[totalNodes];

        //Initialization of the sets.
        //POSTout - empty
        //POSTin - universal set
        //latest - empty
        for(int i=0;i<totalNodes;i++)
        {
            POSTout[i] = new HashSet<String>();                   //initialized to empty set.
            POSTin[i] = new HashSet<String>(UniversalSet);       //initialized to universal set.
            latest[i] = new HashSet<String>();                //initialized to empty set.
        }
        
        boolean convergence = false;
        while(!convergence)
        {
            boolean nochanges = true;
            boolean[] indicesChanged = new boolean[totalNodes];
            for(int i=0;i<ToposortialOrder.size();i++)
            {
                int nodeId = ToposortialOrder.get(i);
                HashSet<String> oldPOSTin = (HashSet<String>)POSTin[nodeId].clone();
                HashSet<String> oldPOSTout = (HashSet<String>)POSTout[nodeId].clone();

                // POSTout[n] = (earliest[n] Union POSTin[n]) - use[n];
                POSTout[nodeId] = new HashSet<String>(earliest[nodeId]);
                POSTout[nodeId].addAll(POSTin[nodeId]);
                POSTout[nodeId].removeAll(use[nodeId]);

                // POSTin[n] = intersection of POSTout[p] for all predecessors p of n.
                POSTin[nodeId] = new HashSet<String>(UniversalSet);
                ArrayList<CFGNode> entries = nodes[nodeId].getEntries();
                for(CFGNode pred : entries)
                {
                    POSTin[nodeId].retainAll(POSTout[pred.getId()]);
                }

                //check for convergence
                if(!oldPOSTin.equals(POSTin[nodeId]) || !oldPOSTout.equals(POSTout[nodeId]))
                {
                    nochanges = false;
                    indicesChanged[nodeId] = true;
                }
            }

            System.out.println("\n\nPass3, After iteration:");
            for(int i=0;i<totalNodes;i++)
            {
                if(indicesChanged[i])
                {
                    System.out.println(i);
                }
            }
            if(nochanges)
            {
                convergence = true;
            }
        }

        for (int i = ToposortialOrder.size() - 1; i >= 0; i--)
        {
            int nodeId = ToposortialOrder.get(i);    
            HashSet<String> S_succ = new HashSet<String>(UniversalSet);
            ArrayList<CFGNode> exits = nodes[nodeId].getExits();

            for (CFGNode succ : exits)
            {
                HashSet<String> temp = new HashSet<String>(earliest[succ.getId()]);
                temp.addAll(POSTin[succ.getId()]);
                S_succ.retainAll(temp);
            }

            // latest[n] = (earliest[n] Union POSTin[n]) intersection (use[n] Union ~S_succ);
            latest[nodeId] = new HashSet<String>(earliest[nodeId]);
            latest[nodeId].addAll(POSTin[nodeId]);

            HashSet<String> s2 = new HashSet<String>(use[nodeId]);
                
            // Calculate complement of S_succ
            HashSet<String> S_succ_comp = new HashSet<String>(UniversalSet);
            S_succ_comp.removeAll(S_succ);

            s2.addAll(S_succ_comp);

            latest[nodeId].retainAll(s2);
        }
        

        System.out.println("\nFinal POSTin, POSTout and latest sets:");
        for(int i=0;i<ToposortialOrder.size();i++)
        {
            int nodeId = i;
            System.out.println("Node " + nodeId + ":");
            System.out.println("POSTin[" + nodeId + "] = " + POSTin[nodeId]);
            System.out.println("POSTout[" + nodeId + "] = " + POSTout[nodeId]);
            System.out.println("latest[" + nodeId + "] = " + latest[nodeId]);
        }

        System.out.println("Pass3 finished");
        pass4();
    }

    public void pass4()
    {
        Usedin = (HashSet<String>[])new HashSet[totalNodes];
        Usedout = (HashSet<String>[])new HashSet[totalNodes];

        //Initialization of the sets.
        //Usedout - universal set
        //Usedin - empty
        for(int i=0;i<totalNodes;i++)
        {
            Usedout[i] = new HashSet<String>(UniversalSet);                     //initialized to universal set.
            Usedin[i] = new HashSet<String>();                                  //initialized to empty set.
        }
        
        boolean convergence = false;
        while(!convergence)
        {
            boolean nochanges = true;
            boolean[] indicesChanged = new boolean[totalNodes];
            for(int i= ToposortialOrder.size() - 1;i >= 0; i--)
            {
                int nodeId = ToposortialOrder.get(i);
                HashSet<String> oldUsedin = (HashSet<String>)Usedin[nodeId].clone();
                HashSet<String> oldUsedout = (HashSet<String>)Usedout[nodeId].clone();

                // Usedin[n] = (use[n] Union Usedout[n]) - latest[n];
                Usedin[nodeId] = new HashSet<String>(use[nodeId]);
                Usedin[nodeId].addAll(Usedout[nodeId]);
                Usedin[nodeId].removeAll(latest[nodeId]);

                // Usedout[n] = intersection of Usedin[s] over all successors s
                Usedout[nodeId] = new HashSet<String>(UniversalSet);
                ArrayList<CFGNode> exits = nodes[nodeId].getExits();
                for (CFGNode succ : exits)
                {
                    Usedout[nodeId].retainAll(Usedin[succ.getId()]);
                }

                //check for convergence
                if(!oldUsedin.equals(Usedin[nodeId]) || !oldUsedout.equals(Usedout[nodeId]))
                {
                    nochanges = false;
                    indicesChanged[nodeId] = true;
                }
            }

            System.out.println("\n\nPass4, After iteration:");
            for(int i=0;i<totalNodes;i++)
            {
                if(indicesChanged[i])
                {
                    System.out.println(i);
                }
            }
            if(nochanges)
            {
                convergence = true;
            }
        }

        System.out.println("\nFinal Usedin, Usedout:");
        for(int i=0;i<ToposortialOrder.size();i++)
        {
            int nodeId = i;
            System.out.println("Node " + nodeId + ":");
            System.out.println("Usedin[" + nodeId + "] = " + Usedin[nodeId]);
            System.out.println("Usedout[" + nodeId + "] = " + Usedout[nodeId]);
        }

        System.out.println("Pass4 finished");
        try {
                writeDataflowToCSV("dataflow_results.csv");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Dataflow analysis complete. Results written to dataflow_results.csv");


        System.out.println("Now proceeding to code instrumentation and changes.");
        code_rewrite();
    }

    public void code_rewrite()
    {
        
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
}
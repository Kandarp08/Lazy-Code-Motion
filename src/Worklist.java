import java.io.FileWriter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.io.BufferedWriter;
// import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

abstract class Pass
{
    protected CFGNode[] nodes;
    protected HashSet<String>[] in;
    protected HashSet<String>[] out;
    protected HashSet<String>[] use;          
    protected HashSet<String>[] kill;
    protected HashSet<String> UniversalSet;

    public Pass(CFGNode[] nodes, HashSet<String>[] in, HashSet<String>[] out, HashSet<String>[] use, HashSet<String>[] kill, HashSet<String> UniversalSet)
    {
        this.nodes = nodes;
        this.in = in;
        this.out = out;
        this.use = use;
        this.kill = kill;
        this.UniversalSet = UniversalSet;

        init(nodes.length);
    }

    public HashSet<String>[] getIn()
    {
        return this.in;
    }

    public HashSet<String>[] getOut()
    {
        return this.out;
    }

    public abstract void init(int totalNodes);
    public abstract void computeIn(int nodeId);
    public abstract void computeOut(int nodeId);
}

class Pass1 extends Pass
{
    public Pass1(CFGNode[] nodes, HashSet<String>[] in, HashSet<String>[] out, HashSet<String>[] use, HashSet<String>[] kill, HashSet<String> UniversalSet)
    {
        super(nodes, in, out, use, kill, UniversalSet);
    }

    public void init(int totalNodes)
    {
        //Initialization of ANTin and ANTout sets.
        for (int i = 0; i < totalNodes; i++)
        {
            in[i] = new HashSet<String>();                   
            out[i] = new HashSet<String>(UniversalSet);      
        }
    }

    public void computeIn(int nodeId)
    {
        //ANTin[n] = use[n] Union (ANTout[n] - kill[n])
        in[nodeId] = new HashSet<String>(use[nodeId]);
        HashSet<String> tempSet = new HashSet<String>(out[nodeId]);
        tempSet.removeAll(kill[nodeId]);
        in[nodeId].addAll(tempSet);
    }

    public void computeOut(int nodeId)
    {
        //ANTout[n] = intersection of ANTin[s] for all successors s of n.
        out[nodeId] = new HashSet<String>(UniversalSet);
        ArrayList<CFGNode> exits = nodes[nodeId].getExits();
        
        if (exits.size() == 0)
            out[nodeId] = new HashSet<String>();
        
        for(CFGNode succ : exits)
        {
            out[nodeId].retainAll(in[succ.getId()]);
        }
    }
}

class Pass2 extends Pass
{
    private HashSet<String>[] ANTin;

    public Pass2(CFGNode[] nodes, HashSet<String>[] in, HashSet<String>[] out, HashSet<String>[] use, HashSet<String>[] kill, HashSet<String> UniversalSet, HashSet<String>[] ANTin)
    {
        super(nodes, in, out, use, kill, UniversalSet);
        this.ANTin = ANTin;
    }

    public void init(int totalNodes)
    {
        //Initialization of the sets.
        for(int i=0;i<totalNodes;i++)
        {
            out[i] = new HashSet<String>();           
            in[i] = new HashSet<String>(UniversalSet);
        }
    }

    public void computeIn(int nodeId)
    {
        //AVin[n] = intersection of AVout[p] for all predecessors p of n.
        in[nodeId] = new HashSet<String>(UniversalSet);
        ArrayList<CFGNode> entries = nodes[nodeId].getEntries();
        
        if (entries.size() == 0)
            in[nodeId] = new HashSet<String>();
        
        for(CFGNode pred : entries)
        {
            in[nodeId].retainAll(out[pred.getId()]);
        }
    }

    public void computeOut(int nodeId)
    {
        //AVout[n] = (ANTin[n] Union AVIn[n]) - kill[n];
        out[nodeId] = new HashSet<String>(in[nodeId]);
        out[nodeId].addAll(ANTin[nodeId]);
        out[nodeId].removeAll(kill[nodeId]);
    }
}

class Pass3 extends Pass
{
    private HashSet<String>[] earliest;

    public Pass3(CFGNode[] nodes, HashSet<String>[] in, HashSet<String>[] out, HashSet<String>[] use, HashSet<String>[] kill, HashSet<String> UniversalSet, HashSet<String>[] earliest)
    {
        super(nodes, in, out, use, kill, UniversalSet);
        this.earliest = earliest;
    }

    public void init(int totalNodes)
    {
        //Initialization of the sets.
        for(int i=0;i<totalNodes;i++)
        {
            out[i] = new HashSet<String>();                  
            in[i] = new HashSet<String>(UniversalSet);       
        }
    }

    public void computeIn(int nodeId)
    {
        // POSTin[n] = intersection of POSTout[p] for all predecessors p of n.
        in[nodeId] = new HashSet<String>(UniversalSet);
        ArrayList<CFGNode> entries = nodes[nodeId].getEntries();
        
        for(CFGNode pred : entries)
        {
            in[nodeId].retainAll(out[pred.getId()]);
        }
    }

    public void computeOut(int nodeId)
    {
        // POSTout[n] = (earliest[n] Union POSTin[n]) - use[n];
        out[nodeId] = new HashSet<String>(earliest[nodeId]);
        out[nodeId].addAll(in[nodeId]);
        out[nodeId].removeAll(use[nodeId]);
    }
}

class Pass4 extends Pass
{
    private HashSet<String>[] latest;

    public Pass4(CFGNode[] nodes, HashSet<String>[] in, HashSet<String>[] out, HashSet<String>[] use, HashSet<String>[] kill, HashSet<String> UniversalSet, HashSet<String>[] latest)
    {
        super(nodes, in, out, use, kill, UniversalSet);
        this.latest = latest;
    }

    public void init(int totalNodes)
    {
        //Initialization of the sets.
        for(int i=0;i<totalNodes;i++)
        {
            out[i] = new HashSet<String>(UniversalSet);                     //initialized to universal set.
            in[i] = new HashSet<String>();                                  //initialized to empty set.
        }
    }

    public void computeIn(int nodeId)
    {
        // Usedin[n] = (use[n] Union Usedout[n]) - latest[n];
        in[nodeId] = new HashSet<String>(use[nodeId]);
        in[nodeId].addAll(out[nodeId]);
        in[nodeId].removeAll(latest[nodeId]);
    }

    public void computeOut(int nodeId)
    {
        // Usedout[n] = intersection of Usedin[s] over all successors s
        out[nodeId] = new HashSet<String>(UniversalSet);
        ArrayList<CFGNode> exits = nodes[nodeId].getExits();
        
        for (CFGNode succ : exits)
        {
            out[nodeId].retainAll(in[succ.getId()]);
        }
    }
}

public class Worklist 
{
    private Pass pass;
    private int dir;
    private FileWriter vals_writer;

    // dir = 0 => forward analysis
    // dir = 1 => backward analysis
    public Worklist(Pass pass, int dir, FileWriter vals_writer)
    {
        this.pass = pass;
        this.dir = dir;
        this.vals_writer = vals_writer;
    }

    private boolean deepEquals(HashSet<String> a, HashSet<String> b) 
    {
        return a.equals(b);
    }

    public void worklist_algorithm(CFGNode[] nodes, String inLabel, String outLabel)
    {
        Deque<CFGNode> worklist = new ArrayDeque<>();

        for (int i = 0; i < nodes.length; ++i)
            worklist.add(nodes[i]);

        List<Integer> visitedOrder = new ArrayList<>();

        // try (FileWriter vals_writer = new FileWriter("lcm_worklist_log.txt")) {
        try {
            while (!worklist.isEmpty())
            {
                CFGNode node = worklist.remove();
                int id = node.getId();

                visitedOrder.add(id);

                // copy only the old sets for this node
                HashSet<String> oldIn  = new HashSet<>(pass.getIn()[id]);
                HashSet<String> oldOut = new HashSet<>(pass.getOut()[id]);

                if (dir == 0)
                {
                    System.out.println("Direction is 0, adding in then out");
                    pass.computeIn(id);
                    vals_writer.write(inLabel + "," + id + "," + pass.getIn()[id] + "\n");
                    pass.computeOut(id);
                    vals_writer.write(outLabel + "," + id + "," + pass.getOut()[id] + "\n");
                }

                else
                {
                    System.out.println("Direction is 1, adding out then in");
                    pass.computeOut(id);
                    vals_writer.write(outLabel + "," + id + "," + pass.getOut()[id] + "\n");
                    pass.computeIn(id);
                    vals_writer.write(inLabel + "," + id + "," + pass.getIn()[id] + "\n");
                }

                HashSet<String> newIn  = pass.getIn()[id];
                HashSet<String> newOut = pass.getOut()[id];

                boolean inChanged  = !deepEquals(newIn, oldIn);
                boolean outChanged = !deepEquals(newOut, oldOut);

                if (inChanged || outChanged)
                {
                    // forward analysis => push succ into worklist
                    if (dir == 0) 
                    {
                        for (CFGNode succ : node.getExits())
                            worklist.add(succ);
                    } 
                    
                    // backward analysis => push pred into worklist
                    else 
                    {
                        for (CFGNode pred : node.getEntries())
                            worklist.add(pred);
                    }
                }
            }
        } catch(Exception e) {
            System.out.println("Error writing to log file.");
            e.printStackTrace();
        }

        System.out.println("Processing order: " + visitedOrder);
    }
}

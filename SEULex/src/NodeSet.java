import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by Jch on 2016/5/12.
 */

public class NodeSet {
    //DFA生成时的，结点集合，及其发出边
    public Set<Vertex> node;
    public int id;//结点结合的下标，即I0、I1等
    public int end;
    public String action;
    //边 与它所指向的集合
    public ArrayList<Map<String,NodeSet>> edges = new ArrayList<Map<String,NodeSet>>();
    public NodeSet() {
        // TODO Auto-generated constructor stub
    }
    public NodeSet(Vertex start) {

        node = new HashSet<Vertex>();
        node.addAll(closure(start));
    }
    public NodeSet(Set<Vertex> s) {
        node = s;
        int high=0;
        for(Iterator<Vertex> it = node.iterator(); it.hasNext(); )
        {
            Vertex vertex = (Vertex)it.next();
            if(vertex.end==1)
            {
                this.end = 1;
                if(high==0)
                {
                    high = vertex.num;
                    this.action = vertex.name;

                }
                else
                {
                    if(vertex.num<high)
                        this.action = vertex.name;
                }
                //break;
            }
        }
    }
    public Set<Vertex> closure(Vertex v)
    {
        Set<Vertex> set= new HashSet<Vertex>();
        set.add(v);
        int last = 0;
        int size = 0;
        Set<Vertex> tmp= new HashSet<Vertex>();
        // closure
        while(true)
        {
            for(Iterator<Vertex> it = set.iterator(); it.hasNext(); )
            {
                Set<Vertex> s = ((Vertex)it.next()).find("");//返回下一条边是空边所连接的点，即求ε闭包
                if(s!=null&&!s.isEmpty()){
                    tmp.addAll(s);//并集
                }
            }
            size = set.size();
            set.addAll(tmp);
            last = set.size();
            if(size ==last)
                break;
			/*if(!set.addAll(tmp))
				break;*/

        }
        return set;
    }
    public boolean equal(NodeSet ns)
    {
        if(ns.end==this.end){
            if(node.equals(ns.node))
                return true;
            else
                return false;
        }
        else
            return false;
    }
    public void union(Set<Vertex> t)
    {

    }

}

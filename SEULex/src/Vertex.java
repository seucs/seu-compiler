import java.util.*;

/**
 * Created by Jch on 2016/5/12.
 */

public class Vertex {
    //存储该结点的发出边和下一结点
    public ArrayList<Map<String, Vertex>> rightEdge = new ArrayList<Map<String, Vertex>>();
    public String name ;//存储语义动作
    public int end=0;//1 表示终结节点
    public int num;//存储编号，唯一
    public Vertex() {
        // TODO Auto-generated constructor stub
    }
    public Set<Vertex> find(String edge)
    {//返回所有下一节点的集合
        Set<Vertex> set = new HashSet<Vertex>();
        Vertex v;
        for(int i=0;i<rightEdge.size();i++)
        {
            if((v=rightEdge.get(i).get(edge))!=null)
            {
                set.add(v);//就是通过边edge与其相连的点的集合
            }
        }
        return set;
    }

}
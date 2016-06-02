import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Created by Jch on 2016/5/12.
 */

public class NFA{
    //存储NFA的3元组
    public Vertex start = new Vertex();//起始点
    public ArrayList<String> edges = new ArrayList<String>();//所有边元素，不包括ε边

    public ArrayList<Vertex> vertices = new ArrayList<Vertex>(); //测试用 ：所有节点

    private int num = 1;

    public NFA(){
        // TODO Auto-generated constructor stub
    }


    //多个NFA通过公共起点，空边连接成一个NFA,终点不合并
    public void create_total_NFA(ArrayList<String> strs,ArrayList<String> actions)
    {
        Map<String, Vertex> next;  //边
        StackNode se; //NFA起点与终点
        //统一起点和终点
        for(int i = 0; i < strs.size(); i++)
        {
            se = create_NFA(strs.get(i),actions.get(i));
            //最后的起点 通过空边 连接 每个NFA的起点
            next = new HashMap<String, Vertex>();
            next.put("",se.start);
            start.rightEdge.add(next);
        }
        vertices.add(start);
    }

    //创建后缀表达式 str 描述的文法规则
    public StackNode create_NFA(String in_str,String action)
    {
        String str = infixToPostfix(in_str);
        //System.out.println(str);
        Map<String, Vertex> next;  //新增边
        Vertex start_vertex;  //新增起点
        Vertex end_vertex;  //新增终点
        StackNode se1; //状态起点与终点
        StackNode se2; //状态起点与终点
        Stack<StackNode> stack = new Stack<StackNode>(); //状态起点与终点记录栈
        int mark = 0;
        if(str.length()==1)
        {
            char ch = str.charAt(0);
            //新增节点
            start_vertex = new Vertex();
            end_vertex = new Vertex();
            //构造  start_vertex 的出边
            next = new HashMap<String, Vertex>();
            next.put(String.valueOf(ch), end_vertex);
            addEdge(String.valueOf(ch));
            start_vertex.rightEdge.add(next);
            //加入栈
            se1 = new StackNode(start_vertex, end_vertex);
            stack.push(se1);
            //测试
            vertices.add(start_vertex);
            vertices.add(end_vertex);
        }
        else
            for(int i = 0; i < str.length(); i++)
            {
                char ch = str.charAt(i);

                switch(ch){

                    case '.':
                        if(mark==1){
                            mark=0;
                            break;
                        }
                        //pop 2个临时状态
                        se1 = stack.pop();
                        se2 = stack.pop();
                        //左操作数se2的终点  通过空边 到达 右边操作数se1的起点
                        next = new HashMap<String, Vertex>();
                        next.put("",se1.start);
                        se2.end.rightEdge.add(next);
                        //新状态的起点与终点
                        se1 = new StackNode(se2.start,se1.end);
                        stack.push(se1);
                        break;

                    case '|':
                        if(mark==1){
                            mark=0;
                            break;
                        }
                        //新增节点
                        start_vertex = new Vertex();
                        end_vertex = new Vertex();
                        //pop 2个临时状态
                        se1 = stack.pop();
                        se2 = stack.pop();
                        //新增起点  通过 空边 到达2个 原起点
                        next = new HashMap<String, Vertex>();
                        next.put("",se1.start);
                        start_vertex.rightEdge.add(next);
                        next = new HashMap<String, Vertex>();
                        next.put("",se2.start);
                        start_vertex.rightEdge.add(next);
                        //2个原终点  通过  空边 到达 新增终点
                        next = new HashMap<String, Vertex>();
                        next.put("", end_vertex);
                        se2.end.rightEdge.add(next);
                        next = new HashMap<String, Vertex>();
                        next.put("", end_vertex);
                        se1.end.rightEdge.add(next);
                        //新状态的起点与终点
                        StackNode now = new StackNode(start_vertex, end_vertex);
                        stack.push(now);
                        vertices.add(start_vertex);
                        vertices.add(end_vertex);
                        break;

                    case '*':
                        if(mark==1){
                            mark=0;
                            break;
                        }
                        //新增节点
                        start_vertex = new Vertex();
                        end_vertex = new Vertex();
                        //pop 1个临时状态
                        se1 = stack.pop();
                        //原终点 通过空边到达  原起点
                        next = new HashMap<String, Vertex>();
                        next.put("",se1.start);
                        se1.end.rightEdge.add(next);
                        //新增起点  通过 空边 到达 原起点
                        next = new HashMap<String, Vertex>();
                        next.put("",se1.start);
                        start_vertex.rightEdge.add(next);
                        //原终点  通过 空边 到达 新终点
                        next = new HashMap<String, Vertex>();
                        next.put("", end_vertex);
                        se1.end.rightEdge.add(next);
                        //新起点  通过 空边 到达 新终点
                        next = new HashMap<String, Vertex>();
                        next.put("", end_vertex);
                        start_vertex.rightEdge.add(next);
                        //新状态的起点与终点
                        se2 = new StackNode(start_vertex, end_vertex);
                        stack.push(se2);
                        vertices.add(start_vertex);
                        vertices.add(end_vertex);
                        break;
                    case '\\':
                        if(mark==1){
                            mark=0;
                            break;
                        }
                        mark = 1;
                        ch = str.charAt(i+1);
                        //System.out.printf("-----------"+ch);
                        start_vertex = new Vertex();
                        end_vertex = new Vertex();
                        //构造  start_vertex 的出边
                        next = new HashMap<String, Vertex>();
                        next.put(String.valueOf(ch), end_vertex);
                        addEdge(String.valueOf(ch));
                        start_vertex.rightEdge.add(next);
                        //加入栈
                        se1 = new StackNode(start_vertex, end_vertex);
                        stack.push(se1);
                        vertices.add(start_vertex);
                        vertices.add(end_vertex);
                        break;
                    default:
                        //新增节点
                        start_vertex = new Vertex();
                        end_vertex = new Vertex();
                        //构造  start_vertex 的出边
                        next = new HashMap<String, Vertex>();
                        next.put(String.valueOf(ch), end_vertex);
                        addEdge(String.valueOf(ch));
                        start_vertex.rightEdge.add(next);
                        //加入栈
                        se1 = new StackNode(start_vertex, end_vertex);
                        stack.push(se1);
                        vertices.add(start_vertex);
                        vertices.add(end_vertex);
                        break;
                }
            }
        //返回单个NFA的 起点 和 终点
        StackNode s = stack.pop();
        s.end.end= 1;
        s.end.num = num++;
        s.end.name = action;
        //System.out.println("end="+s.end.end+ "---"+action);
        //System.out.println("number :"+s.end.num);
        return s;
    }

    public boolean run_NFA(String str, Vertex start){
        //for(int i=0;i<str.length();i++){
        if(!str.isEmpty())
        {
            String s = String.valueOf(str.charAt(0));
            Vertex next;
            //System.out.println(start.rightEdge.size());
            for(int j=0;j<start.rightEdge.size();j++)
            {
                if((next=start.rightEdge.get(j).get(s))!=null)
                {
                    String tmp = new String(str.substring(1));
                    //System.out.println("33"+tmp);
                    if(next.end==1&&tmp.isEmpty())
                    {
                        System.out.println("success");
                        return true;
                    }
                    else
                        return run_NFA(tmp,next);
                }
                else
                {
                    if(str.length()==1&&start.end==1){
                        System.out.println("success");
                        return true;
                    }
                    else
                    {
                        if((next=start.rightEdge.get(j).get(""))!=null)
                        {
                            String tmp = new String(str);
                            //System.out.println("111"+tmp);
                            return run_NFA(tmp,next);
                        }
                        else if((next=start.rightEdge.get(j).get(s))!=null)
                        {
                            String tmp = new String(str.substring(1));
                            //System.out.println("222"+tmp);
                            return run_NFA(tmp,next);
                        }

                    }
                }
            }
        }
        System.out.println("qerror");
        return false;
    }
    //中缀转后缀
    public static String infixToPostfix(String inputString)
    {
        Stack<Character> stack=new Stack<Character>();
        HashMap<Character, Integer> hm=new HashMap<Character, Integer>();
        hm.put('(',new Integer(1));//设置优先级
        hm.put('*',new Integer(2));
        hm.put('.',new Integer(3));
        hm.put('|',new Integer(4));

        char[] input=inputString.toCharArray();
        StringBuffer sbBuffer=new StringBuffer();

        for(int i=0;i<input.length;i++)
        {
            if(stack.empty()&&input[i]=='(')
            {
                stack.push('(');
                continue;
            }
            else if(input[i]==')')
            {
                while(!stack.empty()&&stack.peek()!='(')
                {
                    sbBuffer.append(stack.pop());
                }
                if(input.length!=1)
                    stack.pop();
                else
                    stack.push(')');
                continue;
            }
            else if(input[i]!='('&&input[i]!=')'&&input[i]!='|'&&input[i]!='.'&&input[i]!='*')
            {
                if(input[i]=='\\')
                {
                    sbBuffer.append(input[i]);
                    i++;
                    sbBuffer.append(input[i]);
                }
                else
                {
                    sbBuffer.append(input[i]);
                }
                continue;
            }

            else if(hm.get(input[i])>=1&&hm.get(input[i])<=4)
            {
                while(!stack.empty()&&(hm.get(stack.peek())<=hm.get(input[i]))&&stack.peek()!='(')
                {
                    sbBuffer.append(stack.pop());
                }
                stack.push(input[i]);
                continue;
            }

        }
        while(!stack.empty())
        {
            sbBuffer.append(stack.pop());
        }
        return sbBuffer.toString();

    }

    public void addEdge(String s)
    {
        for(int i=0;i<edges.size();i++)//不可加入重复的边
        {
            if(edges.get(i).equals(s))
                return;
        }
        edges.add(s);
    }
    public Vertex getStart()
    {
        return start;
    }

    public void setStart(Vertex start)
    {
        this.start = start;
    }

}
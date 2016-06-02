import java.util.*;
import java.io.*;

/**
 * Created by Jch on 2016/5/12.
 */

public class DFA {
    //存储DFA并且完成NFA到DFA的操作
    public NodeSet start;
    ArrayList<NodeSet> open = new ArrayList<NodeSet>();
    ArrayList<NodeSet> close = new ArrayList<NodeSet>();
    private String outputFile;
    private ArrayList<String> edges;
    private String input_path;

    public DFA(String in,String out)
    {
        input_path = in;
        outputFile =out;
    }
    public NodeSet create_DFA(NFA nfa)
    {
        Vertex S = nfa.start;
        NodeSet startSet = new NodeSet(S);//开始集合，S的ε闭包
        NodeSet DFAstart= startSet;
        startSet.id = 0;
        edges = nfa.edges;
        open.add(startSet);//所有的结点集合
        close.add(startSet);//所有还未扫过的结点的集合
        int id = 1;
        //if(not.isEmpty())
        //System.out.println(S.num);
        while(!close.isEmpty())
        {//生成还未最小化的DFA
            startSet = close.get(0);
            for(int i=0;i<nfa.edges.size();i++)
            {
                //System.out.print(edges.get(i)+":");
                //show(startSet);
                Set<Vertex> s = nextClosure(startSet,edges.get(i));//找到这条边的终点（集合）
                //System.out.print("=>");
                //show(s);
                if(s!=null&&!s.isEmpty())
                {
                    NodeSet tmp;
                    if((tmp=find(open,s))==null)
                    {
                        //产生了新集合
                        NodeSet node =new NodeSet(s);
                        node.id = id;
                        open.add(node);
                        close.add(node);
                        id++;
                        //连接当前集合和新集合
                        Map<String,NodeSet> m = new HashMap<String, NodeSet>();
                        m.put(edges.get(i),node);
                        startSet.edges.add(m);
                    }
                    else
                    {
                        //虽然是老集合，但是仍要当前集合连接边
                        Map<String,NodeSet> m = new HashMap<String, NodeSet>();
                        m.put(edges.get(i), tmp);
                        startSet.edges.add(m);
                    }
                }
            }
            close.remove(0);//从未查找队列删除，删除列表中指定位置的元素，同时将所有后续元素左移

        }
        start = DFAstart;
        //System.out.println("all:"+all.size());
        return DFAstart;//返回新生成DFA
    }
    public NodeSet find(ArrayList<NodeSet> array,Set<Vertex> s)
    {//结点集合array里是不是已经有了结点集合s，如果没有，返回null
        for(int i=0;i<array.size();i++)
        {
            if(array.get(i).node.equals(s))
                return array.get(i);
        }
        return null;
    }

    public Set<Vertex> nextClosure(NodeSet ns, String edge)
    {//求结点集合ns中每一个结点通过edge边相连的下一个结点的集合的ε闭包
        Set<Vertex> tmp= new HashSet<Vertex>();
        Set<Vertex> set = new HashSet<Vertex>(ns.node);
        int size =0;
        int last =0;
        tmp.clear();
        //show(set);
        for(Iterator<Vertex> it = set.iterator(); it.hasNext(); )
        {
            Set<Vertex> s = ((Vertex)it.next()).find(edge);
            if(s!=null&&!s.isEmpty())
                tmp.addAll(s);
        }
        last = 0;
        size = 0;
        Set<Vertex> tmp2= new HashSet<Vertex>();
        // closure
        while(true){

            for(Iterator<Vertex> it = tmp.iterator(); it.hasNext(); )
            {
                Set<Vertex> s = ((Vertex)it.next()).find("");//ε闭包
                if(s!=null&&!s.isEmpty()){
                    tmp2.addAll(s);
                }
            }
            size = tmp.size();
            tmp.addAll(tmp2);
            last = tmp.size();
            if(size ==last)
                break;
			/*if(!tmp.addAll(tmp2))
				break;*/

        }
        return tmp;
    }

    public void outL(String s){
        //System.out.println(s);
        try {
            FileWriter writer = new FileWriter(outputFile, true);
            writer.write(s+"\n");
            writer.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
    public void createAutoMachine(){
        outL("enum types AutoMachine(FILE* reader)");
        outL("{");
        outL("curser =0;");
        outL("int state = 0;");
        outL("while(1)");
        outL("{");
        outL("ch = getc(reader);");
        //outL()
        //outL("if(ch==EOF)");
        //outL("break;");
        outL("yytext[curser]=ch;");
        outL("curser++;");
        outL("switch(state)");
        outL("{");
        for(int i=0;i<open.size();i++)
        {
            outL("case "+open.get(i).id+":");
            ArrayList<Map<String,NodeSet>> array = open.get(i).edges;

            for(int j=0;j<array.size();j++)
            {
                String key = String.valueOf(open.get(i).edges.get(j).keySet().toString().charAt(1));
                String tmp = key;//key为边
                if(key.equals("\t"))
                {
                    key = "\\t";
                }
                if(key.equals("\n"))
                {
                    key = "\\n";
                }
                if(key.equals("\r")){
                    key = "\\r";
                }
                if(j==0)
                    outL("if(ch=='"+key+"')");
                else
                    outL("else if(ch=='"+key+"')");
                outL("  state="+open.get(i).edges.get(j).get(tmp).id+";");

            }
            if(open.get(i).end==1)
            {
                if(array.size()>0)
                    outL("else{");
                outL(open.get(i).action);
                outL("return;");
                if(array.size()>0)
                    outL("\n}");

            }
            outL("break;");
        }
        outL("}");
        outL("}");
        outL("}");

    }
    public void creatMain(){
        outL("int main()");
        outL("{");
        outL("FILE *reader = fopen(inputName,\"r\");");
        outL("FILE *fp1 = fopen(\"IDtable\",\"a+\");");
        outL("fprintf(fp1, \"%s %s\\n\",\"ID号\",\"编号\");");
        outL("FILE *fp2 = fopen(\"NUMtable\",\"a+\");");
        outL("fprintf(fp2, \"%s %s\\n\",\"数值\",\"编号\");");
        outL("fclose(fp1);");
        outL("fclose(fp2);");
        outL("if(reader == NULL)");
        outL("{");
        outL("fprintf(stderr,\"Error on open input file\\n\");");
        outL("exit(EXIT_FAILURE);");
        outL("}");
        outL("enum types tp;");
        outL("while(1)");
        outL("{");
        outL("yyvalue=-1;");
        outL("if(ch==EOF)");
        outL("break;");
        outL("tp = AutoMachine(reader);");
        outL("switch(tp)");
        outL("{");
        createHandle();
        outL("}");
        outL("ungetc(ch,reader);");
        outL("}");
        outL("FILE *fp = fopen(\"token\",\"a+\");");
        outL("fprintf(fp, \"<%c>\",'$');");

        outL("fclose(fp);");

        outL("}");
    }

    public void createHandle(){
        //ReadLexFile read = new ReadLexFile("mylex.l","parser.yy.c");
        ReadLex read = new ReadLex(input_path,outputFile);
        read.readAllType();
    }
}

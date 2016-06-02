import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Jch on 2016/5/12.
 */

public class Main {
    //运行本文件，输出程序在java工程目录下
    public static void main(String[] args) {
        String input = "mylex.l";
        String output = "lexAnalyse.c";
        ArrayList<String> List = new ArrayList<String>();
        ArrayList<String> Actions = new ArrayList<String>();

        SimpleDateFormat dfs = new SimpleDateFormat("HH:mm:ss:SSS");
        System.out.println("启动时间："+dfs.format(new Date()));
        PretreatmentLex.pretreatment(new File(input),List,Actions); //解析.l文件，对其进行预处理，包括加点和标准化

        SimpleDateFormat dfs1 = new SimpleDateFormat("HH:mm:ss:SSS");
        System.out.println("解析后时间："+dfs1.format(new Date()));

        NFA nfa = new NFA();
        List.set(0, " |\n|\t|\r");//将( |\\t|\\r|\\n)替换为 |\n|\t|\r
        //System.out.println(List.get(0));

        nfa.create_total_NFA(List,Actions);//对.l中的正规表达式生成相应的NFA
        SimpleDateFormat dfs2 = new SimpleDateFormat("HH:mm:ss:SSS");
        System.out.println("生成NFA后时间："+dfs2.format(new Date()));

        DFA dfa = new DFA(input,output);
        dfa.create_DFA(nfa);//在将NFA转DFA
        SimpleDateFormat dfs3 = new SimpleDateFormat("HH:mm:ss:SSS");
        System.out.println("生成DFA后时间："+dfs3.format(new Date()));

        //每次运行要删掉parser.yy.c因为文件是以追加的形式
        System.out.println("wait...");
        ReadLex read = new ReadLex(input,output);
        read.getFirstPart();
        read.getLastPart();
        dfa.createAutoMachine();
        SimpleDateFormat dfs4 = new SimpleDateFormat("HH:mm:ss:SSS");
        System.out.println("生成自动机函数后时间："+dfs4.format(new Date()));

        dfa.creatMain();
        SimpleDateFormat dfs5 = new SimpleDateFormat("HH:mm:ss:SSS");
        System.out.println("生成主函数后时间："+dfs5.format(new Date()));

        System.out.println("complete!");
    }

}


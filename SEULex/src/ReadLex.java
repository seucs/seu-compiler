import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jch on 2016/5/12.
 */


public class ReadLex {
    private String filename ="";
    private String outputFile="";
    ArrayList<String> al_types = new ArrayList<String>();

    public ReadLex(String name,String outFile) {
        filename = name;
        outputFile = outFile;
    }
    public void getFirstPart(){
        //将.l文件的头部直接导入到yy.c文件中
        File file = new File(filename);//打开文件filename
        BufferedReader reader = null;
        String tempString = null;
        int start = 0;
        try {
            reader = new BufferedReader(new FileReader(file));//读取文件放入BufferedReader中
            FileWriter writer = new FileWriter(outputFile, true);
            //匹配定义部分
            while ((tempString = reader.readLine()) != null){
                if(tempString.equals("%{")){//以%{识别第一段的头部
                    start = 1;
                    continue;
                }
                if(tempString.equals("%}"))//当识别到}%标志着第一段结束
                    break;
                if(start==1){
                    //进行写入
                    writer.write(tempString+"\n");
                }

            }
            writer.close();
            reader.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
    public void getLastPart(){
        //将.l文件的尾部直接导入到yy.c文件中
        File file = new File(filename);
        BufferedReader reader = null;
        String tempString = null;
        int start = 0;
        try {
            reader = new BufferedReader(new FileReader(file));
            FileWriter writer = new FileWriter(outputFile, true);
            while ((tempString = reader.readLine()) != null){
                //匹配用户子程序部分
                if(tempString.equals("%%")){//识别到一个%%，start加1
                    //第二次识别到%%时用户子程序部分
                    start++;
                    continue;
                }
                if(start==2){			//当识别到第二个%%，即最后一段时，直接将最后一段内容拷贝到输出文件中
                    writer.write(tempString+"\n");
                }
            }
            writer.close();
            reader.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }
    public Map<String,String> getRegExp(){//获取正规表达式
        File file = new File(filename);
        BufferedReader reader = null;
        String tempString = null;
        int start = 0;
        Map<String,String> map = new HashMap<String, String>();
        try {
            reader = new BufferedReader(new FileReader(file));
            while ((tempString = reader.readLine()) != null){

                if(tempString.equals("%}")){
                    start=1;
                    continue;
                }
                if(tempString.equals("%%")){
                    break;
                }
                if(start==1){
                    //System.out.println(tempString);
                    if (!tempString.equals("")){
                        String[] temp = tempString.split(" ");
                        //System.out.println(temp.length);
                        if(temp!=null){
                            map.put(temp[0],temp[1]);//ex. alpha [A-Za-z]  temp[0]=alpha temp[1]=[A-Za-z]
                            //System.out.println(temp[0]+"=>"+temp[1]+temp[3]);
                        }
                    }
                }
            }

            reader.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return null;

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
    public void readAllType(){//获得第一段所定义的关键字，在enum中
        File file = new File(filename);
        BufferedReader reader = null;
        String tempString = null;
        String alltypes =null;
        int start = 0;
        try {
            reader = new BufferedReader(new FileReader(file));
            FileWriter writer = new FileWriter(outputFile, true);
            while ((tempString = reader.readLine()) != null){
                if(tempString.equals("%{")){
                    start = 1;
                    continue;
                }
                if(tempString.equals("%}"))
                    break;
                if(start==1){
                    String str = tempString.trim();
                    if(str.startsWith("enum")){
                        start =2;
                    }
                }
                if(start==2){
                    //tempString = tempString.trim();
                    String array[] = tempString.split("\\{");
                    //System.out.println(array[1]);
                    String tmp[] = array[1].split("\\}");
                    //System.out.println("!"+tmp[0]);
                    alltypes = tmp[0];
                    if(!tempString.contains("}")){
                        while ((tempString = reader.readLine()) != null){
                            if(tempString.contains("}")){
                                String last_array[] = tempString.split("\\}");
                                alltypes = alltypes+ last_array[0];
                                break;
                            }
                            else{
                                alltypes = alltypes+ tempString;
                            }
                        }
                    }
                    //System.out.println(alltypes);
                    String list[] = alltypes.split(",");
                    for(int i=0;i<list.length;i++){
                        //System.out.println(list[i]);
                        al_types.add(list[i]);
                    }
                    //System.out.println(list.length);
                    break;
                }


            }
            writer.close();
            reader.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        //System.out.println(al_types.size());
        for(int i=0;i<al_types.size();i++){//用于DFA中主函数的生成
            outL("case "+al_types.get(i)+":");
            outL("outToken(yyvalue,\""+al_types.get(i)+"\",yytext);");//outToken函数定义在.l文件的最后一段，用于输出Token序列
            outL("break;");
        }

    }

}
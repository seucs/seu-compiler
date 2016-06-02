import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jch on 2016/5/12.
 */

public class PretreatmentLex
{
    //加点操作
    public static String finalProcess(String stringToBeProcessed,HashMap<String, String> regexMap)
    {
        Pattern finalPattern=Pattern.compile("\\{[^}]+\\}");
        Matcher finalMatcher=finalPattern.matcher(stringToBeProcessed);
        //String newString=new String(stringToBeProcessed);
        while(finalMatcher.find())
        {
            String toBeReplaced=finalMatcher.group(0);
            //System.out.println(toBeReplaced);
            stringToBeProcessed=stringToBeProcessed.replace(toBeReplaced, "("+regexMap.get(toBeReplaced.substring(1,toBeReplaced.length()-1))+")");
        }
        stringToBeProcessed=stringToBeProcessed.replace(".(", "..(");
        stringToBeProcessed=stringToBeProcessed.replace(")(", ").(");
        stringToBeProcessed=stringToBeProcessed.replace("*(", "*.(");
        //System.out.println(stringToBeProcessed);
        return stringToBeProcessed;

    }
    //规则处理
    public static int processMultiLineRules(final Vector<String> rulesVector,final int currentRows,final int currentRowsToken,StringBuilder sb)
    {
        //StringBuffer sb=new StringBuffer();
        Stack<Integer> braceStack=new Stack<Integer>();
        int rowsBeingProcessed=currentRows;
        //对规则当前行进行处理
        //判断括号是否匹配
        for(int i=currentRowsToken;i<rulesVector.get(rowsBeingProcessed).length();i++)
        {
            //处理动作部分
            //找到{
            if(rulesVector.get(rowsBeingProcessed).charAt(i)=='{')
            {
                //System.out.println("stack pushed");
                braceStack.push(1);
            }
            //找到}
            else if(rulesVector.get(rowsBeingProcessed).charAt(i)=='}')
            {
                //System.out.println("stack poped");
                braceStack.pop();
            }

            //System.out.println(i+(rulesVector.get(rowsBeingProcessed).charAt(i)));
            //System.out.println(rulesVector.get(rowsBeingProcessed).substring(currentRowsToken, rulesVector.get(rowsBeingProcessed).length()).trim()+" appended");
        }
        //添加到sb即动作列表里
        sb.append(rulesVector.get(rowsBeingProcessed).substring(currentRowsToken, rulesVector.get(rowsBeingProcessed).length()).trim());

        //如果当前行没有找到匹配括号，向下一行寻找，直接找到停止，将数加入sb末端
        while(!braceStack.isEmpty())
        {//寻找匹配的{}
            //System.out.println("XXXXXX");
            rowsBeingProcessed+=1;
            for(int i=0;i<rulesVector.get(rowsBeingProcessed).length();i++)
            {
                if(rulesVector.get(rowsBeingProcessed).charAt(i)=='{')
                {
                    //System.out.println("stack pushed");
                    braceStack.push(1);
                }
                else if(rulesVector.get(rowsBeingProcessed).charAt(i)=='}')
                {
                    //System.out.println("stack poped");
                    braceStack.pop();
                }

            }
            sb.append(rulesVector.get(rowsBeingProcessed).trim());
            //System.out.println(rulesVector.get(rowsBeingProcessed).trim()+" appended");
        }


        return rowsBeingProcessed;
    }

    //进行转变处理
    public static void pretreatment(File lexToBeProcessed,ArrayList<String> regexList,ArrayList<String> actionList)
    {

        //HashMap<String, String> returnList=new HashMap<String, String>();
        Scanner s=null;//创建Scanner为空

        try
        {
            //s读入mylex.l文件
            s= new Scanner(lexToBeProcessed);
            //s = new Scanner(new File("f:/mylex.l"));
            //s = new Scanner(new File("f:/lex.l"));
        }
        catch (FileNotFoundException e)
        {	//异常处理
            e.printStackTrace();
        }
        //创建StringBuffer
        //利用StringBuffer在进行字符串处理时，不生成新的对象，StringBuffer对象的每次修改都会改变对象自身
        StringBuffer sBuffer=new StringBuffer();

        while(s.hasNextLine())//判断是否存在下一行
        {
            //System.out.println("1");
            sBuffer.append(s.nextLine());//在sBuffer的结尾插入s.nextLine()
            sBuffer.append("\n");//在sBuffer的结尾插入换行符
        }

        String input=sBuffer.toString();//把处理后的存在sBuffer的内容赋给input字符串
        //System.out.println(input);
        s.close();//.l文件扫描处理结束


        //对应于lex文件中的%}后的正则表达式的定义
        //略掉%}前面的定义部分
        Pattern pattern = Pattern.compile("\\%}[^a-zA-Z0-9]+([^\\n]+\\n)+");//将给定的正则表达式编译并赋予给Pattern类
        //将%}与后面的内容进行分段
        //Pattern pattern = Pattern.compile("\\%}");
        Matcher matcher = pattern.matcher(input);//匹配处理

        //EXTRACT THE REGULAR EXPRESSIONS
        //提取正则表达式
        String s1=null;
        while(matcher.find())//使用find()方法查找第一个匹配的对象,.l文件头文件部分取出
        {
            //提取定义词法规则之间的部分
            s1=matcher.group(0);//找到第一个匹配"\\%}[^a-zA-Z0-9]+([^\\n]+\\n)+"的对象,.l文件头文件部分取出
        }

        //处理常量部分
        String[] aStrings=s1.split("\n");//创建aStrings数组，存放常量部分
        HashMap<String,String> regexMap=new HashMap<String,String>();
        for(int i=0;i<aStrings.length;i++)
        {
            String currentRow=aStrings[i].trim();//删除字符串首尾的空白，但会保留字符串内部作为词与词之间分隔的空格。
            if(currentRow.length()==0) continue;
            boolean jumpFlag=false;//跳出循环标志位
            for(int j=0;j<currentRow.length();j++)
            {
                if  (jumpFlag==true) break;
                if(currentRow.charAt(j)==' '||currentRow.charAt(j)=='\t')
                {
                    //hash映射取出常量部分
                    //j->找到非空格部分，取出前部，再利用trim()去掉空格取出后部份
                    regexMap.put(currentRow.substring(0, j), currentRow.substring(j, currentRow.length()).trim());
                    jumpFlag=true;
                    continue;
                }
            }
        }

        //处理规则部分
        pattern=Pattern.compile("%%([^\\n]*\\n)+%%");
        matcher=pattern.matcher(input);//匹配规则部分
        Vector<String> rulesVector=new Vector<String>();//将第二段中每一行都加入到这个向量容器中，对于.l文件，其大小为56
        while(matcher.find())
        {
            String temp=matcher.group(0);//获取规则部分
            Pattern tempPattern=Pattern.compile("[^\\n]+\n");
            Matcher insideMatcher=tempPattern.matcher(temp);
            while(insideMatcher.find())//对每行进行处理
            {
                String currentRow=insideMatcher.group(0);
                currentRow=currentRow.trim();
                if(currentRow.equals("%%"))
                    continue;//略去开始的%%符号
                else if(currentRow.length()==0) continue;//略去空白行
                rulesVector.add(currentRow);//有效的规则部分内容存入向量中
                //System.out.println(currentRow);
                //System.out.println(rulesVector.size());

            }
        }

        //对获取的规则部分进行处理先取出模式部分，在调用函数处理动作部分
        //HashMap <String,String> rulesMap=new HashMap<String,String>();
        for(int i=0;i<rulesVector.size();i++)
        {
            String rules=null;
            //String action=null;

            if(rulesVector.get(i).charAt(0)=='{')//{}处理ID和数字部分
            {
                StringBuilder actionStringBuilder=new StringBuilder();
                int count;//计数
                for(count=1;count<=rulesVector.get(i).length();count++)
                {	//忽略{，		获取该行字符串长度

                    if(rulesVector.get(i).charAt(count)==' '||rulesVector.get(i).charAt(count)=='\t')
                    {
                        rules=rulesVector.get(i).substring(0,count);

                        //调用函数进行处理
                        i=processMultiLineRules(rulesVector,i,count+1,actionStringBuilder);

                        break;
                    }

                }
                //增加规则
                regexList.add(rules);
                actionList.add(actionStringBuilder.toString());
                continue;
            }

            else if(rulesVector.get(i).charAt(0)=='[')//[]一个字符集合。匹配括号内的任意字符。
            {
                StringBuilder actionStringBuilder=new StringBuilder();
                int count;
                for(count=1;count<=rulesVector.get(i).length();count++)
                {
                    if(rulesVector.get(i).charAt(count)==']')//找到]
                    {
                        rules=rulesVector.get(i).substring(0,count+1);
                        //调用函数进行处理
                        i=processMultiLineRules(rulesVector,i,count+1,actionStringBuilder);

                        break;
                    }
                }

                regexList.add(rules);
                actionList.add(actionStringBuilder.toString());

                continue;
            }

            else if(rulesVector.get(i).charAt(0)=='"')
            {
                StringBuilder actionStringBuilder=new StringBuilder();
                int count;
                for(count=1;count<=rulesVector.get(i).length();count++)
                {
                    if(rulesVector.get(i).charAt(count)=='"')
                    {
                        rules=rulesVector.get(i).substring(0,count+1);

                        i=processMultiLineRules(rulesVector,i,count+1,actionStringBuilder);

                        break;
                    }
                }

                regexList.add(rules);
                actionList.add(actionStringBuilder.toString());

                continue;
            }

            else
            {
                StringBuilder actionStringBuilder=new StringBuilder();
                int count;
                for(count=1;count<=rulesVector.get(i).length();count++)
                {


                    if(rulesVector.get(i).charAt(count)==' '||rulesVector.get(i).charAt(count)=='\t')
                    {
                        rules=rulesVector.get(i).substring(0,count+1).trim();

                        i=processMultiLineRules(rulesVector,i,count+1,actionStringBuilder);

                        break;
                    }
                }

                regexList.add(rules);
                actionList.add(actionStringBuilder.toString());

                continue;
            }
        }

        //对%}后的规则部分进行处理

        //遍历regexMap（处理常量部分）
        Set<Entry<String, String>> regexSet=regexMap.entrySet();
        Iterator<Entry<String, String>> regexIterator=regexSet.iterator();
        while(regexIterator.hasNext())
        {
            Entry<String, String> currentRegex=regexIterator.next();//获取常量设定eg：alphanum=[A-Za-z0-9]
            //String currentKey=currentRegex.getKey();
            String currentValue=currentRegex.getValue();//获取常量设定值eg：[A-Za-z0-9]
            Pattern regexPattern=Pattern.compile("[^-]\\-[^-]");//规则设置寻找 'X-X'
            Matcher regexMatcher=regexPattern.matcher(currentValue);//匹配规则
            //System.out.println(currentValue);
            Vector<String> candidateVector=new Vector<String>();
            while(regexMatcher.find())
            {
                //System.out.println("regex processing");
                String rangeString=regexMatcher.group(0);//A-Z  a-z 0-9
                //System.out.println(regexMatcher.group(0));
                //把A-Z a-z 0-9全部存入candidateVector
                for(int i=(int)rangeString.charAt(0);i<=(int)rangeString.charAt(2);i++)
                {
                    candidateVector.add(new Character((char)i).toString());

                }//System.out.println("ss"+candidateVector.get(1));

            }

            for(int i=0;i<currentValue.length();i++)
            {
                if(currentValue.charAt(i)=='\\')
                {
                    i++;
                    //处理转义符
                    candidateVector.add("\\"+currentValue.charAt(i));
                }
            }
            StringBuilder regexBuilder=new StringBuilder();
            //对candidateVector存放所有字符直接加'|'放入regexBuilder中
            for(int i=0;i<candidateVector.size();i++)
            {
                regexBuilder.append(candidateVector.get(i));
                if(i<candidateVector.size()-1)
                {
                    regexBuilder.append("|");
                }
            }
            //对currentRegex赋值
            currentRegex.setValue(regexBuilder.toString());
        }
        //常量部分处理完成

        //Set<Entry<String, String>> rulesSet=rulesMap.entrySet();
        //Iterator<Entry<String, String>> rulesIterator=rulesSet.iterator();
        //处理规则部分
        int n=0;
        while(n<actionList.size())
        {
            //顺序获取处理规则
            String currentKey=regexList.get(n);

            StringBuilder sBuilder=new StringBuilder();
            //"开头---处理操作符eg:"<"
            if(currentKey.charAt(0)=='"')
            {
                for(int i=1;i<currentKey.length()-1;i++)
                {
                    if(currentKey.charAt(i)=='.')
                    {
                        sBuilder.append("\\");
                        sBuilder.append(currentKey.charAt(i));
                        continue;
                    }
                    else if(currentKey.charAt(i)=='\\')
                    {
                        sBuilder.append(currentKey.charAt(i));
                        i++;
                        if(currentKey.charAt(i)!='\"')
                        {
                            sBuilder.append("."+currentKey.charAt(i));
                            continue;
                        }
                        else
                            continue;

                    }
                    sBuilder.append(currentKey.charAt(i));
                    if(i==currentKey.length()-2)
                        continue;
                    sBuilder.append(".");
                }
                //System.out.println(sBuilder.toString());
                //Action action=new Action(currentKey, sBuilder.toString());
                regexList.set(n, sBuilder.toString());//将regexList[n]替换成加点后的字符串

                n++;
                continue;
                //returnList.put(sBuilder.toString(), currentValue);

            }
            //[开头,处理[ \t\r\n]	{ }------------( |\t|\r|\n)
            else if(currentKey.charAt(0)=='[')
            {
                sBuilder.append("(");
                for(int i=1;i<currentKey.length()-1;i++)
                {
                    if(currentKey.charAt(i)=='\\')
                    {
                        sBuilder.append(currentKey.charAt(i));
                        i++;
                        sBuilder.append(currentKey.charAt(i));
                        if(currentKey.charAt(i+1)==']')
                        {
                            break;
                        }
                        sBuilder.append("|");
                        continue;
                    }

                    sBuilder.append(currentKey.charAt(i));
                    if(currentKey.charAt(i+1)==']')
                    {
                        break;
                    }
                    sBuilder.append("|");
                }
                sBuilder.append(")");
                //System.out.println(sBuilder.toString());
                //returnList.put(sBuilder.toString(), currentValue);
                regexList.set(n, sBuilder.toString());

                n++;
                continue;

            }
            //字母开头，处理关键字eg:if
            else if(Character.isLetter(currentKey.charAt(0)))
            {
                for(int i=0;i<currentKey.length();i++)
                {
                    sBuilder.append(currentKey.charAt(i));
                    if(i==currentKey.length()-1)
                    {
                        continue;
                    }
                    sBuilder.append(".");
                }
                //System.out.println(sBuilder.toString());
                //returnList.put(sBuilder.toString(), currentValue);
                regexList.set(n, sBuilder.toString());

                n++;
                continue;
            }

            else
            {	//处理ID和数字表达式，eg{alpha}{alphanum}*
                //Pattern.compile("\\{[^}]+\\}+").matcher("123").replaceAll(repl);
                //Pattern.compile(regex);
                Pattern replacePattern=Pattern.compile("\\{[^}]+\\}\\+");//设置规则
                Matcher replaceMatcher=replacePattern.matcher(currentKey);//匹配规则
                while(replaceMatcher.find())
                {	//获取匹配
                    String replaceString=replaceMatcher.group(0);
                    currentKey=currentKey.replaceFirst("\\{[^}]+\\}\\+", replaceString.substring(0,replaceString.length()-1)+replaceString.substring(0,replaceString.length()-1)+"*");
                    //System.out.println(currentKey+"-----");
                }
                while(currentKey.contains("?"))
                {
                    //System.out.println("while");
                    int i=currentKey.indexOf("?");
                    //for(int i=currentKey.length()-1;i>=0;i--)
                    //System.out.println("for");
                    int count=0;
                    int begin=0;
                    Stack<Integer> braceStack=new Stack<Integer>();

                    begin=i;
                    i--;
                    //匹配()
                    if(currentKey.charAt(i)==')')
                        braceStack.push(1);
                    i--;
                    while(!braceStack.isEmpty())
                    {
                        //System.out.println(currentKey.charAt(i));
                        if(currentKey.charAt(i)==')')
                        {
                            braceStack.push(1);
                        }
                        else if(currentKey.charAt(i)=='(')
                        {
                            braceStack.pop();
                            if(braceStack.isEmpty())
                            {
                                count=i;
                                break;
                            }
                        }
                        i--;
                    }
                    //第二匹配
                    String secondPart=currentKey.substring(count,begin+1);
                    //System.out.println(currentKey.substring(count,begin+1));
                    int placeOfTheFirstRight=0;
                    for(int j=count;j>=0;j--)
                    {
                        if(currentKey.charAt(j)=='}')
                        {
                            braceStack.push(1);
                            placeOfTheFirstRight=j;
                            break;
                        }
                    }
                    int j=placeOfTheFirstRight-1;
                    while(!braceStack.isEmpty())
                    {
                        if(currentKey.charAt(j)=='}')
                            braceStack.push(1);
                        if(currentKey.charAt(j)=='{')
                            braceStack.pop();
                        j--;
                    }
                    //第三匹配
                    String firstPart=currentKey.substring(j+1,count);
                    //System.out.println(firstPart);
                    currentKey=currentKey.replace(firstPart+secondPart, "("+firstPart+"|"+firstPart+secondPart.substring(0, secondPart.length()-1)+")");

                }

                currentKey=finalProcess(currentKey, regexMap);
                regexList.set(n, currentKey);
                //actionList.add(currentValue);
                n++;
                continue;
            }

        }

        //return returnList;
    }
    public static void main(String[] args)
    {
        ArrayList<String> regexList=new ArrayList<String>();
        ArrayList<String> actionList=new ArrayList<String>();
        pretreatment(new File("C:/Users/Toshiba/Desktop/Lex/mylex.l"),regexList,actionList); //specify the lex file here
        if(regexList.size()==actionList.size())
        {
            System.out.println("CORRECT");
            for(int i=0;i<regexList.size();i++)
                System.out.println(regexList.get(i)+":"+actionList.get(i));
        }
        else
        {
            System.out.println("wrong");
        }
        // the traverse of the HashMap

    }
}
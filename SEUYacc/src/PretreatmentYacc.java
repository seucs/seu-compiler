import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jch on 2016/5/20.
 */

public class PretreatmentYacc
{
	public static void read(File f, HashMap<String, ArrayList<Item>> hashmap,
							ArrayList<Item> productors)
	{
		String inputString=null;
		StringBuilder sBuilder=new StringBuilder();
		Scanner s=null;//Scanner默认使用空格作为分割符来分隔文本

		try
		{
			s=new Scanner(f);
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(s.hasNextLine())
		{
			sBuilder.append(s.nextLine());
			sBuilder.append("\n");
		}
		inputString=sBuilder.toString();
		//System.out.println(sBuilder);
		s.close();
		Pattern pattern=Pattern.compile("\\%%[\\d|\\D]+\\%%");
		Matcher matcher=pattern.matcher(inputString);
		int k=0;
		while(matcher.find())
		{
			k++;
			inputString=matcher.group(0);
		}
		if(k>1)
			System.out.println("error");

		String[] rowString=inputString.split("\n");
		int []beginCount=new int[100];
		int []endCount=new int[100];
		int index=0;
		for(int i=0;i<rowString.length;i++)
		{
			rowString[i]=rowString[i].trim();
			System.out.println(i+rowString[i]);
		}
		for(int i=0;i<rowString.length;i++)
		{
			if(rowString[i].contains("%%"))
			{
				continue;
			}
			else if(rowString[i].equals(""))
			{
				continue;
			}
			else if(rowString[i].contains(":"));  //假设右部的产生式不包含：
			{
				beginCount[index]=i;//产生式开始的行数
				while(rowString[i].charAt(rowString[i].length()-1)!=';')
				{
					//System.out.println(rowString[i]);
					//System.out.println(i);
					i++;
				}
				endCount[index]=i;//产生式结束的行数
				index++;
			}
		}

		for(int i=0;i<index;i++)
		{
			//System.out.println(i);
			ArrayList<Item> alArrayList=new ArrayList<Item>();
			int leftNUmber=rowString[beginCount[i]].indexOf(":");
			String leftName=rowString[beginCount[i]].substring(0,leftNUmber).trim();
			//System.out.println(leftNUmber+"leftName:"+leftName);
			//System.out.println(beginCount[i]);
			//rowString[beginCount[i]]=rowString[beginCount[i]].substring(leftNUmber+2,rowString[beginCount[i]].length());

			for(int j=beginCount[i];j<endCount[i];j++)
			{
				rowString[j]=rowString[j].trim();
				if(rowString[j].charAt(0)=='|')
				{
					//System.out.println("| eliminated");
					rowString[j]=rowString[j].substring(2,rowString[j].length());
				}
				Item it=new Item();
				it.left=leftName;
				rowString[j]=rowString[j].replaceAll("\\/\\*[^\\(]+\\*\\/", "");//将注释/**/排除
				rowString[j]=rowString[j].trim();
				System.out.println(rowString[j]);

				if(rowString[j].length()>=1&&rowString[j].charAt(rowString[j].length()-1)=='}')
				{
					int count=rowString[j].length()-2;
					Stack<Integer> braceStack=new Stack<Integer>();
					braceStack.push(1);
					while(!braceStack.isEmpty())
					{
						if(rowString[j].charAt(count)=='}')
							braceStack.push(1);
						else if(rowString[j].charAt(count)=='{')
							braceStack.pop();
						count--;
					}//找到语义规则开始的地方count
					it.semantic=rowString[j].substring(count+1,rowString[j].length());

					int start=rowString[j].indexOf(":");
					int end=count;
					String rightString=rowString[j].substring(start+1,end);
					//System.out.println(rightString);
					String[] rights=rightString.trim().split(" ");
					//System.out.println("Right Added:");
					for(int m=0;m<rights.length;m++)
					{
						//System.out.println(m+rights[m]);
						it.right.add(rights[m]);
					}
					//it.left=rightString;
					//}
					alArrayList.add(it);
					productors.add(it);
					continue;
				}
				else //该产生式没有语义规则
				{
					//System.out.println("aaaa");
					if(rowString[j].trim().isEmpty())
						continue;
					else if(j!=beginCount[i])
					{
						//
						String[] rights=rowString[j].split(" ");
						for(int m=0;m<rights.length;m++)
						{
							//System.out.println(rights[m]);
							it.right.add(rights[m]);
						}
					}
					else {
						continue;
					}
					alArrayList.add(it);
					productors.add(it);

				}

			}
			hashmap.put(leftName,alArrayList);//产生式的左部和其对应包括的产生式
			//System.out.println("--------------");
		}
		Item firstElement=new Item();
		firstElement.right.add(productors.get(0).left);
		firstElement.left="start";
		ArrayList<Item> zeroProductionArrayList=new ArrayList<Item>();
		zeroProductionArrayList.add(firstElement);
		hashmap.put("start", zeroProductionArrayList);
		productors.add(0, firstElement);//在开始位置插入零号产生式
	}

}

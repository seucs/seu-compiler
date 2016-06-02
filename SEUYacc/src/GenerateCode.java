import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Jch on 2016/5/20.
 */

public class GenerateCode {
	public ArrayList<String> outfitArrayList;//PPT表头
	public int [][] table;//PPT 表
	public ArrayList<String> action_list;//语义动作
	public int length;//项集个数
	public ArrayList<Item> products_list;
	public String outputFile;
	public GenerateCode(ArrayList<String> o, ArrayList<String> a,int [][] t,int l,ArrayList<Item> p) {
		outfitArrayList =o;
		action_list =a ;
		table = t;
		length  = l;
		products_list = p;
	}
	public void setOUTFILE(String s){
		outputFile = s;
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
	public void out(String s){
		//System.out.print(s);
		try {
			FileWriter writer = new FileWriter(outputFile, true);
			writer.write(s);
			writer.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	//将规约时执行的语义动作函数输出到相应的代码位置中
	public void createAction(){
		outL("void semanticAction(int productionNo)");
		outL("{");
		outL("switch(productionNo)");
		outL("{");
		for(int i=0;i<action_list.size();i++){
			outL("case "+i+":");
			outL(action_list.get(i));
			outL("break;");
		}
		outL("}");
		outL("}");
	}
	//将ACTION-GOTO表、表头、和文法产生式放入数组
	public void createInit(){
		outL("int size_all_sym="+outfitArrayList.size()+";");
		outL("string reg_Exp["+(products_list.size()-1)+"] =");
		outL("{");
		for(int i=1;i<products_list.size();i++){
			Item it = products_list.get(i);
			out("\""+it.left+":");
			for(String r: it.right){
				out(r+" ");
			}
			if(i!=products_list.size()-1)
				outL("\",");
			else
				outL("\"");
		}
		outL("};");
		outL("string all_symbols["+outfitArrayList.size()+"]= ");
		outL("{");
		for(int i=0;i<outfitArrayList.size();i++)
		{
			out("\""+outfitArrayList.get(i)+"\"");
			if(i!=outfitArrayList.size()-1)
				out(",");
		}
		outL("");
		outL("};");
		outL("int parser_table["+length+"]["+outfitArrayList.size()+"] =");
		outL("{");
		for(int i=0;i<length;i++)
		{
			for(int j=0;j<outfitArrayList.size();j++)
			{
				if(i==length-1&&j==outfitArrayList.size()-1)
					out(table[i][j]+"");
				else
					out(table[i][j]+",");
			}
			outL("");
		}
		outL("};");
	}


}

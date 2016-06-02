import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Jch on 2016/5/20.
 */

public class ReadYacc {
	private String filename ="";
	private String outputFile="";

	public ReadYacc(String name,String outFile) {
		filename = name;
		outputFile = outFile;
	}
	public void getFirstPart(){
		//将.y文件的头部直接导入到parser.cpp文件中
		File file = new File(filename);
		BufferedReader reader = null;
		String tempString = null;
		int start = 0;
		try {
			reader = new BufferedReader(new FileReader(file));
			FileWriter writer = new FileWriter(outputFile, true);
			while ((tempString = reader.readLine()) != null){

				if(start==0){
					start =1;
				}
				else if(tempString.equals("%}"))
					break;
				else{
					System.out.println(tempString);
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
		//将.y文件的尾部直接导入到parser.cpp文件中
		File file = new File(filename);
		BufferedReader reader = null;
		String tempString = null;
		int start = 0;
		try {
			reader = new BufferedReader(new FileReader(file));
			FileWriter writer = new FileWriter(outputFile, true);
			while ((tempString = reader.readLine()) != null){

				if(tempString.equals("%%")){
					start++;
					continue;
				}
				if(start==2){
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

}

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jch on 2016/5/20.
 */

public class GetTokenPriority
{
	public static void getPriorityArray(File inputFile,HashMap<String, Integer> priorityHashMap,ArrayList<String> priorityFeature)
	{
		StringBuilder sBuilder=new StringBuilder();
		Scanner scanner=null;
		try
		{
			scanner = new Scanner(inputFile);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		while(scanner.hasNextLine())
		{
			sBuilder.append(scanner.nextLine());
			sBuilder.append("\n");
		}
		String inputString=sBuilder.toString();
		int start =inputString.indexOf("%}");
		int end=inputString.indexOf("%%");
		inputString=inputString.substring(start+2,end).trim();
		Pattern pattern=Pattern.compile("%[^%]+");
		Matcher matcher=pattern.matcher(inputString);
		int i=0;
		while(matcher.find())
		{
			String[] operTheSamePri=matcher.group(0).trim().split("\\ ");

			for(int j=1;j<operTheSamePri.length;j++)
			{
				priorityHashMap.put(operTheSamePri[j], i);//操作符与优先级，数字越小优先级越低
			}
			//优先级的性质，左结合还是又结合
			priorityFeature.add(operTheSamePri[0].substring(1,operTheSamePri[0].length()));
			i++;
		}


	}
}

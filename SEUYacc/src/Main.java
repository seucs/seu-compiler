
/**
 * Created by Jch on 2016/5/20.
 */

public class Main {
	//运行本文件，输出程序在java工程目录下
	public static void main(String[] args)
	{
		Parser parser = new Parser("./Yacc.y");
		//Parser parser = new Parser("a.txt");
		parser.createPPT();
		System.out.println("complete!");
	}

}

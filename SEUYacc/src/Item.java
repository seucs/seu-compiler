import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by Jch on 2016/5/20.
 */

//LR项
public class Item
{
	public String left; //产生式左部
	public ArrayList<String> right = new ArrayList<String>(); //产生式右部的每个字符串集合，以空格分割
	public int dot = 0; //当前点所在位置
	public HashSet<String> forward = new HashSet<String>(); //向前看符号集
	public String semantic = new String(); //语义动作

	//构造函数
	public Item()
	{}

	public Item(Item item)
	{
		left = new String(item.left);
		right.addAll(item.right);
		forward.addAll(item.forward);
		dot = item.dot;
		semantic = new String(item.semantic);

	}

	public void out()
	{
		//System.out.println("******************");
		System.out.print(left);
		System.out.print(" -> ");
		//right.add(dot, ".");
		int i;
		for ( i = 0; i < right.size(); i++)
		{
			if(i == dot)
				System.out.print(".");
			System.out.print(right.get(i));
		}
		if(right.size() == dot)
			System.out.print(".");
		System.out.print(" , ");
		for (String s : forward)
		{
			System.out.print(s+"|");
		}
		System.out.println("");
	}

	//返回当前item点后的字符
	public String getCurrent()
	{
		if(dot < right.size())
			return right.get(dot);
		else
			return null;
	}

	//seter和getter
	public String getLeft()
	{
		return left;
	}

	public void setLeft(String left)
	{
		this.left = left;
	}

	public ArrayList<String> getRight()
	{
		return right;
	}

	public void setRight(ArrayList<String> right)
	{
		this.right = right;
	}

	public int getDot()
	{
		return dot;
	}

	public void setDot(int dot)
	{
		this.dot = dot;
	}

	public HashSet<String> getForward()
	{
		return forward;
	}

	public void setForward(HashSet<String> forward)
	{
		this.forward = forward;
	}

	public String getSemantic()
	{
		return semantic;
	}

	public void setSemantic(String semantic)
	{
		this.semantic = semantic;
	}

	public int hashCode()
	{
		return 0;
	}

	public boolean equals(Object o)
	{

		Item s = (Item) o;

		/******新加――构造LALR*********/
		if(s.dot==dot && s.left.equals(left) && s.right.equals(right)
				&& s.semantic.equals(semantic) )
			return true;
		else
			return false;

	}


}

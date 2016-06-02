import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Created by Jch on 2016/5/20.
 */

public class Parser
{
	//键值表示文法左部，值表示对应的产生式集合
	public HashMap<String, ArrayList<Item>> products_map = new HashMap<String, ArrayList<Item>>();
	public ArrayList<Item> products_list = new ArrayList<Item>();//产生式集合
	public ArrayList<String> sematic_list = new ArrayList<String>();//产生式语义集合
	public Item start_item; //初始item（文法开始Item项）
	public ItemSet start_itemSet; //初始itemset（文法开始Item项集）
	public Set<ItemSet> set_LR = new HashSet<ItemSet>(); //存储LR(1)项集
	public int [][] table;//存储PPT
	private String path;//读取的yacc.y路径
	public Parser(String p) {
		path = p;
	}

	public ItemSet closure(LinkedList<Item> queue)
	{	//对queue产生式进行拓展，返回以queue产生式开始的项集
		Item item;
		ItemSet itemset = new ItemSet();
		//poll函数返回queue的头元素，然后删除之
		 /*item是正扫描的LR项
		  * itemset是已经扫描过的LR项集
		  * queue是还未扫过的LR项的集合
		  */
		while((item = queue.poll()) != null)
		{
			String current = item.getCurrent();//返回当前item点后的位置的字符
			if(products_map.containsKey(current))
			{//若是非终结符
				HashSet<String> new_forward = getForward(item);  //计算新Item的预测符集合forward
				for (Item i : products_map.get(current))
				{//将预测符加入非终结符对应的所有产生式
					Item new_item = new Item(i);
					HashSet<String> n_forward = new HashSet<String>();
					n_forward.addAll(new_forward);
					new_item.setForward(n_forward);	//将新产生的预测符集合加入到新项集中
					//如果i不存在将扫描队列queue中和已扫描的 itemset包含的项集中，则加入将queue
					test(new_item,itemset.getItems(),queue);
				}
			}
	    	 /*已经扫描过的item放入LR项集itemset，加入时将会合并同心
	    	  item的预测符集合并更新itemset的成员变量——发出边edge_set*/
			merge(item,itemset);
		}
		return itemset;
	}

	//已经扫描过的item放入LR项集itemset，加入时将会合并同心
	//item的预测符集合并更新itemset的成员变量——发出边edge_set
	private void merge(Item item, ItemSet itemset)
	{
		boolean exist = false;
		for (Item i : itemset.items)
		{
			if(i.left.equals(item.left) && i.right.equals(item.right)
					&& i.dot == item.dot)
			{
				i.forward.addAll(item.forward);
				exist = true;
				break;
			}
		}
		//如果itemLR项不在这个项集中，则将其加入
		if(!exist)
		{
			itemset.items.add(item);
		}
		//如果该项不是归约项，则更新itemset的edge_set
		if(item.getCurrent() != null)
		{
			itemset.edge_set.add(item.getCurrent());

		}
	}

	private void test(Item item, Set<Item> items, Queue<Item> queue_a)
	{
		//如果item不存在将扫描队列queue_a中和已扫描的 items项集中，则加入将queue_a
		for (Item i : items)
		{
			if(i.left.equals(item.left) && i.right.equals(item.right) && i.dot == item.dot)
			{
				item.forward.removeAll(i.forward);
			}
		}
		//
		if(item.forward.size() > 0)
		{
			for (Item i : queue_a)
			{
				if(i.left.equals(item.left) && i.right.equals(item.right) && i.dot == item.dot)
				{
					//
					i.forward.addAll(item.forward);
					return;
				}
			}

			queue_a.add(item);

		}
	}


	//构造LR(1)
	void construct_LALR()
	{
		//从.y文件中读取文法
		PretreatmentYacc.read(new File(path), products_map, products_list);
		for(Item it:products_list){
			sematic_list.add(it.semantic);
			System.out.println(it.semantic);
		}
		//itemset(I0)
		start_item = new Item( products_list.get(0) );
		start_item.forward.add("$");
		LinkedList<Item> queue = new LinkedList<Item>();
		queue.add(start_item);//将初始LR项加入要扫描的队列

		start_itemSet = closure(queue);//对初始LR项进行拓展
		start_itemSet.id = 0;//设置初始LR项集的ID为0
		int id =1;//用于标记新产生的LR项集的ID
		//queue_LR存储未扫描的LR_1项集
		LinkedList<ItemSet> queue_LR = new LinkedList<ItemSet>();
		queue_LR.add(start_itemSet);//将初始的LR项集加入queue_LR中
		//set_LR存储的是所有的LR_1项集，将初始的LR项集加入set_LR中
		set_LR.add(start_itemSet);

		//扫描queue_LR，构造LR项集
		ItemSet itemset;
		ItemSet new_itemset;
		while((itemset = queue_LR.poll()) != null)
		{
			//若扫描的项集无发出边，则跳过
			if(itemset.edge_set.size() == 0)
			{
				continue;
			}
			//扫描发出边产生新项集
			for(String edge : itemset.edge_set)
			{
				boolean exist = false; //新产生的项集是否已存在
				new_itemset = goTo(itemset,edge);//通过发出边得到下一个项集
				//处理新项集
				for (ItemSet iset : set_LR)
				{
					//若已存在，更新itemset的next_ItemSet
					if(new_itemset.items.equals(iset.items))
					{
						//exist = true;
						/******新加——构造LALR*********/
						 /*合并同核LR项集，若new_itemset中包含的每个item
						   的 forward在iset的对应item的forward中都已经存在， 则不加入
						 queue_LR扫描，否则，将合并的同核LR项集加入queue_LR*/
						if(!iset.MergeCoreforward(new_itemset))
						{
							queue_LR.add(iset);//此时的iset是合并同心之后的项集
						}
						itemset.next_ItemSet.put(edge, iset);
						exist = true;
						break;
					}
				}
				//若不存在，更新itemset的next_ItemSet
				if(!exist)
				{
					queue_LR.add(new_itemset);
					new_itemset.id = id;
					set_LR.add(new_itemset);
					id++;
					itemset.next_ItemSet.put(edge, new_itemset);
				}
			}
		}

	}

	//通过特定edge产生新项集
	private ItemSet goTo(ItemSet itemset, String edge)
	{
		//queue存储将要扫描的Item对象
		LinkedList<Item> queue = new LinkedList<Item>();
		for (Item item : itemset.getItems())
		{   //取可移动的item放入将扫描的queue
			if(item.getCurrent()!= null && item.getCurrent().equals(edge))
			{
				Item new_item = new Item(item);
				new_item.dot++;//new_item的dot移进
				queue.add(new_item);
			}
		}

		return closure(queue);
	}


	//根据item当前下的dot计算forward
	private HashSet<String> getForward(Item item)
	{
		HashSet<String> forward = new HashSet<String>();
		//first(εa)这样的情况
		if(item.dot + 1 == item.right.size())
		{
			forward.addAll(item.forward);
			return forward;
		}

		////否则，求first(βa)
		ArrayList<String> back = new ArrayList<String>();
		for(int c = item.dot + 1; c < item.right.size(); c++)
		{
			back.add(item.right.get(c));
		}
		forward = first(back,0,new HashSet<String>());


		if(forward.contains("")){
			forward.addAll(item.forward);
		}
		return forward;
	}

	private HashSet<String> first(ArrayList<String> back, int index, HashSet<String> scanned_set)
	{	//存储求first得到的符号
		HashSet<String> first_set = new HashSet<String>();

		if(index == back.size())
			return first_set;

		//若当前字符是终结符
		if(!products_map.containsKey(back.get(index)))
		{
			first_set.add(back.get(index)); //直接加入
			return first_set;
		}
		//若当前字符是非终结符,首先将当前符号加入已扫描集
		scanned_set.add(new String(back.get(index)));
		//获取对应非终结符的所有文法产生式
		ArrayList<Item> pros = products_map.get(back.get(index));
		for(int j = 0; j < pros.size(); j++)
		{
			Item item = new Item(pros.get(j));
			//若要扫描的符号已出现在扫描过的集合中，则跳过
			if(item.right.get(0).equals(back.get(index)))
				continue;
			if(scanned_set.contains(item.right.get(0)))
				continue;
			scanned_set.add(back.get(index));
			//并递归求该符号的first
			first_set.addAll(first(item.right,0,scanned_set));
			/*
			if(first_set.contains("") && index + 1 < back.size() )
			{
				first_set.remove("");
				first_set.addAll(first(back,index+1,scanned_set));
			}*/
		}
		return first_set;
	}
	private void createTable(ArrayList<ItemSet> seq_out_Graph,HashMap<ArrayList<String>, Integer> product_HashMap)
	{
		HashMap<String, Integer> priorityHashMap=new HashMap<String, Integer>();
		ArrayList<String> priorityFeature=new ArrayList<String>();
		GetTokenPriority.getPriorityArray(new File(path), priorityHashMap, priorityFeature);
		Set<String> terminalSet=new HashSet<String>();
		Set<String> nonTerminalSet=new HashSet<String>();
		for (ItemSet itemSet : seq_out_Graph)
		{
			for (Item item : itemSet.items)
			{
				for(int i=0;i<item.right.size();i++)
				{
					if(products_map.containsKey(item.right.get(i)))
					{//若是非终结符则加入非终结符集合
						nonTerminalSet.add(item.right.get(i));
					}
					else
					{//否则加入终结符集合
						terminalSet.add(item.right.get(i));

					}
				}

			}
		}
		terminalSet.add("$");
		HashMap<String, Integer> outfitMap=new HashMap<String, Integer>();//表头
		int index=0;
		//存储所有终结符和非终结符
		ArrayList<String> outfitArrayList=new ArrayList<String>();
		Iterator<String> iterator=terminalSet.iterator();
		while(iterator.hasNext())
		{
			String temp=iterator.next();
			System.out.println(index+temp);
			outfitMap.put(temp, index);//将终结符加入到表中，并加上相应的编号
			outfitArrayList.add(temp);
			index++;
		}
		int splitIndex=index;
		iterator=nonTerminalSet.iterator();
		while(iterator.hasNext())
		{
			String temp=iterator.next();
			outfitMap.put(temp, index);
			outfitArrayList.add(temp);
			index++;
		}
		//将预测分析表作为一个二维数组进行存储，行数是项集数，列数是终结符数+非终结符数
		table =new int[seq_out_Graph.size()][outfitArrayList.size()];
		//初始化为全0
		for(int i=0;i<seq_out_Graph.size();i++)
			for(int j=0;j<outfitArrayList.size();j++)
				table[i][j]=0;
		for (ItemSet itemSet : seq_out_Graph)
		{
			for(String str : itemSet.edge_set)
			{
				//如果为非终结符，直接填表
				if(outfitMap.get(str)>=splitIndex)
				{
					table[itemSet.id][outfitMap.get(str)]=itemSet.next_ItemSet.get(str).id+1;
				}
				else if(outfitMap.get(str)<splitIndex&&outfitMap.get(str)>=0)
				{
					for(Item item : itemSet.items)
					{
						//如果该LR项是归约项且它的预测符也包含str的话就有可能产生移进规约冲突，故要对其做相应的处理
						if(item.dot==item.right.size()&&item.forward.contains(str))
						{
							int priorityOprIndexOfThisItem=-1;
							for(int i=item.right.size()-1;i>=0;i--)
							{
								if(outfitMap.get(item.right.get(i))<splitIndex)
									priorityOprIndexOfThisItem=i;
							}
							if(priorityOprIndexOfThisItem!=-1)
							{
								String operatorA=item.right.get(priorityOprIndexOfThisItem);
								String operatorB=str;
								if(operatorA.charAt(0)=='\'')
								{
									operatorA=operatorA.substring(1, operatorA.length()-1);
								}
								if(operatorB.charAt(0)=='\'')
								{
									operatorB=operatorB.substring(1, operatorB.length()-1);
								}
								//A的优先级大于B，该规约
								if(priorityHashMap.get(operatorA)>priorityHashMap.get(operatorB))
								{
									ArrayList<String> productionToBeReduced=new ArrayList<String>();
									productionToBeReduced.add(item.left);
									for(String string:item.right)
									{
										productionToBeReduced.add(string);
									}
									table[itemSet.id][outfitMap.get(str)]=-product_HashMap.get(productionToBeReduced)-1;
								}
								//A的优先级小于B，该移进
								else if(priorityHashMap.get(operatorA)<priorityHashMap.get(operatorB))
								{
									table[itemSet.id][outfitMap.get(str)]=itemSet.getNext_ItemSet().get(str).id+1;
								}
								else
								{
									//左结合规约，填表
									if(priorityFeature.get(priorityHashMap.get(operatorA))=="left")
									{
										ArrayList<String> productionToBeReduced=new ArrayList<String>();
										productionToBeReduced.add(item.left);
										for(String string:item.right)
										{
											productionToBeReduced.add(string);

										}
										table[itemSet.id][outfitMap.get(str)]=-product_HashMap.get(productionToBeReduced)-1;
									}
									//右结合规约，填表
									else if(priorityFeature.get(priorityHashMap.get(operatorA))=="right")
									{
										table[itemSet.id][outfitMap.get(str)]=itemSet.getNext_ItemSet().get(str).id+1;
									}
								}
							}
						}
						else if(table[itemSet.id][outfitMap.get(str)]==0)
						{
							table[itemSet.id][outfitMap.get(str)]=itemSet.next_ItemSet.get(str).id+1;

						}
					}
				}
			}

			for(Item item: itemSet.items)
			{
				for(String string:item.forward)
				{
					if(item.dot==item.right.size()&&table[itemSet.id][outfitMap.get(string)]==0)
					{
						ArrayList<String> productionToBeReduced=new ArrayList<String>();
						productionToBeReduced.add(item.left);
						for(String string1:item.right)
						{
							productionToBeReduced.add(string1);
						}

						table[itemSet.id][outfitMap.get(string)]=-product_HashMap.get(productionToBeReduced)-1;
					}
				}

			}
			//accept状态填入
			for (Item item : itemSet.items)
			{
				if(item.left.equalsIgnoreCase("start")&&item.dot==item.right.size()&&item.forward.contains("$"))
				{
					table[itemSet.id][outfitMap.get("$")]=-1;
				}
			}
		}
		ReadYacc wtf = new ReadYacc(path, "sematic_parser.cpp");
		wtf.getFirstPart();
		GenerateCode GC = new GenerateCode(outfitArrayList,sematic_list,table,seq_out_Graph.size(),products_list);
		GC.setOUTFILE("sematic_parser.cpp");
		GC.createInit();
		GC.createAction();
		wtf.getLastPart();
	}

	public void createPPT()
	{
		HashMap<ArrayList<String>, Integer> product_HashMap=new HashMap<ArrayList<String>, Integer>();
		construct_LALR();
		int count=0;
		//对产生式进行编号
		for(Item item: products_list)
		{
			ArrayList<String> currentProduct=new ArrayList<String>();
			currentProduct.add(item.left);
			for(int i=0;i<item.right.size();i++)
			{
				currentProduct.add(item.right.get(i));
			}
			product_HashMap.put(currentProduct, count);
			count++;
		}

		ArrayList<ItemSet> seq_out_Graph = new ArrayList<ItemSet>();
		for(int i=0;i<set_LR.size();i++)
		{

			for (ItemSet itemSet : set_LR)
			{
				if(itemSet.id==i)
				{
					seq_out_Graph.add(itemSet);
					break;
				}
			}
		}
		createTable(seq_out_Graph, product_HashMap);
	}

	public static void main(String[] args)
	{
		Parser parser = new Parser("./yacc_file/Yacc.y");
		parser.createPPT();

	}
}
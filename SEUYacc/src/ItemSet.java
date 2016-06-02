import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Jch on 2016/5/20.
 */

//LR项集
public class ItemSet
{
	public int id; //LR项集的唯一编号
	public Set<Item> items; //包含的LR项
	public Set<String> edge_set; //可以到达下一项集的‘符号’集合
	public HashMap<String, ItemSet> next_ItemSet; //通过‘符号’到达的‘项集’

	public ItemSet()
	{
		//this.id = id;
		items = new HashSet<Item>();
		next_ItemSet = new HashMap<String, ItemSet>();
		edge_set = new HashSet<String>();
	}

	/******新加――构造LALR*********/
	public boolean MergeCoreforward(ItemSet core_itemset) {
		// TODO Auto-generated method stub
		boolean contains = true;
		for (Item core_item : core_itemset.items)
		{
			for (Item item : this.items)
			{
				if(item.equals(core_item))
				{
					int num = item.forward.size();
					item.forward.addAll(core_item.forward);
					if(num != item.forward.size())
						contains = false;
				}
			}
		}
		return contains;
	}

	public int getId() {
		return id;
	}



	public void setId(int id) {
		this.id = id;
	}



	public Set<Item> getItems() {
		return items;
	}



	public void setItems(Set<Item> items) {
		this.items = items;
	}



	public HashMap<String, ItemSet> getNext_ItemSet() {
		return next_ItemSet;
	}



	public void setNext_ItemSet(HashMap<String, ItemSet> next_ItemSet) {
		this.next_ItemSet = next_ItemSet;
	}

}

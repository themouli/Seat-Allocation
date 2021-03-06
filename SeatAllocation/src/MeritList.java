import java.util.ArrayList;
import java.util.HashMap;

public class MeritList{
	int highest_rank;
	HashMap<Integer,ArrayList<String>> rank_list;
	
	public MeritList(){
		this.highest_rank = 0;
		this.rank_list = new HashMap<Integer,ArrayList<String>>();
	}
	
	void putCandidate(String ID,String rank){
		ArrayList<String> tempArrList;
		int tempRank = Integer.valueOf(rank);
		if(tempRank>this.highest_rank) this.highest_rank = tempRank;
		if(this.rank_list.containsKey(tempRank)){
			tempArrList = this.rank_list.get(rank);
			tempArrList.add(ID);
		}
		else{
			tempArrList = new ArrayList<String>();
			tempArrList.add(ID);
		}
		this.rank_list.put(tempRank, tempArrList);
	}
	
	int getHighestRank(){
		return this.highest_rank;
	}
}
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;


public class GaleShapleyAdmission {
	static String program_tokens[] = {"GE","OBC","SC","ST","GE-PD","OBC-PD","SC-PD","ST-PD"};
	static HashMap<String,VirtualProgramme> allVirtualPrograms= new HashMap<String,VirtualProgramme>();
	static HashMap<String,Candidate> allCandidates = new HashMap<String,Candidate>();
	static HashMap<String,GSMeritList> allRankLists  = new HashMap<String,GSMeritList>();
	private static boolean end = false;
	public static void main(String args[]) throws FileNotFoundException{
		for(int i=0;i<8;i++){
			GSMeritList tempMeritList = new GSMeritList();
			allRankLists.put(program_tokens[i],tempMeritList);
		}
		readPrograms();
		readChoices();
		readRankList();
		iterate();            // Iterate through all the rank lists and allot seats accordingly
	}
	
	private static void readPrograms() throws FileNotFoundException{
        BufferedReader fileReader = null;
        try
        {
            String line = "";
            fileReader = new BufferedReader(new FileReader("programs.csv"));
            line = fileReader.readLine();
            while ((line = fileReader.readLine()) != null)
            {
                String[] tokens = line.split(",");
                for(int i=0;i<program_tokens.length;i++)
                {
                	VirtualProgramme tempVPL = new VirtualProgramme();
                	tempVPL.setQuota(Integer.valueOf(tokens[i+3]));
                	tempVPL.setCategory(program_tokens[i]);
                	allVirtualPrograms.put(tokens[1]+"-"+program_tokens[i],tempVPL);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        } 
        /*Set<String> all_names = allVirtualPrograms.keySet();
        String[] temp_names = all_names.toArray(new String[all_names.size()]);
        for(int i=0;i<allVirtualPrograms.size();i++){
        	System.out.println(temp_names[i]);
        } */
	}
	private static void readChoices(){
		BufferedReader fileReader = null;
		try{
			String line ="";
			fileReader = new BufferedReader(new FileReader("choices.csv"));
            line = fileReader.readLine();
			while((line = fileReader.readLine()) != null){
				String[] tokens = line.split(",");
				Candidate tempCandidate = new Candidate();
				tempCandidate.setID(tokens[0]);
				tempCandidate.setCategory(tokens[1]);
				tempCandidate.setPDStatus(tokens[2]);
				tempCandidate.setPL(tokens[3]);
			/*	for(int i=0;i<tempCandidate.virtual_pl.size();i++){
					System.out.print(tempCandidate.virtual_pl.get(i));
					System.out.print(" ");
				}
				System.out.println(); */
				allCandidates.put(tokens[0],tempCandidate);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	private static void readRankList(){
		BufferedReader fileReader = null;
		try{
			String line ="";
			fileReader = new BufferedReader(new FileReader("ranklist.csv"));
            line = fileReader.readLine();
			while((line = fileReader.readLine()) != null){
				String[] tokens = line.split(",");
				Candidate candidate = allCandidates.get(tokens[0]);
				String candidate_category = candidate.getCategory().toLowerCase();
				candidate.ge_rank = Integer.valueOf(tokens[3]);
				candidate.ge_pd_rank = Integer.valueOf(tokens[8]);
				switch (candidate_category){
					case "obc" : candidate.cat_rank = Integer.valueOf(tokens[4]);
								 candidate.cat_pd_rank = Integer.valueOf(tokens[9]);
								 break;
					case "sc" : candidate.cat_rank = Integer.valueOf(tokens[5]);
					 			 candidate.cat_pd_rank = Integer.valueOf(tokens[10]);
					 			 break;
					case "st" : candidate.cat_rank = Integer.valueOf(tokens[6]);
					 			 candidate.cat_pd_rank = Integer.valueOf(tokens[11]);
					 			 break;
				}
				candidate.makeActualVPL();
				allCandidates.put(candidate.getID(), candidate);
				for(int i=0;i<4;i++){
					GSMeritList tempMeritList = allRankLists.get(program_tokens[i]);
					if(Integer.valueOf(tokens[i+3])>=0){
						tempMeritList.putCandidate(tokens[0], tokens[i+3]);
						allRankLists.put(program_tokens[i], tempMeritList);
					}
				}
				for(int i=4;i<8;i++){
					GSMeritList tempMeritList = allRankLists.get(program_tokens[i]);
					if(Integer.valueOf(tokens[i+3])>=0){
						tempMeritList.putCandidate(tokens[0], tokens[i+4]);
						allRankLists.put(program_tokens[i], tempMeritList);
					}
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	public static void iterate(){	
		int N = allCandidates.size();
		Set<String> temp_candidates_id = allCandidates.keySet();
		String[] candidates_id = temp_candidates_id.toArray(new String[temp_candidates_id.size()]);
		while(!end){
			end = true;
			for(int n=0;n<N;n++){
				Candidate candidate = allCandidates.get(candidates_id[n]);
				if(candidate.getAllotedVP() == -1 && candidate.getNextVP()<candidate.virtual_pl.size() && candidate.virtual_pl.size()!=0){
					int next_vp = candidate.getNextVP();
					candidate.current_vp = next_vp;
					VirtualProgramme virtual_programme = allVirtualPrograms.get(candidate.virtual_pl.get(next_vp));
					virtual_programme.addCandidateToAppliedList(candidate);
					allVirtualPrograms.put(candidate.virtual_pl.get(next_vp), virtual_programme);
					candidate.next_vp++;
					allCandidates.put(candidate.getID(), candidate);
				}
				if(end==true){
					if(candidate.getNextVP()>candidate.virtual_pl.size()-1 || candidate.alloted_vp != -1) end=true;
					else end=false;
				}
			}
			Set<String> temp_vpl_names = allVirtualPrograms.keySet();
			String[] vpl_names =  temp_vpl_names.toArray(new String[temp_vpl_names.size()]);
			int K = vpl_names.length;
			for(int k=0;k<K;k++){
				VirtualProgramme virtualProgramme = allVirtualPrograms.get(vpl_names[k]);
				ArrayList<Candidate> applied_candidates = virtualProgramme.getAppliedList();
				applied_candidates.addAll(virtualProgramme.wait_list);
				virtualProgramme.wait_list = new ArrayList<Candidate>();
				int P = applied_candidates.size();
				if(applied_candidates.size()<virtualProgramme.quota){
					for(int i=0;i<applied_candidates.size();i++){
						virtualProgramme.addCadidateToWaitList(applied_candidates.get(i));
						String id = applied_candidates.get(i).getID();
						Candidate candidate = allCandidates.get(id);
						candidate.alloted_vp = candidate.current_vp;
						allCandidates.put(id, candidate);
					}
				}
				else{
					ArrayList<Candidate> sorted_candidates = sortCandidates(applied_candidates,virtualProgramme.getCategory());
					for(int i=0;i<virtualProgramme.quota;i++){                                    //adding the top q candidates of the sorted applied list
						virtualProgramme.addCadidateToWaitList(sorted_candidates.get(i));
					}
					int q = virtualProgramme.quota;
					int last_added = q-1;
					int last_rank = applied_candidates.get(last_added).ge_rank;
					for(int i=q;i<applied_candidates.size();i++){                                //taking care of same rank as last added but no seat left case
						if(applied_candidates.get(i).ge_rank==last_rank){
							virtualProgramme.addCadidateToWaitList(sorted_candidates.get(i));
							last_added++;
						}
						else break;
					}
					for(int i=0;i<=last_added;i++){
						Candidate candidate = sorted_candidates.get(i);
						candidate.alloted_vp = candidate.current_vp;
						allCandidates.put(candidate.getID(), candidate);
					}
					for(int i=last_added+1;i<sorted_candidates.size();i++){
						Candidate candidate = sorted_candidates.get(i);
						candidate.alloted_vp = -1;
						allCandidates.put(candidate.getID(), candidate);
					}
				}
				virtualProgramme.applied_list = new ArrayList<Candidate>();                                             //clearing the applied list after filtering 
				allVirtualPrograms.put(vpl_names[k], virtualProgramme);
			}
		}
		for(String id : candidates_id){
			Candidate candidate = allCandidates.get(id);
			if(candidate.alloted_vp!=-1)System.out.println(candidate.getID()+" "+candidate.virtual_pl.get(candidate.alloted_vp));
		}
	}
	
	public static ArrayList<Candidate> sortCandidates(ArrayList<Candidate> applied_candidates,String merit_list_name){
		int q = applied_candidates.size();
		ArrayList<Candidate> result = new ArrayList<Candidate>();
		GSMeritList merit_list = allRankLists.get(merit_list_name);
		Collections.sort(applied_candidates, new CandidateComparator(merit_list_name));
		for(int i=0;i<q;i++){
			result.add(applied_candidates.get(i));
		}
		return result;
	}
	
	public void reject(ArrayList<Candidate> reject_list){
		for(Candidate candidate : reject_list){
			candidate.alloted_vp = -1;
		}
	}
	public static class CandidateComparator implements Comparator<Candidate>{
		String merit_list_name;
		GSMeritList merit_list;
		public CandidateComparator(String merit_list_name){
			this.merit_list_name = merit_list_name;
			merit_list = allRankLists.get(merit_list_name);
		}
	    @Override
	    public int compare(Candidate c1, Candidate c2) {
	    	int rank1 = merit_list.getRank(c1.getID());
	    	int rank2 = merit_list.getRank(c2.getID());
    		if(rank1 > rank2){
	            return 1;
	        } else {
	            return -1;
	        }
	    }
	}
}

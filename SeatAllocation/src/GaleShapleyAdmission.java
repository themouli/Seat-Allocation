import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

public class GaleShapleyAdmission{
	String program_tokens[] = {"GE","OBC","SC","ST","GE-PD","OBC-PD","SC-PD","ST-PD"};
	HashMap<String,VirtualProgramme> allVirtualPrograms= new HashMap<String,VirtualProgramme>();
	HashMap<String,Candidate> allCandidateChoices = new HashMap<String,Candidate>();
	HashMap<String,MeritList> allRankLists  = new HashMap<String,MeritList>();
	public void main(String args[]) throws FileNotFoundException{
		for(int i=0;i<8;i++){
			MeritList tempMeritList = new MeritList();
			allRankLists.put(program_tokens[i],tempMeritList);
		}
		readPrograms();
		readChoices();
		readRankList();
	}
	
	private void readPrograms() throws FileNotFoundException{
        BufferedReader fileReader = null;
        try
        {
            String line = "";
            fileReader = new BufferedReader(new FileReader("programs.csv"));
            while ((line = fileReader.readLine()) != null)
            {
                String[] tokens = line.split(",");
                for(int i=0;i<tokens.length;i++)
                {
                	VirtualProgramme tempVPL = new VirtualProgramme();
                	tempVPL.setQuota(Integer.valueOf(tokens[i+2]));
                	tempVPL.setCategory(program_tokens[i]);
                	allVirtualPrograms.put(tokens[0]+program_tokens[i],tempVPL);
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        } 
	}
	private void readChoices(){
		BufferedReader fileReader = null;
		try{
			String line ="";
			fileReader = new BufferedReader(new FileReader("choices.csv"));
			while((line = fileReader.readLine()) != null){
				String[] tokens = line.split(",");
				Candidate tempCandidate = new Candidate();
				tempCandidate.setID(tokens[0]);
				tempCandidate.setCategory(tokens[1]);
				tempCandidate.setPDStatus(tokens[2]);
				tempCandidate.setPL(tokens[3]);
				tempCandidate.makeVPL();
				allCandidateChoices.put(tokens[0],tempCandidate);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	private void readRankList(){
		BufferedReader fileReader = null;
		try{
			String line ="";
			fileReader = new BufferedReader(new FileReader("ranklist.csv"));
			while((line = fileReader.readLine()) != null){
				String[] tokens = line.split(",");
				for(int i=0;i<4;i++){
					MeritList tempMeritList = allRankLists.get(program_tokens[i]);
					if(tokens[i+3]!="0"){
						tempMeritList.putCandidate(tokens[0], tokens[i+3]);
						allRankLists.put(program_tokens[i], tempMeritList);
					}
				}
				for(int i=4;i<8;i++){
					MeritList tempMeritList = allRankLists.get(program_tokens[i]);
					if(tokens[i+4]!="0"){
						tempMeritList.putCandidate(tokens[0], tokens[i+3]);
						allRankLists.put(program_tokens[i], tempMeritList);
					}
				}
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}
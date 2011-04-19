import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import au.com.bytecode.opencsv.CSVReader;

public class rulesParser {
	
	HashMap<Integer, HashMap<String,String>> RULES = new HashMap<Integer, HashMap<String,String>>();
	private String[] fields = {"action","protocol","ip/mask_A","port1_&_port2_A","<->","ip/mask_B","port1_&_port2_B"};
	
	public rulesParser(String rules) throws IOException{
		
		CSVReader reader = new CSVReader(new FileReader("Files/Data/"+rules), ' ');
		
		String [] nextLine;
		int rule_num = 0;
		
		while ((nextLine = reader.readNext()) != null) {
			if(nextLine.length<8){
				System.out.println("Problem parsing rule " + (rule_num+1) + ". Not enought parameters specified!");
				System.exit(1);
			}
			RULES.put(new Integer(rule_num), new HashMap<String,String>());
			
			for(int i = 0; i<7; i++){
				RULES.get(rule_num).put(fields[i], nextLine[i]);
			}
			
			String options = nextLine[7];
			
			options = options.replace(")", "");
			options = options.replace("(", "");
			
			StringTokenizer options_t = new StringTokenizer(options);
			
			while (options_t.hasMoreElements()){
			      options_t.nextToken();
			      List<String> option_list = Arrays.asList(options.split(";"));
			      for(int i = 0; i<option_list.size();i++){
			    	  String opt = option_list.get(i).split(":")[0];
			    	  String val = option_list.get(i).split(":")[1];
			    	  RULES.get(rule_num).put(opt, val);
			      }
			}
			rule_num++;
		}
	}
	
	public void print_rules(){
		for(int i =0; i<RULES.size();i++){
			System.out.println("Rule : " + i);
			for(String keys: RULES.get(i).keySet()){
				System.out.println("\t" + keys + " : "+RULES.get(i).get(keys) );
			}
		}
		System.out.println();
	}
	/*public static void main(String[] argv) throws IOException {
		new rulesParser("Project3/rules.txt");
	}*/
}


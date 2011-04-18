/*import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import au.com.bytecode.opencsv.CSVReader;

public class rulesParser {
	
	ArrayList <String[]> allRules = new ArrayList<String[]>();
	
	public rulesParser(String rules) throws IOException{
		CSVReader reader = new CSVReader(new FileReader("Files/Data/"+rules), ' ');
		
		String [] nextLine;
		
		while ((nextLine = reader.readNext()) != null) {
	        allRules.add(nextLine);
	    }
		
		for(int m = 0; m< allRules.size();m++){
			String[] temp = allRules.get(m);
			for(String values: temp){
				System.out.print(values + " ");
			}
			System.out.println();
		}
	}
}
*/
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.StringTokenizer;

import au.com.bytecode.opencsv.CSVReader;

public class rulesParser {
	
	HashMap<String,String> OPTIONS = new HashMap<String,String>();
	HashMap<Integer, HashMap<String,String>> RULES = new HashMap<Integer, HashMap<String,String>>();
	private String[] fields = {"action","protocol","ip/mask_A","port1_&_port2_A","<->","ip/mask_B","port1_&_port2_B"};
	
	public rulesParser(String rules) throws IOException{
		
		CSVReader reader = new CSVReader(new FileReader("Files/Data/"+rules), ' ');
		
		String [] nextLine;
		int rule_num = 0;
		
		while ((nextLine = reader.readNext()) != null) {
			
			for(int i = 0; i<7; i++){
				OPTIONS.put(fields[i], nextLine[i]);
				RULES.put(rule_num, OPTIONS);
			}
			
			ArrayList <String> opts = new ArrayList<String>();
			
			String options = nextLine[7];
			
			options = options.replace(")", "");
			options = options.replace("(", "");
			
			StringTokenizer options_t = new StringTokenizer(options);
			
		/*	while (options_t.hasMoreElements()){
			      //System.out.println(options_t.nextToken());
			      opts.addAll(options.split(";"));
			      System.out.println(Arrays.asList(options.split(";")));
			      
			      for(int i = 0; i<)
			  }
			
			rule_num++;
			OPTIONS.clear();*/
	    }
		
		/*for(int m = 0; m< allRules.size();m++){
			String[] temp = allRules.get(m);
			for(String values: temp){
				System.out.print(values + " ");
			}
			System.out.println();
		}*/
	}
	public static void main(String[] argv) throws IOException {
		new rulesParser("Project3/rules.txt");
	}
}


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import au.com.bytecode.opencsv.CSVReader;

public class rulesParser {
	public rulesParser(String rules) throws IOException{
		CSVReader reader = new CSVReader(new FileReader("Files/Data/"+rules), ' ');
		ArrayList <String[]> allRules = new ArrayList<String[]>();
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

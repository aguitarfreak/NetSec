import java.io.*;
import java.util.StringTokenizer;

public class fileReader extends methods {
	String temp="";
	byte[][] arrayOfAob;
	int packetCount=0;
	int i=0;
	public fileReader(String filename){
		try {
		    BufferedReader in = new BufferedReader(new FileReader("Files/Data/"+filename));
		    String str;
		    
		    while ((str = in.readLine()) != null) {
		        temp+=str.replaceAll(" ","");
		        
		        if(str.equals("")){
		        	//packetCount++;
		        	temp+=(" ");
		        }
		    }
		    in.close();
		    //packetCount--;//to count for the last empy line
		    
		} catch (IOException e) {
			System.out.println("There was a problem:" + e);
		}
		temp=temp.trim();
		
		//count whitespaces
		StringTokenizer st = new StringTokenizer(temp, " ");
		packetCount = st.countTokens();
		arrayOfAob = new byte[packetCount][];
		
		while(st.hasMoreTokens()){
			String packet = st.nextToken();
			arrayOfAob[i] = stringToByte(packet);
			i++;
		}
	}
}

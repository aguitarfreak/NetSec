import java.io.FileNotFoundException;
import java.io.IOException;


public class ids {
	
	//filemode
	public ids(String rf, String df) throws IOException{
		rulesParser rp = new rulesParser(rf);
	}
	
	//network mode
	public ids(String rf) {
		
	}
	
	public static void main(String[] argv) throws IOException {
		String rule_filename="";
		String data_filename="";
		boolean networkmode = false;
		
		try{
			rule_filename = argv[0];
			if(argv.length==2){
				data_filename = argv[1];
				System.out.println("filemode!");
			}
			else if(argv.length==1){
				networkmode = true;
				System.out.println("network mode!");
			}
			else{
				System.out.println("USAGE: filemode: rule_filename " + " data_filename " );
				System.out.println("USAGE: networkmode: rule_filename ");
				System.exit(1);
			}
		}catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("USAGE: filemode: rule_filename " + " data_filename " );
			System.out.println("USAGE: networkmode: rule_filename ");
			System.exit(1);
		}
		
		if(!networkmode) new ids(rule_filename,data_filename);
		else new ids(rule_filename);
		
	}

}

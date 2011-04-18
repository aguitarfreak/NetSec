import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class ids {
	
	//filemode
	public ids(String rf, String df) throws IOException{
		rulesParser rp = new rulesParser(rf);
		
		//System.out.println(rp.allRules.get(0)[7]);
	}
	
	//network mode
	public ids(String rf) {
		
	}
	
	public static void main(String[] argv) throws IOException {
		String rule_filename="";
		String data_filename="";
		boolean networkmode = true;
		
		OptionParser parser = new OptionParser() {
            {
                accepts( "r" ).withRequiredArg().ofType( String.class )
                    .describedAs( "rule_filename" );
                accepts( "i" ).withRequiredArg().ofType( String.class )
                    .describedAs( "input" );
                acceptsAll( Arrays.asList( "h", "?" ), "show help" );
            }
        };

        OptionSet options = parser.parse( argv );

        if ( options.has( "?" ) )
            parser.printHelpOn( System.out );
        
        if ( options.has( "r" ) )
            rule_filename = (String) options.valueOf("r");
        
        if ( options.has( "i" ) ){
            data_filename = (String) options.valueOf("i");
            networkmode = false;
        }
        
        if(!networkmode) new ids(rule_filename,data_filename);
		else new ids(rule_filename);
        
    }
}		
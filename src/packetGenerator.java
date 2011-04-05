import static java.util.Arrays.asList;

import java.io.IOException;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class packetGenerator extends methods {
	
	static OptionSet options;
	static String infilename;
	static String adapter = "eth0";
	static int j = 0 , packet=0;
	
	static byte[][] arrayOfArrayOfBytes;
	
	public static void main(String[] args) throws IOException, InterruptedException{
		OptionParser parser = new OptionParser() {
	        {
	            accepts( "f" ).withRequiredArg().ofType( String.class )
	                .describedAs( "Packet File" );
	            
	            acceptsAll( asList( "help", "?" ), "show help" );
	        }
	    };
	    
	    options = parser.parse( args );
	    
	    
	    if ( options.has( "?" ) )
	        parser.printHelpOn( System.out );
	    
	    if ( options.has( "f" ) ){
	    	infilename = options.valueOf("f").toString();
	    	System.out.println("Reading Packets from file : "+ infilename);
	    	
	    	arrayOfArrayOfBytes = read_from_file(infilename);
	    	
	    	String[] adapters=driver.getAdapterNames();
	        System.out.println("Number of adapters: "+adapters.length);
	        for (int i=0; i< adapters.length; i++) System.out.println("Device name in Java ="+adapters[i]);
		//Open first found adapter (usually first Ethernet card found)
	        if (driver.openAdapter(adapters[0])) System.out.println("Adapter is open: "+adapters[0]);
	    	
	        //while(j<100){
		        while(packet<100){
		        	System.out.print("sending packet :"+packet+"|");//+ driver.byteArrayToString(arrayOfArrayOfBytes[packet]));
		    		send_to_network(arrayOfArrayOfBytes[packet]);
		    		packet++;
		        }
	        //packet = 0;
	        //j++;
	        //System.out.println();
	        //}
	    }
	}
}

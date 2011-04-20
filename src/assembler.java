import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import joptsimple.OptionParser;
import joptsimple.OptionSet;

import org.apache.commons.collections.map.MultiValueMap;

public class assembler extends methods {
	public static ArrayList<byte[]> one_packet = new ArrayList<byte[]>();//get one packet
	public static ArrayList<byte[]> all_packets = new ArrayList<byte[]>();//store all packets 
	public static ArrayList<Thread> threads = new ArrayList<Thread>();//all working threads, named after the packet ID
	public static ArrayList<String> working_on = new ArrayList<String>();//all working threads names
	public static ArrayList<String> complete = new ArrayList<String>();//all working threads names
	public static ArrayList<String> analyzed = new ArrayList<String>();//all analyzed packets/ threads names
	
	public static MultiValueMap fragments_dict = new MultiValueMap( );//fragments mapped to the threads
	public static HashMap<String, ArrayList<byte[]>>completed_fragments = new HashMap<String, ArrayList<byte[]>>();//completed packet fragments mapped to the ID's
	public static HashMap<String,byte[]> reassembled_packets = new HashMap<String,byte[]>();//store only reassembled packets
	
	public static HashMap<String,Integer> sid = new HashMap<String,Integer>();//Unique Identifier
	public static HashMap<String,byte[]> arp_packets = new HashMap<String,byte[]>();//for arp packets
	
	public static final int ethernet_header_length = 14;
	public static final int ip_header_length = 20;
	public static final int arp_header_length = 28;
	
	public static boolean network = true;
	
	static String rule_filename="";
	static String data_filename="";
	static int packet_count = 5;
	
	public static void main(String[] args) throws InterruptedException, IOException {
		
		OptionParser parser = new OptionParser() {
            {
                accepts( "r" ).withRequiredArg().ofType( String.class )
                    .describedAs( "rule_filename" );
                accepts( "i" ).withRequiredArg().ofType( String.class )
                    .describedAs( "input" );
                accepts( "c" ).withOptionalArg().ofType( Integer.class )
                	.describedAs( "packet_count" ).defaultsTo(5);
                acceptsAll( Arrays.asList( "h", "?" ), "show help" );
            }
        };

        OptionSet options = parser.parse( args );

        if ( options.has( "?" ) )
            parser.printHelpOn( System.out );
        
        if ( options.has( "r" ) )
            rule_filename = (String) options.valueOf("r");
        
        if ( options.has( "i" ) ){
            data_filename = (String) options.valueOf("i");
            network = false;
        }
        
        if ( options.has( "c" ) ){
            packet_count = (Integer) options.valueOf("c");
            
        }
		if(network){
			//new ids(rule_filename);
			//start network sniffer
			networkSniffer netSniffer = new networkSniffer(packet_count);
			Thread snifferThread = new Thread(netSniffer);
			snifferThread.start();

			//start packet assembler
			Runnable assemble = new fragmentAssembleThread(netSniffer);
			Thread assembleThread = new Thread(assemble);
			assembleThread.start();
			
			//start ids
			Runnable ids = new ids(rule_filename);
			Thread idsThread = new Thread(ids);
			idsThread.start();
			
			//this checks if the thread is done and removes it from the thread arraylist
			while(assembleThread.isAlive()){
				Thread.sleep(1000);
				check_threads();
				//final_output();
			}
			//final_output();
		}
		
		else{
			//new ids(rule_filename,data_filename);
			//start file sniffer
			fileSniffer FSniffer = new fileSniffer(data_filename);
			Thread snifferThread = new Thread(FSniffer);
			snifferThread.start();
			
			//start packet assembler
			Runnable assemble = new fragmentAssembleThread(FSniffer);
			Thread assembleThread = new Thread(assemble);
			assembleThread.start();
			
			//start ids
			Runnable ids = new ids(rule_filename);
			Thread idsThread = new Thread(ids);
			idsThread.start();
			
			//this checks if the thread is done and removes it from the thread arraylist
			while(assembleThread.isAlive()|| !working_on.isEmpty()){
				Thread.sleep(1000);
				check_threads();
				//final_output();
			}
			
		}
		//final_output();
		
		/*System.out.println("ip reassem :"+reassembled_packets.toString());
		System.out.println("arp :"+arp_packets.keySet().toString());
		System.out.println("analyzed : "+analyzed.toString());
		System.out.println(completed_fragments.toString());
		System.out.println("working on:"+ working_on.toString());*/
	}
	
	public static void check_threads(){
		for (int j= 0; j<threads.size(); j++){
         	if(!threads.get(j).isAlive()){
         		threads.remove(j);
         	}
        }
	}
	
	public static void final_output(){
		//this array "complete" has the threads that have completed and is the key for the completed packets
		for(Object key : complete.toArray()){
			print_divider();
			System.out.print("Packet : "+ key);
			if(sid.get(key)==1 || sid.get(key)==2){
				
				System.out.print(" , Sid : "+sid.get(key));
				System.out.println(" , Reassembled Packet : ");
				System.out.println(driver.byteArrayToString(reassembled_packets.get(key)));
				
				int x = 0;
				for(byte[] frags : completed_fragments.get(key)){
					System.out.print("Fragment "+ x +" : ");
					System.out.println(driver.byteArrayToString(frags));
					x++;
				}
				print_divider();
			}
			else if(sid.get(key)==3){
				System.out.print(" , Sid : "+3);
				System.out.println(" , First Segment : ");
				System.out.println(driver.byteArrayToString(reassembled_packets.get(key)));
				
				int x = 0;
				for(byte[] frags : completed_fragments.get(key)){
					System.out.print("Fragment "+ x +" : ");
					System.out.println(driver.byteArrayToString(frags));
					x++;
				}
				print_divider();
			}
			else if(sid.get(key)==4){
				System.out.print(" , Sid : "+4);
				System.out.println(" , First Segment : ");
				System.out.println(driver.byteArrayToString(reassembled_packets.get(key)));
				
				int x = 0;
				for(byte[] frags : completed_fragments.get(key)){
					System.out.println("Partially Processed Fragment "+ x +" : ");
					System.out.println(driver.byteArrayToString(frags));
					x++;
				}
				
				print_divider();
			}
			else if(sid.get(key)==5){
				System.out.print(" , Sid : "+5);
				System.out.println(" , Unfragmented Packet : ");
				System.out.println(driver.byteArrayToString(reassembled_packets.get(key)));
				
				print_divider();
			}
			else if(sid.get(key)==6){
				System.out.print(" , Sid : "+6);
				System.out.println(" , Teardrop Attack  ");
				System.out.println("Partially Reassembled Packet : ");
				System.out.println(driver.byteArrayToString(reassembled_packets.get(key)));
				
				int x = 0;
				for(byte[] frags : completed_fragments.get(key)){
					System.out.println("Partially Processed Fragment "+ x +" : ");
					System.out.println(driver.byteArrayToString(frags));
					x++;
				}
				
				print_divider();
			}
			System.out.println();
		}
		
		for(Object key : arp_packets.keySet()){
			print_divider();
			System.out.print("Packet : " + key + " , Sid : "+0);
			System.out.println(" , ARP Packet : ");
			System.out.println(driver.byteArrayToString(arp_packets.get(key)));
			
			System.out.print("Packet List : ");
			System.out.println(driver.byteArrayToString(arp_packets.get(key)));
			
			print_divider();
		}
	}
}
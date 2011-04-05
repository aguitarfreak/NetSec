import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.collections.map.MultiValueMap;

public class assembler extends methods {
	public static ArrayList<byte[]> one_packet = new ArrayList<byte[]>();//get one packet
	public static ArrayList<byte[]> all_packets = new ArrayList<byte[]>();//store all packets 
	public static ArrayList<Thread> threads = new ArrayList<Thread>();//all working threads, named after the packet ID
	public static ArrayList<String> working_on = new ArrayList<String>();//all working threads names
	public static ArrayList<String> complete = new ArrayList<String>();//all working threads names
	
	public static MultiValueMap fragments_dict = new MultiValueMap( );//fragments mapped to the threads
	public static HashMap<String, ArrayList<byte[]>>completed_fragments = new HashMap<String, ArrayList<byte[]>>();//completed packet fragments mapped to the ID's
	public static HashMap<String,byte[]> reassembled_packets = new HashMap<String,byte[]>();//store only reassembled packets
	
	public static HashMap<String,Integer> sid = new HashMap<String,Integer>();//Unique Identifier
	public static HashMap<String,byte[]> arp_packets = new HashMap<String,byte[]>();//for arp packets
	
	public static final int ethernet_header_length = 14;
	public static final int ip_header_length = 20;
	public static final int arp_header_length = 28;
	
	public static boolean network = false;
	
	public static void main(String[] args) throws InterruptedException, IOException {
		
		if(network){
			//start network sniffer
			networkSniffer netSniffer = new networkSniffer(10000);
			Thread snifferThread = new Thread(netSniffer);
			snifferThread.start();

			//start packet assembler
			Runnable assemble = new fragmentAssembleThread(netSniffer);
			Thread assembleThread = new Thread(assemble);
			assembleThread.start();
			
			//this checks if the thread is done and removes it from the thread arraylist
			while(assembleThread.isAlive()){
				Thread.sleep(1000);
				check_threads();
				final_output();
			}
			//final_output();
		}
		
		else{
			//start file sniffer
			fileSniffer FSniffer = new fileSniffer("Project2/test.dat");
			Thread snifferThread = new Thread(FSniffer);
			snifferThread.start();
			
			//start packet assembler
			Runnable assemble = new fragmentAssembleThread(FSniffer);
			Thread assembleThread = new Thread(assemble);
			assembleThread.start();
			
			//this checks if the thread is done and removes it from the thread arraylist
			while(assembleThread.isAlive()){
				Thread.sleep(1000);
				check_threads();
				final_output();
			}
		}
	}
	
	public static void check_threads(){
		for (int j= 0; j<threads.size(); j++){
         	if(!threads.get(j).isAlive()){
         		threads.remove(j);
         	}
        }
	}
	
	public static void final_output(){
		//this array "complete" only holds ip packets
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
					System.out.print("Partially Processed Fragment "+ x +" : ");
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
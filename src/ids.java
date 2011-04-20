import java.io.File;
import java.io.IOException;


public class ids extends assembler implements Runnable{
	rulesParser rp;
	int count_analyzed = 0;
	boolean verbose = false;
	
	public ids(String rf) throws IOException {
		rp = new rulesParser(rf);
	}

	@Override
	public void run() {
		 try { 
	            while ( count_analyzed<packet_count ) {
	            	analyze();
	            	Thread.sleep(10L);
	            } 
	        }  
        catch( InterruptedException e )  {
			e.printStackTrace();
		}
	}
	
	synchronized void analyze(){
		
		for(Object key : complete.toArray()){
			if(analyzed.contains(key)){
				if(verbose)System.out.println("already analyzed packet :"+key);
			}else{
				//do stuff here
				
				byte[] packet_to_analyze = reassembled_packets.get(key);
				
				if(verbose)System.out.print(key +" |eth|");
				ethernet E = new ethernet(packet_to_analyze);
				
				if(E.packet_type=="ip"){
					if(verbose)System.out.print("ip|");
					ip I = new ip(packet_to_analyze);
					//I.pretty_print();
					
					if(I.protocol=="tcp"){
						if(verbose)System.out.print("tcp|");
						tcp T = new tcp(packet_to_analyze);
						//T.pretty_print();
						
					}else if(I.protocol=="udp"){
						if(verbose)System.out.print("udp|");
						udp U = new udp(packet_to_analyze);
						//U.pretty_print();
						
					}else if(I.protocol=="icmp"){
						if(verbose)System.out.print("icmp|");
						icmp IC = new icmp(packet_to_analyze);
						//IC.pretty_print();
					}
				}
				if(verbose)System.out.println();
				analyzed.add((String) key);
				count_analyzed++;
			}
		}
		
		for(Object key : arp_packets.keySet()){
			if(analyzed.contains(key)){
				if(verbose)System.out.println("already analyzed packet :"+key);
			}else{
				byte[] packet_to_analyze =  arp_packets.get(key);
				arp A = new arp(packet_to_analyze);
				analyzed.add((String) key);
				count_analyzed++;
			}
		}
	}
}		
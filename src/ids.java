import java.io.File;
import java.io.IOException;
import java.util.HashMap;


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
				check_rules(reassembled_packets.get(key),key);
			}
		}
		
		for(Object key : arp_packets.keySet()){
			if(analyzed.contains(key)){
				if(verbose)System.out.println("already analyzed packet :"+key);
			}else{
				check_rules(arp_packets.get(key),key);
			}
		}
	}
	
	synchronized void check_rules(byte[] packet_to_analyze, Object k){
		ethernet E = new ethernet(packet_to_analyze);
		String eth_type = "";
		String ip_type = "";
		
		if(E.packet_type=="ip"){
			ip I = new ip(packet_to_analyze);
			eth_type = "ip";
			I.pretty_print();
			
			if(I.protocol=="tcp"){
				tcp T = new tcp(packet_to_analyze);
				ip_type = "tcp";
				//T.pretty_print();
				
			}else if(I.protocol=="udp"){
				udp U = new udp(packet_to_analyze);
				ip_type = "udp";
				U.pretty_print();
				
			}else if(I.protocol=="icmp"){
				icmp IC = new icmp(packet_to_analyze);
				ip_type = "icmp";
				IC.pretty_print();
			}
		}
		
		else if(E.packet_type=="arp"){
			arp A = new arp(packet_to_analyze);
			//A.pretty_print();
			eth_type = "arp";
		}
		
		for(int i =0; i<rp.RULES.size();i++){
			HashMap<String,String> currRule = rp.RULES.get(i);
			
			//for ip
			if((eth_type.equals("ip")&&currRule.get("protocol").equals("ip"))&&
					(currRule.get("action").equals("alert"))){
				if(currRule.get("ip/mask_A").equals("any")){
					if(currRule.get("port1_&_port2_A").equals("any")){
							if(currRule.get("<->").equals("->")){
								
							}else if(currRule.get("<->").equals("<>")){
								
							}
					}
				}
			}
			//for tcp
			else if((ip_type.equals("tcp")&&currRule.get("protocol").equals("tcp"))&&
					(currRule.get("action").equals("alert"))){
				if(currRule.get("ip/mask_A").equals("any")){
					if(currRule.get("port1_&_port2_A").equals("any")){
							if(currRule.get("<->").equals("->")){
								
							}else if(currRule.get("<->").equals("<>")){
								
							}
					}
				}
			}
			//for udp
			else if((ip_type.equals("udp")&&currRule.get("protocol").equals("udp"))&&
					(currRule.get("action").equals("alert"))){
				if(currRule.get("ip/mask_A").equals("any")){
					if(currRule.get("port1_&_port2_A").equals("any")){
							if(currRule.get("<->").equals("->")){
								
							}else if(currRule.get("<->").equals("<>")){
								
							}
					}
				}
			}
			//for icmp
			else if((ip_type.equals("icmp")&&currRule.get("protocol").equals("icmp"))&&
					(currRule.get("action").equals("alert"))){
				if(currRule.get("ip/mask_A").equals("any")){
					if(currRule.get("port1_&_port2_A").equals("any")){
							if(currRule.get("<->").equals("->")){
								
							}else if(currRule.get("<->").equals("<>")){
								
							}
					}
				}
			}
			else if((eth_type.equals("arp")&&currRule.get("protocol").equals("arp"))&&
					(currRule.get("action").equals("alert"))){
				if(currRule.get("ip/mask_A").equals("any")){
					if(currRule.get("port1_&_port2_A").equals("any")){
							if(currRule.get("<->").equals("->")){
								
							}else if(currRule.get("<->").equals("<>")){
								
							}
					}
				}
			}
			
		}
		
		analyzed.add((String) k);
		count_analyzed++;
	}
}		
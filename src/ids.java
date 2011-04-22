import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


public class ids extends assembler implements Runnable{
	rulesParser rp;
	int count_analyzed = 0;
	boolean verbose = false;
	
	String eth_type = "";
	String ip_type = "";
	
	String ip_mask_A = "";
	int port_A = 0;
	String ip_mask_B = "";
	int port_B = 0;
	
	String time_stamp, id, ttl, tos, fragoffset, fragbits, flags, seq, ack, itype, icode;
	boolean sameip;
	boolean print_sid_rule =false; //rule num
	boolean logtofile = false;
	
	StringBuilder sb;
	BufferedWriter logwriter;
	
	DateUtils filestamp = new DateUtils();//for filename
	String fileformat = "_MMM_d_h_mm_ss";
	String file_stamp;
	
	public ids(String rf) throws IOException {
		rp = new rulesParser(rf);
		file_stamp = filestamp.now(fileformat);
	}

	@Override
	public void run() {
		 try { 
	            while ( count_analyzed<packet_count ) {
	            	analyze();
	            	Thread.sleep(100L);
	            } 
	        }  
        catch( InterruptedException e )  {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	synchronized void analyze() throws IOException{
		
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
	
	synchronized void check_rules(byte[] packet_to_analyze, Object k) throws IOException{
		ethernet E = new ethernet(packet_to_analyze);
		ip I=null; tcp T; udp U; icmp IC; arp A;
		id = k.toString();
		
		eth_type = "";
		ip_type = "";
		
		ip_mask_A = "";
		port_A = 0;
		ip_mask_B = "";
		port_B = 0;
		
		time_stamp = "";
		
		if(E.packet_type=="ip"){
			I = new ip(packet_to_analyze);
			eth_type = "ip";
			ip_mask_A = I.src;
			ip_mask_B = I.dest;
			ttl = Integer.toString(I.time_to_live);
			tos = I.type_of_service;
			fragoffset = Integer.toString(I.fragment_offset);
			if(I.fragmented)
				fragbits = "M";
			else fragbits = "N";
				
			time_stamp = timestamped_packets.get(k);
			
			if(ip_mask_A.equals(ip_mask_B))
				sameip = true;
			else sameip = false;
			//I.pretty_print();
			
			if(I.protocol=="tcp"){
				T = new tcp(packet_to_analyze);
				ip_type = "tcp";
				port_A = T.src_port;
				port_B = T.dest_port;
				
				flags = T.reserved_flags;
				
				seq = Integer.toString(T.seq_num);
				ack = Integer.toString(T.ack_num);
				//T.pretty_print();
				
			}else if(I.protocol=="udp"){
				U = new udp(packet_to_analyze);
				ip_type = "udp";
				port_A = U.src_port;
				port_B = U.dest_port;
				
				//U.pretty_print();
				
			}else if(I.protocol=="icmp"){
				IC = new icmp(packet_to_analyze);
				ip_type = "icmp";
				
				itype = IC.type_of_service;
				icode = Integer.toString(IC.code);
				//IC.pretty_print();
			}
		}
		else if(E.packet_type=="arp"){
			A = new arp(packet_to_analyze);
			//A.pretty_print();
			eth_type = "arp";
			
			time_stamp = timestamped_packets.get(k);
		}
		
		String[] packet_params = {ip_mask_A,Integer.toString(port_A),ip_mask_B,Integer.toString(port_B)};
		
		//now check against the rules, the first 7 options
		for(int i =0; i<rp.RULES.size();i++){
			
			HashMap<String,String> currRule = rp.RULES.get(i);
			
			//create a string array of the rules in file for easier comparison
			String[] rule_params = {currRule.get("ip/mask_A"),currRule.get("port1_&_port2_A"),currRule.get("ip/mask_B"),currRule.get("port1_&_port2_B"),currRule.get("<->")};
			
			//under ip stuff
			if(currRule.get("protocol").equals(ip_type) && currRule.get("action").equals("alert"))
				//check if the traffic matches rules
				if(combination(rule_params,packet_params)){
					//check options to log or not
					if(options_check(currRule)){
						//log
							log(currRule,i);
					}
				}
		}
		
		analyzed.add((String) k);
		count_analyzed++;
		//System.out.println(count_analyzed);
	}
	
	synchronized boolean options_check(HashMap<String,String> cr){
		
		boolean log = true;
		
		if(cr.containsKey("ttl"))
			if(cr.get("ttl").equals(ttl))
				log = true;
			else return false;
		if(cr.containsKey("tos"))
			if(cr.get("tos").equals(tos))
				log = true;
			else return false;
		if(cr.containsKey("id"))
			if(cr.get("id").equals(id))
				log = true;
			else return false;
		if(cr.containsKey("fragbits"))
			if(cr.get("fragbits").equals(fragbits))
				log = true;
			else return false;
		if(cr.containsKey("flags"))
			if(flags.contains(cr.get("flags")))
				log = true;
			else return false;
		if(cr.containsKey("seq"))
			if(cr.get("seq").equals(seq))
				log = true;
			else return false;
		if(cr.containsKey("ack"))
			if(cr.get("ack").equals(ack))
				log = true;
			else return false;
		if(cr.containsKey("itype"))
			if(cr.get("itype").equals(itype))
				log = true;
			else return false;
		if(cr.containsKey("icode"))
			if(cr.get("icode").equals(icode))
				log = true;
			else return false;
		if(cr.containsKey("sameip"))
			if(sameip)
				log = true;
			else return false;
		if(cr.containsKey("sid"))
			if(sid.get(id) == Integer.parseInt(cr.get("sid")))
				log = true;
			else return false;
		if(cr.containsKey("sid_rule"))
			print_sid_rule = true;
		else print_sid_rule = false;
		if(cr.containsKey("logto"))
			logtofile = true;
		else logtofile = false;
		
		return log;
	}
	
	synchronized void log(HashMap<String,String> cr, int sid_rule) throws IOException{
		sb = new StringBuilder();
		sb.append("["+time_stamp + "]" + " ");
		sb.append("|"+ip_type + "| ");
		sb.append("ID:"+id + " ");
		if(print_sid_rule)
			sb.append("Rule#:"+ sid_rule + " ");
		if(cr.containsKey("msg"))
			sb.append("{"+cr.get("msg")+"} ");
		if(logtofile){
			logwriter = new BufferedWriter(new FileWriter(cr.get("logto")+file_stamp+".log",true));
			logwriter.append(sb);
			logwriter.newLine();
			logwriter.close();
		}else System.out.println(sb);
	}
	
	synchronized boolean combination(String[] rule, String[] params){
		//a=any, s=set
		boolean check =false;
		boolean debug = false;
		
		if(rule[0].equals("any") && rule[1].equals("any") && rule[2].equals("any") && rule[3].equals("any")){//aaaa
			if(debug) System.out.println("aaaa");
			check = true;
		}
		else if(rule[0].equals("any") && rule[1].equals("any") && rule[2].equals("any") && !rule[3].equals("any")){//aaas
			if(debug) System.out.println("aaas");
			if(rule[4].equals("->")){
				if(params[3].equals(rule[3]))
					check =true;
			}else if(rule[4].equals("<>")){
				if(params[3].equals(rule[3]) || params[1].equals(rule[3]))
					check =true;
			}
		}
		else if(rule[0].equals("any") && rule[1].equals("any") && !rule[2].equals("any") && rule[3].equals("any")){//aasa
			if(debug) System.out.println("aasa");
			if(rule[4].equals("->")){
				if(params[2].equals(rule[2]))
					check =true;
			}else if(rule[4].equals("<>")){
				if(params[2].equals(rule[2]) || params[0].equals(rule[2]))
					check =true;
			}
		}
		else if(rule[0].equals("any") && !rule[1].equals("any") && rule[2].equals("any") && rule[3].equals("any")){//asaa
			if(debug) System.out.println("asaa");
			if(rule[4].equals("->")){
				if(params[1].equals(rule[1]))
					check =true;
			}else if(rule[4].equals("<>")){
				if(params[1].equals(rule[1]) || params[3].equals(rule[1]))
					check =true;
			}
		}
		else if(!rule[0].equals("any") && rule[1].equals("any") && rule[2].equals("any") && rule[3].equals("any")){//saaa
			if(debug) System.out.println("saaa");
			if(rule[4].equals("->")){
				if(params[0].equals(rule[0]))
					check =true;
			}else if(rule[4].equals("<>")){
				if(params[0].equals(rule[0]) || params[2].equals(rule[0]))
					check =true;
			}
		}
		else if(!rule[0].equals("any") && !rule[1].equals("any") && rule[2].equals("any") && rule[3].equals("any")){//ssaa
			if(debug) System.out.println("ssaa");
			if(rule[4].equals("->")){
				if(params[0].equals(rule[0]) && params[1].equals(rule[1]))
					check = true;
			}else if(rule[4].equals("<>")){
				if((params[0].equals(rule[0]) && params[1].equals(rule[1])) || (params[2].equals(rule[0]) && params[3].equals(rule[1])))
					check = true;
			}
		}
		else if(!rule[0].equals("any") && !rule[1].equals("any") && !rule[2].equals("any") && rule[3].equals("any")){//sssa
			if(debug) System.out.println("sssa");
			if(rule[4].equals("->")){
				if(params[0].equals(rule[0]) && params[1].equals(rule[1]) && params[2].equals(rule[2]))
					check = true;
			}else if(rule[4].equals("<>")){
				if((params[0].equals(rule[0]) && params[1].equals(rule[1]) && params[2].equals(rule[2])) || (params[2].equals(rule[0]) && params[3].equals(rule[1]) && params[0].equals(rule[2])))
					check = true;
			}
		}
		else if(!rule[0].equals("any") && !rule[1].equals("any") && !rule[2].equals("any") && !rule[3].equals("any")){//ssss
			if(debug) System.out.println("ssss");
			if(rule[4].equals("->")){
				if(params[0].equals(rule[0]) && params[1].equals(rule[1]) && params[2].equals(rule[2]) && params[3].equals(rule[3]))
					check = true;
			}else if(rule[4].equals("<>")){
				if((params[0].equals(rule[0]) && params[1].equals(rule[1]) && params[2].equals(rule[2]) && params[3].equals(rule[3])) || (params[2].equals(rule[0]) && params[3].equals(rule[1]) && params[0].equals(rule[2]) && params[1].equals(rule[3])))
					check = true;
			}
		}
		else if(rule[0].equals("any") && rule[1].equals("any") && !rule[2].equals("any") && !rule[3].equals("any")){//aass
			if(debug) System.out.println("aass");
			if(rule[4].equals("->")){
				if(params[2].equals(rule[2]) && params[3].equals(rule[3]))
					check = true;
			}else if(rule[4].equals("<>")){
				if((params[2].equals(rule[2]) && params[3].equals(rule[3])) || (params[0].equals(rule[2]) && params[1].equals(rule[3])))
					check = true;
			}
		}
		else if(rule[0].equals("any") && !rule[1].equals("any") && !rule[2].equals("any") && !rule[3].equals("any")){//asss
			if(debug) System.out.println("asss");
			if(rule[4].equals("->")){
				if(params[1].equals(rule[1]) && params[2].equals(rule[2]) && params[3].equals(rule[3]))
					check = true;
			}else if(rule[4].equals("<>")){
				if((params[1].equals(rule[1]) && params[2].equals(rule[2]) && params[3].equals(rule[3])) || (params[3].equals(rule[1]) && params[0].equals(rule[2]) && params[1].equals(rule[3])))
					check = true;
			}
		}
		else if(rule[0].equals("any") && !rule[1].equals("any") && rule[2].equals("any") && !rule[3].equals("any")){//asas
			if(debug) System.out.println("asas");
			if(rule[4].equals("->")){
				if(params[1].equals(rule[1]) && params[3].equals(rule[3]))
					check = true;
			}else if(rule[4].equals("<>")){
				if((params[1].equals(rule[1]) && params[3].equals(rule[3])) || (params[3].equals(rule[1]) && params[1].equals(rule[3])))
					check = true;
			}
		}
		else if(!rule[0].equals("any") && rule[1].equals("any") && rule[2].equals("any") && !rule[3].equals("any")){//saas
			if(debug) System.out.println("saas");
			if(rule[4].equals("->")){
				if(params[0].equals(rule[0]) && params[3].equals(rule[3]))
					check =true;
			}else if(rule[4].equals("<>")){
				if((params[0].equals(rule[0]) && params[3].equals(rule[3])) || (params[2].equals(rule[0]) && params[1].equals(rule[3])))
					check =true;
			}
		}
		else if(!rule[0].equals("any") && rule[1].equals("any") && !rule[2].equals("any") && rule[3].equals("any")){//sasa
			if(debug) System.out.println("sasa");
			if(rule[4].equals("->")){
				if(params[0].equals(rule[0]) && params[2].equals(rule[2]))
					check = true;
			}else if(rule[4].equals("<>")){
				if((params[0].equals(rule[0]) && params[2].equals(rule[2])) || (params[2].equals(rule[0]) && params[0].equals(rule[2])))
					check = true;
			}
		}
		else if(rule[0].equals("any") && !rule[1].equals("any") && !rule[2].equals("any") && rule[3].equals("any")){//assa
			if(debug) System.out.println("assa");
			if(rule[4].equals("->")){
				if(params[1].equals(rule[1]) && params[2].equals(rule[2]))
					check =true;
			}else if(rule[4].equals("<>")){
				if((params[1].equals(rule[1]) && params[2].equals(rule[2])) || (params[3].equals(rule[1]) && params[0].equals(rule[2])))
					check =true;
			}
		}
		else if(!rule[0].equals("any") && !rule[1].equals("any") && rule[2].equals("any") && !rule[3].equals("any")){//ssas
			if(debug) System.out.println("ssas");
			if(rule[4].equals("->")){
				if(params[0].equals(rule[0]) && params[1].equals(rule[1]) && params[3].equals(rule[3]))
					check = true;
			}else if(rule[4].equals("<>")){
				if((params[0].equals(rule[0]) && params[1].equals(rule[1]) && params[3].equals(rule[3])) || (params[2].equals(rule[0]) && params[3].equals(rule[1]) && params[1].equals(rule[3])))
					check =true;
			}
		}
		else if(!rule[0].equals("any") && rule[1].equals("any") && !rule[2].equals("any") && !rule[3].equals("any")){//sass
			if(debug) System.out.println("sass");
			if(rule[4].equals("->")){
				if(params[0].equals(rule[0]) && params[2].equals(rule[2]) && params[3].equals(rule[3]))
					check = true;
			}else if(rule[4].equals("<>")){
				if((params[0].equals(rule[0]) && params[2].equals(rule[2]) && params[3].equals(rule[3])) || (params[2].equals(rule[0]) && params[0].equals(rule[2]) && params[1].equals(rule[3])))
					check =true;
			}
		}
		
		return check;
		
	}
}		
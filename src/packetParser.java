import java.util.ArrayList;


public class packetParser extends methods {
	boolean verbose = true;
	public static ArrayList<byte[]> one_packet = new ArrayList<byte[]>();
	public static ArrayList<byte[]> all_packets = new ArrayList<byte[]>();
	
	public static void main(String[] args) throws InterruptedException {
		

		networkSniffer netSniffer = new networkSniffer(100000);
		Thread snifferThread = new Thread(netSniffer);
		snifferThread.start();
		
		while(netSniffer.count>0){
			byte[] p = netSniffer.getPacket();
			ethernet ETH = new ethernet(p);
			if(ETH.packet_type.equals("ip")){
				ip IP = new ip(p);
				if(!IP.fragmented){
					/*if(IP.protocol.equals("tcp")){
						tcp TCP = new tcp(p);
						if((TCP.src.equals("192.168.1.14")||TCP.dest.equals("192.168.1.14"))||
								(TCP.src.equals("192.168.1.46")||TCP.dest.equals("192.168.1.46")) ){
							
							System.out.print(TCP.src + " to " +TCP.dest + " || ");
							System.out.println(TCP.data);
						}
					}*/
					if(IP.protocol.equals("udp")){
						udp TCP = new udp(p);
						if((TCP.src.equals("192.168.1.14")||TCP.dest.equals("192.168.1.14"))||
								(TCP.src.equals("192.168.1.46")||TCP.dest.equals("192.168.1.46")) ){
							
							System.out.print(TCP.src + " to " +TCP.dest + " || ");
							System.out.println(TCP.data);
						}
					}
					
				}
			}
			/*all_packets.add(p);
			for (int i= 0; i<all_packets.size(); i++){
				print_packet(all_packets.get(i));
			}*/
		}
		System.out.println("EXITED!");
	}
}
	
	
	/*//read from file
	public packetParser(String filename){
		byte[][] arrayOfArrayOfBytes;
		
		arrayOfArrayOfBytes = read_from_file(filename);
		
		for(int i=0;i<arrayOfArrayOfBytes.length;i++){
			byte[] packet = arrayOfArrayOfBytes[i];
			
			ethernet Eth = new ethernet(packet);
			if(verbose)System.out.print("|eth|");
			if (Eth.packet_type.equals("ip")){
				ip Ip = new ip(packet);
				if(verbose)System.out.print("|ip|");
				if(Ip.protocol.equals("tcp")){
					tcp Tcp = new tcp(packet);
					if(verbose)System.out.print("|tcp|");
				}
				else if(Ip.protocol.equals("udp")){
					udp Udp = new udp(packet);
					if(verbose)System.out.print("|udp|");
				}
				else if(Ip.protocol.equals("icmp")){
					icmp Icmp = new icmp(packet);
					if(verbose)System.out.print("|icmp|");
				}
			}
			else if (Eth.packet_type.equals("arp")){
				arp Arp = new arp(packet);
				if(verbose)System.out.print("|arp|");
			}
			new_line();
		}
	}*/
	

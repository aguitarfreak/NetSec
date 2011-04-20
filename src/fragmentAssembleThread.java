import java.nio.ByteBuffer;
import java.util.ArrayList;

public class fragmentAssembleThread extends assembler implements Runnable {
	
	networkSniffer netSniffer;
	fileSniffer fSniffer;
	int packetCount;
	boolean verbose = false;
	int arp_count = 0;
	DateUtils timestamp;
	
	fragmentAssembleThread(networkSniffer n){
		netSniffer = n;
		timestamp = new DateUtils();
	}
	
	fragmentAssembleThread(fileSniffer f){
		fSniffer = f;
		timestamp = new DateUtils();
	}

	public void run() {
		try { 
			if(network){
	            while ( netSniffer.count>0 ) { 
	                byte[] p = netSniffer.getPacket();
	                //ADD TO THE ARRAYLIST
	                all_packets.add(p);
	                for (int i= 0; i<all_packets.size(); i++){
	                	 spawn_thread(all_packets.get(i));
	                	 Thread.sleep( 100 ); 
	                }
	            }
			}
			else{
	            while ( (fSniffer.notDone) ) {
	                byte[] p = fSniffer.getPacket();
	                //ADD TO THE ARRAYLIST
	                all_packets.add(p);
	                for (int i= 0; i<all_packets.size(); i++){
	                	 spawn_thread(all_packets.get(i));
	                	 Thread.sleep( 100 ); 
	                }
	            }
			}
        }  
        catch( InterruptedException e ) { } 
	}

	private void spawn_thread(byte[] P) throws InterruptedException {
		ethernet Eth = new ethernet(P);
		if(verbose)System.out.print("|eth");
		
		if (Eth.packet_type.equals("ip")){
			
			ip Ip = new ip(P);
			
			//check checksum
			if(isChecksumCorrect(P)){
				//check to see if fragmented
				if(!Ip.fragmented){
					if(verbose)System.out.println("Unfragmented packet "+ Ip.identification);
					if(!complete.contains(Ip.identification)){
						complete.add(Ip.identification);
						reassembled_packets.put(Ip.identification,P);
						sid.put(Ip.identification, 5);
					}
					else
						if(verbose)System.out.println("Already assembled packet "+ Ip.identification);
				}
				
				//check to see if a thread is working on it
				else if (working_on.contains(Ip.identification)&&!complete.contains(Ip.identification)){
					//check to see if the thread's arraylist already has the packet
					ArrayList<byte[]> check = (ArrayList<byte[]>) fragments_dict.get(Ip.identification);
					if(!check.contains(P)){
						//put the packet in the threads's arraylist
						fragments_dict.put(Ip.identification, P);
						//System.out.println(fragments_dict.toString());
					}
						
				}
				
				//check to see if the packet has been reassembled
				else if(complete.contains(Ip.identification)){
					fragments_dict.remove(Ip.identification);
					working_on.remove(Ip.identification);
				}
				
				//create a new worker thread
				//else{
				else if (!working_on.contains(Ip.identification)&&!complete.contains(Ip.identification)){
					fragments_dict.put(Ip.identification, P);
					workerThread worker = new workerThread(P);
					Thread w = new Thread(worker,Ip.identification);
					w.start();
					threads.add(w);
					working_on.add(Ip.identification);
				}
			//if checksum is not correct
			}else{
				System.out.println("checksum failed on packet : " + Ip.identification);
			}
		}

		else if (Eth.packet_type.equals("arp")){
			if(!arp_packets.containsValue(P)){
				String arp_id = new String("arp"+arp_count);
				sid.put(arp_id, 0);
				arp_packets.put(arp_id, P);
				arp_count++;
			}
		}
		
		//clear the packet list of analyzed packet
		all_packets.remove(P);
	}
	
private boolean isChecksumCorrect(byte[] packet) {  
		
		int result;
		ip Ip = new ip(packet);
		//length is the header length
		byte[] ipheader =  new byte[Ip.header_length];
		
		for(int i=0;i<Ip.header_length;i++)
		{
			ipheader[i]=packet[ethernet_header_length+i];
		}
		
		ByteBuffer buff=ByteBuffer.wrap(ipheader);
		
		int sum = 0;  

		while (buff.remaining() > 1) {  

		 sum += buff.getShort() & 0xFFFF;  

		}  

		if (buff.remaining() > 0) {  
			
			sum += buff.get() & 0xFF;  

		}
		
		while (sum >>> 16 != 0) {  
			
			sum = (sum & 0xFFFF) + (sum >>> 16);  
			
		}  
		
		 result=(~sum)& 0xFFFF;
		 
		 if(result==0)
			 return true;
		 else
			 return false;
	} 
}

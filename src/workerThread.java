import java.util.ArrayList;

public class workerThread extends assembler implements Runnable {
	
	boolean done = false;
	boolean found_last = false;
	boolean overwritten = false;
	boolean large_ip_seg = false;
	boolean verbose = false;
	
	ArrayList<byte[]> h = new ArrayList <byte[]>();
	ip IP;
	byte[] first_packet;
	
	byte[] reassembled;
	boolean[] reassembled_bools;
	
	int dl = 0;//data length
	int tl = 0;//total length
	int pl = 0;//payload length
	
	int ip_payload_start = 34;
	
	long startTime;
	long ttl;
	long nanofactor = 1000000000;
	
	public workerThread(byte[] fp){
		first_packet = fp;
		IP = new ip(first_packet);
		ttl = IP.time_to_live;
		startTime = System.nanoTime();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		try { 
            while (!done) {
            	//check to see if timed out
            	if(!(System.nanoTime() > startTime + 5*nanofactor)){
            		//System.out.println(System.nanoTime()-(startTime + ttl*nanofactor));
	            	//copy thread specific fragments
	            	h = (ArrayList<byte[]>) fragments_dict.get(Thread.currentThread().getName().toString());
	            	if(!h.isEmpty()){
	            		reassemble(h);
	            		Thread.sleep(100);
	            	}
	            //if timed out
            	}else{
            		completed_fragments.put(Thread.currentThread().getName().toString(), h);//this stores all fragments
    				reassembled_packets.put(Thread.currentThread().getName().toString(), first_packet);//this stores reassembled packet
    				sid.put(Thread.currentThread().getName().toString(), 4);
    				complete.add(Thread.currentThread().getName().toString());
    				working_on.remove(Thread.currentThread().getName().toString());
            	}
            		
            }
            //add to complete arraylist when done
            complete.add(Thread.currentThread().getName().toString());
            working_on.remove(Thread.currentThread().getName().toString());
        }  
        catch( InterruptedException e )  {
			e.printStackTrace();
		}
		
	}
	
	synchronized void reassemble(ArrayList<byte[]> H){
		//search for the last packet in order to initialize the array
		for(int idx = 0; idx<H.size(); idx++){
			IP = new ip(H.get(idx));
			
			if(!IP.more_fragments){
				tl = IP.total_length;
				dl = tl - IP.header_length;
				pl = IP.fragment_offset + dl;
				
				reassembled = new byte[pl];//includes IP header(8 bytes)
				reassembled_bools = new boolean[pl];
				
				for(int b = 0; b < pl; b++){
					reassembled_bools[b]= false;
					reassembled[b] = 0;
				}
				
				found_last = true;
			}
		}
		
		if(found_last){
			for(int idx = 0; idx<H.size(); idx++){
				IP = new ip(H.get(idx));
				
				//check for IP segment larger than 64k
				if(!large_ip_seg){
					if(IP.total_length>64){
						completed_fragments.put(Thread.currentThread().getName().toString(), h);
						reassembled_packets.put(Thread.currentThread().getName().toString(), first_packet);
						sid.put(Thread.currentThread().getName().toString(), 3);
						large_ip_seg = true;
						done = true;
					}
				}
				tl = IP.total_length;
				dl = tl - IP.header_length;
				pl = IP.fragment_offset + dl;
				
				copy_to_reassembled(H.get(idx),IP.fragment_offset,pl);
				done();
			}
		}
	}
	
	//copy fragment payload to the reassembled array
	public synchronized void copy_to_reassembled(byte[] data, int start_idx, int end_idx){
		int j = 0;
		for(int m = start_idx; m<end_idx; m++){
			//if it has been overwritten
			if(reassembled_bools[m]){
				overwritten = true;
			}
			reassembled[m] = data[ip_payload_start+j];
			reassembled_bools[m] = true;
			j++;
		}
	}
	
	//check to see if the reassembled payload has been filled completely
	public synchronized void done(){
		for(int m = 0; m< reassembled_bools.length; m++){
			if(!reassembled_bools[m]){
				done = false;
				break;
			}else{
				
				completed_fragments.put(Thread.currentThread().getName().toString(), h);//this stores all fragments
				reassembled_packets.put(Thread.currentThread().getName().toString(), reassembled);//this stores reassembled packet
				
				//update sids
				if(!overwritten)
					sid.put(Thread.currentThread().getName().toString(), 1);
				else
					sid.put(Thread.currentThread().getName().toString(), 2);
				
				done = true;
			}
				
		}
	}
}

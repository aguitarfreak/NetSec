
public class networkSniffer extends assembler implements Runnable {
	static final int MAXQUEUE = 1;
	int count;
	
	public networkSniffer(int c){
		count = c;
		String[] adapters=driver.getAdapterNames();
        System.out.println("Number of adapters: "+adapters.length);
        for (int i=0; i< adapters.length; i++) System.out.println("Device name in Java ="+adapters[i]);
        //Open first found adapter (usually first Ethernet card found)
        if (driver.openAdapter(adapters[0])) System.out.println("Adapter is open: "+adapters[0]);
	}
	@Override
	public void run() {
        try { 
            while ( count>0 ) {
            	newPacket();
            	count--;
            	//System.out.println(count);
            } 
        }  
        catch( InterruptedException e ) { }
       
	}
	
	synchronized void newPacket() throws InterruptedException {
        	
    	while ( one_packet.size() == MAXQUEUE ) 
            wait(); 
    	one_packet.add(driver.readPacket());
        notify(); 

	}
	
	// Called by Consumer 
    public synchronized byte[] getPacket() throws InterruptedException { 
        notify(); 
        while ( one_packet.size() == 0 ) 
            wait();
        byte[] packet = one_packet.get(0); 
        one_packet.clear();
        return packet; 
    } 
}

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class fileSniffer extends assembler implements Runnable {
	static final int MAXQUEUE = 1;
	String temp="";
	String filename;
	BufferedReader in;
	String str = " ";
	boolean notDone;
	
	public fileSniffer(String fileName) throws InterruptedException, IOException{
		filename = fileName;
		in = new BufferedReader(new FileReader("Files/Data/"+filename));
		notDone = true;
	}
	
	@Override
	public void run() {
		try { 
            while (notDone) {
            	newPacket();
            	//Thread.sleep(200);
            }
        }  
        catch( InterruptedException e ) { } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	synchronized void newPacket() throws InterruptedException, IOException {

    	while ( one_packet.size() == MAXQUEUE ) 
            wait();
    	
	    while ((str = in.readLine()) != null) {
	        temp+=str.replaceAll(" ","");
	        if(str.equals(""))
	        	break;
	    }
	    
	    if(str==null){
    		notDone = false;
    	}
	    one_packet.add(stringToByte(temp));
    	temp = "";
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


public class udp extends ip {
	//indices
	private  int src_port_idx = 34;//0-1
	private  int dest_port_idx = 36;//2-3
	private  int length_idx = 38;//4-5
	private  int chksum_idx = 40;//6-7
	private  int data_idx = 42;//8+
	
	 int src_port;
	 int dest_port;
	 int length;
	 String chksum;
	String data = "";
	
	public udp(byte[] array){
		super(array);
		
		data = "";
		
		src_port = ((array[src_port_idx]&0xFF)<<8) + (array[src_port_idx+1]&0xFF);
		dest_port = ((array[dest_port_idx]&0xFF)<<8)+(array[dest_port_idx+1]&0xFF);
		length = ((array[length_idx]&0xFF)<<8) + (array[length_idx+1]&0xFF);
		chksum = "0x"+(driver.byteToHex(array[chksum_idx])+driver.byteToHex(array[chksum_idx+1]));
		//get data from file
		for(int i = data_idx; i<array.length; i++){
			data+=driver.byteToChar(array[i]);
			
		}
	}
	
	public void pretty_print(){
		System.out.println(justifyCenter("UDP HEADER",36));
		System.out.print(" 0\t7 8\t15 16\t 23 24\t  31");
		System.out.println();
		
		divider();
		System.out.println("|"+justifyCenter(Integer.toString(src_port),17)+"|"+
							   justifyCenter(Integer.toString(dest_port),17)+"|");
		divider();
		System.out.println("|"+justifyCenter(Integer.toString(length),17)+"|"+
				   			   justifyCenter(chksum,17)+"|");
		divider();
		System.out.println("|"+justifyCenter(data,35)+"|");
		divider();
	}
	
	public void divider(){
		for(int i = 0; i<37;i++){
			if(i%9==0){
				System.out.print("+");
			}
			else
			System.out.print("-");
		}
		System.out.println();
	}
}

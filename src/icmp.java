
import java.util.HashMap;
import java.util.Map;

public class icmp extends ip {
	
	//indices
	public  int type_idx = 34;
	private  int code_idx = 35;
	private  int chksum_idx = 36;//2-3
	private  int identifier_idx = 38;//4-5
	private  int seq_num_idx = 40;//6-7
	public  int data_idx = 42;//8+
	
	private HashMap<Integer, String> icmp_types = new HashMap<Integer, String>();
	//HashMap<Integer, String> icmp_codes = new HashMap<Integer, String>();
	 int type_num;
	 String type;
	 int code;
	 String chksum;
	 String identifier;
	 int seq_num;
	 String data="";
	
	public icmp(byte[] array){
		super(array);
		
		data = "";
		//create icmp type hashmap
		icmp_types.put(new Integer(0),"Echo Reply" );
		icmp_types.put(new Integer(3),"Dest Unreachable");
		icmp_types.put(new Integer(8),"Echo Request" );//etc
		
		type_num = array[type_idx]&0xFF;
		
		//check to see if hashmap contains value
		if(icmp_types.containsKey(type_num))
			type = icmp_types.get(type_num).toString();
		else type = "unknown";
		
		code = array[code_idx]&0xFF;
		
		chksum = "0x"+(driver.byteToHex(array[chksum_idx])+driver.byteToHex(array[chksum_idx+1]));
		
		identifier = "0x"+(driver.byteToHex(array[identifier_idx])+driver.byteToHex(array[identifier_idx+1]));
		
		seq_num = ((array[seq_num_idx]&0xFF)<<8) + (array[seq_num_idx+1]&0xFF);
			
		for (int i=data_idx; i<array.length; i++){
			data+=driver.byteToChar(array[i]);
		}
	}

	public void pretty_print() {
		System.out.println(justifyCenter("ICMP HEADER",65));
		print_num_header();
		
		print_divider();
		System.out.println("|"+ justifyCenter(type,15)+"|"+
								justifyCenter(Integer.toString(code),15)+"|"+
								justifyCenter(chksum,31)+"|");
		print_divider();
		System.out.println("|"+ justifyCenter(identifier, 31)+"|"+
								justifyCenter(Integer.toString(seq_num),31)+"|");
		print_divider();
		System.out.println("|"+ justifyCenter(data,63)+"|");
		print_divider();
	}
}

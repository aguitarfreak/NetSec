import java.awt.List;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.ArrayList;


public class tcp extends ip {
	//indices
	private  int src_port_idx = 34;//0-1
	private  int dest_port_idx = 36;//2-3
	private  int seq_num_idx = 38;//4-7
	private  int ack_num_idx = 42;//8-11
	private  int header_length_idx = 46;//Data Offset
	private  int reserved_idx = 47;//flags
	private  String[] flags = new String[]{"fin","syn","rst","psh","ack","urg","ecn","cwr"};
	private  int window_idx = 48;//14-15
	private  int chksum_idx = 50;//16-17
	private  int urgent_pointer_idx = 52;//18-19
	private  int options_idx = 54;//20-21
	private  int options_timestamp_idx = 56;//22-31
	private  int data_idx = 66;//28+
	
	
	 int src_port;
	 int dest_port;
	 int seq_num;
	 int ack_num;
	 int header_length;
	 ArrayList<String> res_flags = new ArrayList<String>();
	 String reserved_flags;
	 int window;
	 String chksum;
	 String urgent_pointer;//0x000 usually
	 String options;//string with commas between each bytes
	 String options_timestamp="";
	 String data="";//convert each bit to char
	
	public tcp(byte[] array){
		super(array);
		
		data = "";
		res_flags.clear();
		//System.out.println(driver.byteArrayToString(array));
		src_port = ((array[src_port_idx]&0xFF)<<8) + (array[src_port_idx+1]&0xFF);
		dest_port = ((array[dest_port_idx]&0xFF)<<8)+(array[dest_port_idx+1]&0xFF);
		
		/*seq_num = ((array[seq_num_idx]&0xFF)*16^6) + ((array[seq_num_idx+1]&0xFF)*16^4) 
				+ ((array[seq_num_idx+2]&0xFF)*16^2) + (array[seq_num_idx+3]&0xFF);*/
		//seq_num = ((array[src_port_idx]&0xff)<<24) + ((array[src_port_idx+1]&0xff)<<16)  + ((array[src_port_idx+2]&0xff)<<8) + (array[src_port_idx+3]&0xff);
		/*ack num*/
		header_length = (array[header_length_idx]&0xFF)/4;//in bytes
		
		//check for flags
		for(int i=0;i<8;i++){
			if(is_bit_set(array[reserved_idx], i))
				res_flags.add(flags[i]);
		}
		reserved_flags = res_flags.toString();
		
		window = ((array[window_idx]&0xFF)<<8) + (array[window_idx+1]&0xFF);
		
		chksum = "0x"+(driver.byteToHex(array[chksum_idx])+driver.byteToHex(array[chksum_idx+1]));
		urgent_pointer = "0x"+(driver.byteToHex(array[urgent_pointer_idx])+driver.byteToHex(array[urgent_pointer_idx+1]));
		
		if(array.length>64){
			options = "0x"+(driver.byteToHex(array[options_idx])+driver.byteToHex(array[options_idx+1]));
			//get timestamp
			for(int i = options_timestamp_idx; i<data_idx; i++){
				options_timestamp+=driver.byteToHex(array[i]);
			}
			//get data from file
			for(int i = data_idx; i<array.length; i++){
				data+=driver.byteToChar(array[i]);
			}
		}
	}
	
/*	//to check for flags
	boolean is_bit_set(byte value, int bitindex)
	{
	    return (value & (1 << bitindex)) != 0;
	}*/
	
	public void pretty_print(){
		System.out.println(justifyCenter("TCP HEADER",65));
		print_num_header();
		print_divider();
		System.out.println("|"+ justifyCenter(Integer.toString(src_port), 31)+"|"+
								justifyCenter(Integer.toString(dest_port),31)+"|");
		print_divider();
		System.out.println("|"+ justifyCenter(Integer.toString(seq_num),63)+"|");
		print_divider();
		System.out.println("|"+ justifyCenter(Integer.toString(ack_num),63)+"|");
		print_divider();
		System.out.println("|"+ justifyCenter(Integer.toString(header_length),7)+"|"+
								justifyCenter(reserved_flags,23)+"|"+
								justifyCenter(Integer.toString(window),31)+"|");
		print_divider();
		System.out.println("|"+ justifyCenter(chksum,31)+"|"+
								justifyCenter(urgent_pointer,31)+"|");
		print_divider();
		System.out.println("|"+ justifyCenter(options,7)+"|"+
						   		justifyCenter(options_timestamp,55)+"|");
		print_divider();
		System.out.println("|"+ justifyCenter(data,63)+"|");
		print_divider();
	}

}

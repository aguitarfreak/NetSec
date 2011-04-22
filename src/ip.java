
public class ip extends ethernet{
	
	//protocols
	private String tcp = "06"; private String udp = "11";
	private String icmp = "01";
	
	public int ver_idx = 14;//version(14[0])
	private int length_idx = 14;//length(14[1]*4 = num of bytes)
	private int type_of_service_idx = 15;
	private int total_length_idx= 16;//Total Length(16-17)
	private int identification_idx=18; //(18-19)
	private int flags_idx=20;//(20)
	//private int fragment_offset_idx=21;//(20-21)
	private int fragment_offset_idx=20;//(20-21)
	private int time_to_live_idx=22;//(22)
	private int protocol_idx = 23;//(23)
	private int header_chksm_idx = 24;//(24-25)
	private int src_idx = 26;//(26-29)
	private int dest_idx = 30;//(30-33)
	
	
	 int version;
	 int header_length;
	 String type_of_service;
	 int total_length;
	 String identification;
	 String flags;
	 //String fragment_offset;
	 int fragment_offset;
	 int time_to_live;
	 private String proto;
	 String header_chksum;
	 String src;
	 String dest;
	
	 String protocol;
	 
	 boolean fragmented;
	 boolean more_fragments;
	
	public ip(byte[] array){
		
		super(array);
		
		version = array[ver_idx]>>4&0x0F;
		header_length = (array[ver_idx]&0x0F)*4;
		type_of_service = "0x"+driver.byteToHex(array[type_of_service_idx]);
		total_length = (array[total_length_idx]&0xff)+(array[total_length_idx+1]&0xFF);
		identification = driver.byteToHex(array[identification_idx])+
							driver.byteToHex(array[identification_idx+1]);
		flags="0x"+driver.byteToHex(array[flags_idx]);
		
		//fragment_offset = (array[fragment_offset_idx]&0xFF)*8;
		String offset_hex = driver.byteToHex(array[fragment_offset_idx]) + driver.byteToHex(array[fragment_offset_idx+1]);
		fragment_offset = (int) ((Math.pow(16, 3)*Character.getNumericValue(offset_hex.charAt(0)) +
								(Math.pow(16, 2)*Character.getNumericValue(offset_hex.charAt(1)) +
								(Math.pow(16, 1)*Character.getNumericValue(offset_hex.charAt(2)) +
								(Math.pow(16, 0)*Character.getNumericValue(offset_hex.charAt(3)))))))*8;
		//check flags and offset fields to determine if the packet is fragmented
		//if more fragment is set or fragment offset is not 0--> fragmented
		if((is_bit_set(array[flags_idx],5))||(fragment_offset!=0)){
			fragmented = true;
			//check more fragments
			if(is_bit_set(array[flags_idx],5)){
				more_fragments = true;
				//System.out.println("MF");
			}else{
				more_fragments = false;
				//System.out.println("DF");
			}
			
			/*if(is_bit_set(array[flags_idx],5))
				more_fragments = true;
			else if (is_bit_set(array[flags_idx],6)){
				more_fragments = false;
				System.out.println("mfmfmfmfmfm");
			}*/
		}else{
			fragmented = false;
			more_fragments = false;
		}
		
		
		/*if((is_bit_set(array[flags_idx],5))||(fragment_offset!=0))
			more_fragments = true;
		if(is_bit_set(array[flags_idx],6))
			more_fragments = false;*/
		
		time_to_live	= array[time_to_live_idx]&0xFF;
		proto = driver.byteToHex(array[protocol_idx]);
		header_chksum = "0x"+driver.byteToHex(array[header_chksm_idx])+
							driver.byteToHex(array[header_chksm_idx+1]);
		src = byteToIP(array,src_idx);
		dest = byteToIP(array,dest_idx);
		
		if(proto.equals(tcp))
			protocol = "tcp";
		else if(proto.equals(udp))
			protocol = "udp";
		else if (proto.equals(icmp))
			protocol = "icmp";
		else 
			protocol = "unknown";
	}
	
	public void pretty_print(){
		System.out.println(justifyCenter("IP HEADER",65));
		print_num_header();
		print_divider();
		System.out.println("|"+ justifyCenter(Integer.toString(version), 7)+"|"+
								justifyCenter(Integer.toString(header_length),7)+"|"+
								justifyCenter(type_of_service,15)+"|"+
								justifyCenter(Integer.toString(total_length),31)+"|");
		print_divider();
		System.out.println("|"+ justifyCenter(identification,31)+"|"+
								justifyCenter(flags,7)+"|"+
								justifyCenter(Integer.toString(fragment_offset),23)+"|");
		print_divider();
		System.out.println("|"+ justifyCenter(Integer.toString(time_to_live),15)+"|"+
								justifyCenter(protocol,15)+"|"+
								justifyCenter(header_chksum,31)+"|");
		print_divider();
		System.out.println("|"+ justifyCenter(src,63)+"|");
		print_divider();
		System.out.println("|"+ justifyCenter(dest,63)+"|");
		print_divider();
		System.out.println("|"+ justifyCenter("Options/Padding",63)+"|");
		print_divider();
	}
	
}

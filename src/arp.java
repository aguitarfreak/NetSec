import java.util.HashMap;


public class arp extends ethernet {
	private  int hardware_type_idx = 14;//0-1
	private  int protocol_type_idx = 16;//2-3
	private  int hardware_size_idx = 18;
	private  int protocol_size_idx = 19;
	private  int opcode_idx = 20;//6-7
	private  int sender_mac_addr_idx = 22;//8-13
	private  int sender_ip_addr_idx = 28;//14-17
	private  int target_mac_addr_idx = 32;//18-23
	private  int target_ip_addr_idx = 38;//24-27
	private  int data_idx = 42;//28+
	
	private HashMap<Integer, String> arp_hardware_types = new HashMap<Integer, String>();
	
	 int hardware_type_num; String hardware_type;
	 String protocol_type;
	 int hardware_size;
	 int protocol_size;
	 String opcode;
	 StringBuilder sender_mac_addr;
	 String sender_ip_addr;
	 StringBuilder target_mac_addr;
	 String target_ip_addr;
	 String data="";
	
	public arp(byte[] array){
		super(array);
		
		data = "";
		//create arp type hashmap
		arp_hardware_types.put(new Integer(0),"Reserved" );
		arp_hardware_types.put(new Integer(1),"Ethernet");
		arp_hardware_types.put(new Integer(6),"IEEE 802");
		arp_hardware_types.put(new Integer(31),"IPsec Tunnel" );//etc
		
		hardware_type_num = ((array[hardware_type_idx]&0xFF)<<8) + (array[hardware_type_idx+1]&0xFF);
		
		//check to see if hashmap contains value
		if(arp_hardware_types.containsKey(hardware_type_num))
			hardware_type = arp_hardware_types.get(hardware_type_num).toString();
		else hardware_type = "unknown";
		
		if((driver.byteToHex(array[protocol_type_idx])+driver.byteToHex(array[protocol_type_idx+1])).equals("0800"))
			protocol_type = "ip";
		else protocol_type = "unknown";
		
		hardware_size = array[hardware_size_idx]&0xFF;
		protocol_size = array[protocol_size_idx]&0xFF;
		
		opcode = "0x"+(driver.byteToHex(array[opcode_idx])+driver.byteToHex(array[opcode_idx+1]));
		
		//destination and source MAC addresses
		sender_mac_addr = new StringBuilder();
		target_mac_addr = new StringBuilder();
		for (int i = sender_mac_addr_idx; i < sender_ip_addr_idx; i++) {
			sender_mac_addr.append(String.format("%02X%s", array[i], (i < sender_ip_addr_idx - 1) ? ":" : ""));
			target_mac_addr.append(String.format("%02X%s", array[i+10], (i < sender_ip_addr_idx - 1) ? ":" : ""));
		}
		
		sender_ip_addr = byteToIP(array,sender_ip_addr_idx);
		target_ip_addr = byteToIP(array,target_ip_addr_idx);
		
		for (int i=data_idx; i<array.length; i++){
			data+=driver.byteToChar(array[i]);
		}
		
		/*System.out.println(hardware_type_num+ " " + hardware_type+ " " +protocol_type+ " " +
						hardware_size+ " " +protocol_size+ " " +opcode+ " " +sender_mac_addr+ " " +target_mac_addr
						+ " " +sender_ip_addr+ " " +target_ip_addr+ " \n" +data);*/
		
	}
	
	public void pretty_print(){
		System.out.println(justifyCenter("ARP HEADER",65));
		print_num_header();
		
		print_divider();
		System.out.println("|"+ justifyCenter(hardware_type,31)+"|"+
								justifyCenter(protocol_type,31)+"|");
		print_divider();
		System.out.println("|"+ justifyCenter(Integer.toString(hardware_size), 15)+"|"+
								justifyCenter(Integer.toString(protocol_size), 15)+"|"+
								justifyCenter(opcode,31)+"|");
		print_divider();
		System.out.println("|"+ justifyCenter(sender_mac_addr.toString(),63)+"|");
		print_divider();
		System.out.println("|"+ justifyCenter(sender_ip_addr,63)+"|");
		print_divider();
		System.out.println("|"+ justifyCenter(target_mac_addr.toString(),63)+"|");
		print_divider();
		System.out.println("|"+ justifyCenter(target_ip_addr,63)+"|");
		print_divider();
		System.out.println("|"+ justifyCenter(data,63)+"|");
		print_divider();
		
	}
	
}

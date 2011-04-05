
public class ethernet extends methods{
	
	//indices
	private int eth_destination_start = 0,eth_source_start = 6;
	private int ethertype_start = 12; //type (12-13)
	
	public StringBuilder eth_destination, eth_source; //dest (0-5), src (6-11)
	public String packet_type;
	private String ip = "0800", arp = "0806";
	
	private String type;
	
	public ethernet(byte[] array) {
		
		//type of packet
		type = driver.byteToHex(array[ethertype_start])+driver.byteToHex(array[ethertype_start+1]);
		if(type.equals(ip)){
			packet_type = "ip";
		}
		else if(type.equals(arp)){
			packet_type = "arp";
		}
		else
			packet_type = "unknown";
		
		//destination and source MAC addresses
		eth_destination = new StringBuilder();
		eth_source = new StringBuilder();
		for (int i = eth_destination_start; i < eth_source_start; i++) {
			eth_destination.append(String.format("%02X%s", array[i], (i < eth_source_start - 1) ? ":" : ""));
			eth_source.append(String.format("%02X%s", array[i+6], (i < eth_source_start - 1) ? ":" : ""));
		}
	}
	
	public void pretty_print(){
		System.out.println(justifyCenter("ETHERNET HEADER",65));
		print_num_header();
		print_divider();
		System.out.println("|"+ justifyCenter(eth_destination.toString(), 29)+"|"+
								justifyCenter(eth_source.toString(),29)+"|"+
								justifyCenter(packet_type,3)+"|");
		print_divider();
		
	}
}

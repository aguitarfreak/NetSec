import java.util.Formatter;
import java.util.HashMap;


public class methods{

	public static SimplePacketDriver driver=new SimplePacketDriver();
	
	public  void help(){
		System.out.println("help [usage]");
	}
	
	//convert a string to byte
	public byte[] stringToByte (String s) {
        String s2;
        byte[] b = new byte[s.length() / 2];
        int i;
        for (i = 0; i < s.length() / 2; i++) {
            s2 = s.substring(i * 2, i * 2 + 2);
            b[i] = (byte)(Integer.parseInt(s2, 16) & 0xff);
        }
        return b;
    }
	
	
	public  byte[] read_from_network(){
		return driver.readPacket();
	}
	
	public static byte[][] read_from_file(String filename){
		fileReader fr = new fileReader(filename);
		return fr.arrayOfAob;
	}
	
	//method to print the packets
	public void print_packets(byte[][] array){
		for (int i=0;i<array.length;i++){
			//System.out.println("Packet: "+(i+1));
        	System.out.println(driver.byteArrayToString(array[i]));
		}
	}
	
	public static void print_packet(byte[] packet){
        System.out.println(driver.byteArrayToString(packet));
	}
	
	void write_to_file(byte[] array,String filename){
		fileWriter File = new fileWriter(array,filename);
	}
	
	public static void send_to_network(byte[] array){
		if (!driver.sendPacket(array)) System.out.println("Error sending packet!");
	}
	
	//print adapter names
	public static void print_adapter_names(){
		String[] adapters = driver.getAdapterNames();
		System.out.println("Number of adapters: " + adapters.length);
		//Print adapter names
		for(int i=0; i< adapters.length; i++){
			System.out.println("Device name in java: " + adapters[i]);
		}
	}
	
	public String[] get_adapters(){
		return driver.getAdapterNames();
	}
	
	//open adapter
	public  void open_adapter(String adapter_name){
		if (driver.openAdapter(adapter_name)) System.out.println("Adapter is open: "+adapter_name);
	}
	
	//divider (+-+-+-.....)
	public static void print_divider(){
		for(int i = 1;i<66;i++){
			if(i%2==0){
				System.out.print("-");
			}
			else
			System.out.print("+");
		}
		System.out.println();
	}
	
	public void print_num_header(){
	System.out.print(" 0\t\t     1\t\t\t 2\t\t     3");
	new_line();
		for(int i = 0; i<3; i++){
			for (int j = 0; j<10; j++)
				System.out.print(" "+j);
		}
		System.out.print(" 0 "+ "1");
		new_line();
	}
	
	public void new_line(){
		System.out.println();
	}
	
	//get ethernet header
	public  byte[] getEthHeader(byte[] array){
		byte[] header = new byte[14];
		for (int i = 0;i<14;i++){
			header[i] = array[i];
		}
		return header;
	}
	
	protected String byteToIP(byte[] array,int start){
		StringBuffer ip = new StringBuffer();
		for(int i = start;i<start+4;i++){
			ip.append((array[i]&0xff)+".");
		}
		return ip.deleteCharAt(ip.length()-1).toString();
	}
	
	//to check for flags, note: this looks at the byte last to first
	public boolean is_bit_set(byte value, int bitindex)
	{
	    return (value & (1 << bitindex)) != 0;
	}
	
	public  String justifyCenter( String str,
            final int width) {
		// Trim the leading and trailing whitespace ...
		char padWithChar = ' ';
		str = str != null ? str.trim() : "";
		
		int addChars = width - str.length();
		if (addChars < 0) {
		// truncate
		return str.subSequence(0, width).toString();
		}
		// Write the content ...
		int prependNumber = addChars / 2;
		int appendNumber = prependNumber;
		if ((prependNumber + appendNumber) != addChars) {
		++prependNumber;
		}
		
		final StringBuilder sb = new StringBuilder();
		
		// Prepend the pad character(s) ...
		while (prependNumber > 0) {
		sb.append(padWithChar);
		--prependNumber;
		}
		
		// Add the actual content
		sb.append(str);
		
		// Append the pad character(s) ...
		while (appendNumber > 0) {
		sb.append(padWithChar);
		--appendNumber;
		}
		
		return sb.toString();
		}
		
		}

	


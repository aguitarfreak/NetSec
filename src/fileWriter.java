import java.io.*;

public class fileWriter extends methods {
	BufferedWriter out;
	int i;
	public fileWriter(byte[] array,String filename){
		try {
			out = new BufferedWriter(new FileWriter(filename+".dat",true));
			for (i =0;i<array.length;i++){
				out.write(driver.byteToHex(array[i])+" ");
					if((i+1)%16==0)
						out.write("\n");
			}
			out.write("\n\n");
			out.close();
		}catch(IOException e){
			System.out.println("There was a problem:" + e);
		}
	}
}

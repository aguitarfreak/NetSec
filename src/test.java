import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;

public class test {
	
	public static HashMap<String, ArrayList<String>> test = new HashMap<String, ArrayList<String>>();
	static MultiMap map = new MultiHashMap( );
	
	public static void main(String[] args) {
			
			/*ArrayList<String> test = new ArrayList<String>();
			test.add("test");
				
	        map.put( "ONE", test );
	        map.put( "ONE", "WAR" );
	        map.put( "ONE", "CAR" );
	        map.put( "ONE", "WEST" );
	        map.put( "TWO", "SKY" );
	        map.put( "TWO", "WEST" );
	        map.put( "TWO", "SCHOOL" );
	        // At this point "ONE" should correspond to "TEST", "WAR", "CAR", "WEST"
	       
	        map.put("ONE", "1");
	        // The size of this collection should be two "TEST", "WEST"
	        Collection oneCollection = (Collection) map.get("ONE");
	        // This collection should be "TEST", "WEST", "SKY", "WEST", "SCHOOL"
	        Collection values = map.values( );
	        
	        System.out.println(map.toString());*/
			boolean done = false;
			boolean[] test = {false, true, true, false, false};
			
			for (int i = 0 ; i< test.length ; i++){
				if(!test[i]){
					done = false;
					break;
				}else{
					done = true;
				}
			}
			
			System.out.println(done);
	}

}

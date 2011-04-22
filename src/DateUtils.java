import java.util.Calendar;
import java.text.SimpleDateFormat;

public class DateUtils {


  public String now(String dateFormat) {
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
    return sdf.format(cal.getTime());
  }

 /* public static void  main(String arg[]) {
     System.out.println(DateUtils.now("dd MMMMM yyyy"));
     System.out.println(DateUtils.now("yyyyMMdd"));
     System.out.println(DateUtils.now("dd.MM.yy"));
     System.out.println(DateUtils.now("MM/dd/yy"));
     System.out.println(DateUtils.now("yyyy.MM.dd G 'at' hh:mm:ss z"));
     System.out.println(DateUtils.now("EEE, MMM d, ''yy"));
     System.out.println(DateUtils.now("h:mm a"));
     System.out.println(DateUtils.now("H:mm:ss:SSS"));
     System.out.println(DateUtils.now("K:mm a,z"));
     System.out.println(DateUtils.now("yyyy.MMMMM.dd 'at' hh:mm aaa"));
  }*/
}

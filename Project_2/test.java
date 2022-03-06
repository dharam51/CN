import java.time.*;
import java.util.*;
import java.text.SimpleDateFormat;
public class test {
    
    public static void main(String[] args) {
        LinkedHashMap<String, LocalDateTime> map = new LinkedHashMap<>();
        map.put("1",LocalDateTime.now());
        try{
            Thread.sleep(60);
        }catch(Exception e){

        }
       
        try{
           
            Duration duration = Duration.between(map.get("1"), LocalDateTime.now());
            int minu = duration.toMinutes();
            System.out.println(minu);
        } catch(Exception e){
            System.out.println(e);
        }

    }

    
}

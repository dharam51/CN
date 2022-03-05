import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;

public class RoverStatus{

    
    LinkedHashMap<String , LocalDateTime> time_table = new LinkedHashMap<>();

    public RoverStatus(LinkedHashMap<String , LocalDateTime> time_table){
    
        this.time_table = time_table;
    }

    public LinkedHashMap get_time_table(){
        return this.time_table;
    }

 
    public void update_time(String source_ip){
        
        time_table.put(source_ip,LocalDateTime.now());

    }

}
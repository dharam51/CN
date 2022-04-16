/**
 * Author : @Dharmendra Nasit (drn1263)
 * This file is responsible to maintain datastructure to track and modify update from adjacent rover table
 */

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.concurrent.*;

public class rover_status{

    
    ConcurrentHashMap<String , LocalDateTime> time_table = new ConcurrentHashMap<>();

    public rover_status(ConcurrentHashMap<String , LocalDateTime> time_table){
    
        this.time_table = time_table;
    }

    public ConcurrentHashMap<String, LocalDateTime> get_time_table(){
        return this.time_table;
    }

 
    public void update_time(String source_ip){
        this.time_table.put(source_ip,LocalDateTime.now());

    }

}
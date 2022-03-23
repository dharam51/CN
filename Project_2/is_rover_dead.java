/**
 *  Author : @Dharmendra Nasit
 *  This file will check continuously if update is received properly every 10s from the neighbour 
 *
 */

import java.time.*;
import java.util.*;

public class is_rover_dead extends Thread {
    
    @Override
    public void run(){
        System.out.println();
        while(true){
            try{
                Thread.sleep(1);
            } catch(Exception e){
                System.out.println(e);
            }

            /**
             * Iterate over status table and update the time corresponding to that IP address
             */
            for (Map.Entry<String, LocalDateTime> entry : start_rover.rover_status.time_table.entrySet()) {
            
                String next_hop_ip = entry.getKey();
                LocalDateTime ldt = entry.getValue();
                LocalDateTime ldt1 = LocalDateTime.now();
                Duration duration = Duration.between(ldt, ldt1);
                int difference_seconds = (int)duration.toSeconds();

                //If diff is greater than 10s update the metrics and remove its entry from status table
                if(difference_seconds > 10){
                    for(rover_routing_table each_entry : start_rover.rrt){
                        if(each_entry.get_next_hop().equalsIgnoreCase(next_hop_ip)){
                            each_entry.update_metrics(rip_packet.unreachable);
                            
                            rover_routing_table.print();

                            start_rover.rover_status.time_table.remove(next_hop_ip);
                        }
                    }
                }
                
            }

        }

    }

}

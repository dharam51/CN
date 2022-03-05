import java.time.LocalDateTime;

public class is_rover_dead extends Thread {
    
    @Override
    public void run(){

        while(true){

            for (Map.Entry<String, LocalDateTime> entry : StartRover.rover_status.time_table.entrySet()) {
                
                String next_hop_ip = entry.getKey();
                LocalDateTime ldt = entry.getValue();
                LocalDateTime ldt1 = LocalDateTime.now();
                
                
            }

        }

    }

}

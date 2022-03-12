import java.time.*;
import java.util.*;

public class is_rover_dead extends Thread {
    
    @Override
    public void run(){
        System.out.println();
        while(true){
            
            //System.out.println(StartRover.rover_status.get_time_table().size());
            try{
                Thread.sleep(1);
            } catch(Exception e){
                System.out.println(e);
            }
            for (Map.Entry<String, LocalDateTime> entry : StartRover.rover_status.time_table.entrySet()) {
            
                String next_hop_ip = entry.getKey();
                LocalDateTime ldt = entry.getValue();
                LocalDateTime ldt1 = LocalDateTime.now();
                Duration duration = Duration.between(ldt, ldt1);
                int difference_seconds = (int)duration.toSeconds();

                if(difference_seconds > 10){
                    for(RoverRoutingTable each_entry : StartRover.rrt){
                        if(each_entry.get_next_hop().equalsIgnoreCase(next_hop_ip)){
                            System.out.println("Rover "+next_hop_ip+" is unreachable !!");

                            each_entry.update_metrics(RIPPacket.unreachable);
                            
                            //print table
                            RoverRoutingTable.print();

                            //send update that rover is dead
                            //StartRover.send_my_update.send_broadcast(RIPPacket.cmd_response);

                            //Send Request Packet
                            //StartRover.send_my_update.send_broadcast(RIPPacket.cmd_request);

                            //remove entry
                            StartRover.rover_status.time_table.remove(next_hop_ip);
                        }
                    }
                }
                
            }

        }

    }

}

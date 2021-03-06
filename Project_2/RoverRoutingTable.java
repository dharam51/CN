import java.util.ArrayList;
import java.util.LinkedHashMap;

public class RoverRoutingTable {

    public static final int address_family_identified = 2;
    public static final String subnet_mask = "255.255.255.0";
    public static final int route_tag = 1;

    String destinatipn_ip;
    String next_hop;
    int metrics;

    public RoverRoutingTable( String destination_ip , String next_hop ,int metrics){
        
       this.destinatipn_ip = destination_ip;
       this.next_hop = next_hop;
       this.metrics = metrics;

    }

    public String get_destination_ip(){
        return this.destinatipn_ip;
    }

    public String get_next_hop(){
        return this.next_hop;
    }

    public int get_metrics(){
        return this.metrics;
    }

    public void update_next_hop(String updated_hop){
        this.next_hop = updated_hop;
    }

    public void update_metrics(int updated_metrics){
        this.metrics = updated_metrics;
    }

    public static void print(){
        System.out.println("###########################################################");
        System.out.println("Source IP ::: Next Hop ::: Destination ::: Cost ");
        for(RoverRoutingTable each_rrt : StartRover.rrt){
            System.out.println(RIPPacket.get_sender_ip(StartRover.rover_id)+ " ::: "+each_rrt.get_next_hop()+" ::: "+each_rrt.get_destination_ip()+" ::: "+each_rrt.get_metrics());
        }
        System.out.println("###########################################################");
    }

}

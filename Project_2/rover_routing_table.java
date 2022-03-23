/**
 * Author : @Dharmendra Nasit
 * This file will maintain rover routing table getters and setters
 * 
 */
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class rover_routing_table {

    public static final int address_family_identified = 2;
    public static final String subnet_mask = "255.255.255.0";
    public static final int route_tag = 1;

    String destinatipn_ip;
    String next_hop;
    int metrics;

    public rover_routing_table( String destination_ip , String next_hop ,int metrics){
        
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
        System.out.println("Destination ::: Next Hop  ::: Cost ");
        for(rover_routing_table each_rrt : start_rover.rrt){
            System.out.println(each_rrt.get_destination_ip()+ "/24 ::: "+each_rrt.get_next_hop()+" ::: "+each_rrt.get_metrics());
        }
        System.out.println("###########################################################");
    }

}

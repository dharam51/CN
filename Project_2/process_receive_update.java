/**
 * Author : @Dharmendra Rasikbhai Nasit
 * This file will process the receive packet from neighbor
 */
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.util.ArrayList;

public class process_receive_update extends Thread{
    
    public MulticastSocket socket;

    public process_receive_update(MulticastSocket socket){
        this.socket = socket;
    }

    @Override
    public void run(){
        while(true){
            try{
                byte[] receive_data = new byte[504];
                DatagramPacket incomingPacket = new DatagramPacket(receive_data, receive_data.length);
                socket.receive(incomingPacket);
                process_receive_bytes process = new process_receive_bytes(incomingPacket);
                process.start();
            } catch (Exception e) {
                System.out.println("Exception Occured in process_receive_update class "+e);
            }
        }
    }
    
}


class process_receive_bytes extends Thread{

    DatagramPacket packet;
    public static final char[] Hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9','a', 'b', 'c', 'd', 'e', 'f' }; 
    public process_receive_bytes(DatagramPacket dp){
        this.packet = dp;
    }

    @Override
    public void run(){

        byte[] data = packet.getData();
        String sender_id = String.valueOf(Integer.parseInt(to_hex(data[2]) ,16));

        int type =  data[0] & 0xff;

        

        /**
        1. if sender is itself do not do anything just return 
        2. if its request from another rover send my rrt table as response
        3. update time corresponds to sender's ip in rover_status
        **/

        /** 1 */
        if(start_rover.rover_id.equalsIgnoreCase(sender_id)){
            return;
        }

        /** 2 */
        if(type == rip_packet.cmd_request){
            start_rover.send_my_update.send_broadcast(rip_packet.cmd_response);
            return;
        }

        /** 3 */
        start_rover.rover_status.update_time(rip_packet.get_sender_ip(sender_id));

        
        int index = 4;
        int i = index;
        ArrayList<rover_routing_table> temp_rrt = new ArrayList<>();
        /**
         * Iterate over the received router table and pass this new router table 
         * so that current router can update its own table.
         */
        while(i < data.length){

            int address_family_identified = Integer.parseInt(to_hex(data[i])+ to_hex(data[++i]), 16);
            int route_tag = Integer.parseInt(to_hex(data[++i])+ to_hex(data[++i]), 16);
            String ip_address = Long.parseLong(to_hex(data[++i]) ,16) + "."+ Long.parseLong(to_hex(data[++i]) ,16) + "." + Long.parseLong(to_hex(data[++i]) ,16) + "."+ Long.parseLong(to_hex(data[++i]) ,16);
            String subnet  = Long.parseLong(to_hex(data[++i]) ,16) + "."+ Long.parseLong(to_hex(data[++i]) ,16) + "." + Long.parseLong(to_hex(data[++i]) ,16) + "."+ Long.parseLong(to_hex(data[++i]) ,16);
            String next_hop = Long.parseLong(to_hex(data[++i]) ,16) + "."+ Long.parseLong(to_hex(data[++i]) ,16) + "." + Long.parseLong(to_hex(data[++i]) ,16) + "."+ Long.parseLong(to_hex(data[++i]) ,16);
            int metrics = Integer.parseInt( to_hex(data[++i]) + to_hex(data[++i]) + to_hex(data[++i]) + to_hex(data[++i]) ,16 );
            i++;
            if(address_family_identified == 0 &&  route_tag == 0 && ip_address.equalsIgnoreCase("0.0.0.0") && subnet.equalsIgnoreCase("0.0.0.0") &&  next_hop .equalsIgnoreCase("0.0.0.0") ) {     
                break; 
            }
            temp_rrt.add(new rover_routing_table(ip_address , next_hop , metrics));

        }

        update_table(sender_id , temp_rrt );
    }

    public static String to_hex(byte val){
        return Hex[(0xF0 & val) >>> 4] + "" +Hex[(0x0F & val)];
    }


    public void update_table(String sender_id , ArrayList<rover_routing_table> new_rrt){

        String sender_ip = rip_packet.get_sender_ip(sender_id);
        boolean is_sender_present = false;
        boolean is_table_changed = false;

        // ** BASE CASE **
        //if current table size is zero just add sender details in my routing table
        if(start_rover.rrt.size() == 0){
            start_rover.rrt.add(new rover_routing_table( sender_ip , sender_ip , 1));
        }
        //if sender's IP address matches with any of my routing table destination entry update 
        //cost to 1
        for(rover_routing_table each_rrt : start_rover.rrt){
            if(each_rrt.get_destination_ip().equalsIgnoreCase(sender_ip)){
                is_sender_present = true;
                each_rrt.update_next_hop(sender_ip);
                if(each_rrt.get_metrics() != 1){
                    each_rrt.update_metrics(1);
                    is_table_changed = true;
                    
                }
            }
        }
        //If entry not matches add entry to my routing table
        if(!is_sender_present ){
            is_table_changed = true;
            start_rover.rrt.add(new rover_routing_table( sender_ip , sender_ip , 1));
           
        }
        /**
         * Logic for Updating my routing table based on various conditions
         */
        for(rover_routing_table each_entry_new : new_rrt){
            boolean did_address_matched = false;
            int new_cost = 1 + each_entry_new.get_metrics();
            for(rover_routing_table each_entry_current : start_rover.rrt){
                if(each_entry_new.get_destination_ip().equalsIgnoreCase(each_entry_current.get_destination_ip())){
                    did_address_matched = true;
                    if( new_cost != each_entry_current.get_metrics() && each_entry_current.get_next_hop().equalsIgnoreCase(sender_ip) ){
                        if(new_cost >= rip_packet.unreachable){
                            each_entry_current.update_metrics(rip_packet.unreachable);
                        } else{
                            each_entry_current.update_metrics(new_cost);
                        }
                        is_table_changed = true;
                    } else if(new_cost < each_entry_current.get_metrics()){
                            each_entry_current.update_metrics(new_cost);
                            each_entry_current.update_next_hop(sender_ip);
                            is_table_changed = true;
                    }
                }
            }
            if(!did_address_matched && !rip_packet.get_sender_ip(start_rover.rover_id).equalsIgnoreCase(sender_ip)){
                is_table_changed = true;
                start_rover.rrt.add(new rover_routing_table (each_entry_new.get_destination_ip(),sender_ip,new_cost));  
            }
        }
        //print routing table if changed
        if(is_table_changed){
            rover_routing_table.print();
        }

    }

}
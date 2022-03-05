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

            byte[] receive_data = new byte[504];
            DatagramPacket incomingPacket = new DatagramPacket(receive_data, receive_data.length);
            socket.receive(incomingPacket);


        }
    }
    
}


public class process_receive_bytes extends Thread{

    DatagramPacket packet;

    public process_receive_bytes(DatagramPacket dp){
        this.packet = dp;
    }

    @Override
    public void run(){

        byte[] data = packet.getData();
        String sender_id = String.valueOf(Integer.parseInt(data[2] ,16));

        /**
        To Implement :
        1. if sender is itself do not do anything just return 
        2. update time corresponds to sender's ip in RoverStatus
        **/

        /** 1 */
        if(StartRover.rover_id == sender_id ){
            return;
        }

        /** 2 */
        StartRover.rover_status.update_time(RIPPacket.get_sender_ip(sender_id));
        
        int index = 4;
        int i = index;
        ArrayList<RoverRoutingTable> temp_rrt = new ArrayList<>();
        while(i < data.length){

            int address_family_identified = Integer.parseInt(to_hex(data[i])+ to_hex(data(++i)), 16);
            int route_tag = Integer.parseInt(to_hex(data[++i])+ to_hex(data(++i)), 16);
            String ip_address = Long.parseLong(to_hex(data[++i]) ,16) + "."+ Long.parseLong(to_hex(data[++i]) ,16) + "." + Long.parseLong(to_hex(data[++i]) ,16) + "."+ Long.parseLong(to_hex(data[++i]) ,16);
            String subnet  = Long.parseLong(to_hex(data[++i]) ,16) + "."+ Long.parseLong(to_hex(data[++i]) ,16) + "." + Long.parseLong(to_hex(data[++i]) ,16) + "."+ Long.parseLong(to_hex(data[++i]) ,16);
            String next_hop = Long.parseLong(to_hex(data[++i]) ,16) + "."+ Long.parseLong(to_hex(data[++i]) ,16) + "." + Long.parseLong(to_hex(data[++i]) ,16) + "."+ Long.parseLong(to_hex(data[++i]) ,16);
            int metrics = Integer.parseInt( to_hex(data[++i]) + Integer.parseInt(data[++i]) + Integer.parseInt(data[++i]) + Integer.parseInt(data[++i]) ,16 );
            i++;
            if(address_family_identified == 0 && 
                    route_tag == 0 &&
                    ip_address.equalsIgnoreCase("0.0.0.0") &&
                    subnet.equalsIgnoreCase("0.0.0.0") && 
                    next_hop .equalsIgnoreCase("0.0.0.0") ) {
                        break;
                    }
            
            temp_rrt.add(new RoverRoutingTable(ip_address , next_hop , metrics));

        }

        MyRoutingTableUpdate(sender_id , temp_rrt );
    }

    public static String to_hex(byte val){
        return Hex[(0xF0 & val) >>> 4] + "" +Hex[(0x0F & val)];
    }


    public void MyRoutingTableUpdate(String sender_id , ArrayList<RoverRoutingTable> new_rrt){

        String sender_ip = RIPPacket.get_sender_ip(sender_id);
        boolean is_sender_present = false;
        
        for(RoverRoutingTable each_rrt : StartRover.rrt){

            if(each_rrt.get_destination_ip.equalsIgnoreCase(sender_ip)){
                is_sender_present = true;
                StartRover.rrt.update_next_hop(sender_ip);
                StartRover.rrt.update_metrics(1);

            }

        }

        if(StartRover.rrt.size() == 0 || !is_sender_present ){

            StartRover.rrt.add(new RoverRoutingTable( sender_ip , sender_ip , 1));

        }

        for(RoverRoutingTable each_entry_new : new_rrt){

            boolean did_address_matched = false;
            for(RoverRoutingTable each_entry_current : StartRover.rrt){

                if(each_entry_new.get_destination_ip.equalsIgnoreCase(each_entry_current.destination_ip)){
                    
                    did_address_matched = true;

                    if(each_entry_current.get_next_hop.equalsIgnoreCase(sender_ip)){

                        if(1 + each_entry_new.get_metrics() >= RIPPacket.unreachable){

                            each_entry_current.update_metrics(RIPPacket.unreachable);

                        } else{
                            each_entry_current.update_metrics(1 + each_entry_new.get_metrics());
                        }

                    } else{

                        if(1+ each_entry_new.get_metrics() < each_entry_current.get_metrics){

                            each_entry_current.update_metrics(1 + each_entry_new.get_metrics());
                            each_entry_current.update_next_hop(sender_ip);

                        }

                    }

                }

            }

            if(!did_address_matched && !RIPPacket.get_sender_ip(StartRover.rover_id).equalsIgnoreCase(sender_ip)){

                StartRover.rrt.add(new RoverRoutingTable (
                    each_entry_new.get_destination_ip(),
                    sender_ip,
                    each_entry_new.get_metrics()));

            }

        }

    }

}
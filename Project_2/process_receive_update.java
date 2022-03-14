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
        To Implement :
        1. if sender is itself do not do anything just return 
        2. if its request from another rover send my rrt table as response
        3. update time corresponds to sender's ip in RoverStatus
        **/

        /** 1 */
        if(StartRover.rover_id.equalsIgnoreCase(sender_id)){
            return;
        }

        /** 2 */
        if(type == RIPPacket.cmd_request){
            StartRover.send_my_update.send_broadcast(RIPPacket.cmd_response);
            return;
        }

        /** 3 */
        StartRover.rover_status.update_time(RIPPacket.get_sender_ip(sender_id));

        
        int index = 4;
        int i = index;
        ArrayList<RoverRoutingTable> temp_rrt = new ArrayList<>();
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
        boolean is_table_changed = false;

        

        for(RoverRoutingTable each_rrt : StartRover.rrt){

            if(each_rrt.get_destination_ip().equalsIgnoreCase(sender_ip)){
                is_sender_present = true;
                each_rrt.update_next_hop(sender_ip);
                if(each_rrt.get_metrics() != 1){
                    each_rrt.update_metrics(1);
                    is_table_changed = true;
                    
                }
            }

        }

        if(StartRover.rrt.size() == 0 || !is_sender_present ){
            is_table_changed = true;
            StartRover.rrt.add(new RoverRoutingTable( sender_ip , sender_ip , 1));
           
        }

        for(RoverRoutingTable each_entry_new : new_rrt){

            boolean did_address_matched = false;
            for(RoverRoutingTable each_entry_current : StartRover.rrt){

                if(each_entry_new.get_destination_ip().equalsIgnoreCase(each_entry_current.get_destination_ip())){
                    
                    did_address_matched = true;

                    if(each_entry_current.get_next_hop().equalsIgnoreCase(sender_ip) && 1 + each_entry_new.get_metrics() != each_entry_current.get_metrics() ){

                        if(1 + each_entry_new.get_metrics() >= RIPPacket.unreachable){

                            each_entry_current.update_metrics(RIPPacket.unreachable);

                        } else{
                            each_entry_current.update_metrics(1 + each_entry_new.get_metrics());
                        }
                        is_table_changed = true;
                       

                    } else{

                        if(each_entry_new.get_metrics() >= RIPPacket.unreachable){
                            each_entry_current.update_metrics(RIPPacket.unreachable);
                        }
                        else if(1+ each_entry_new.get_metrics() < each_entry_current.get_metrics()){

                            each_entry_current.update_metrics(1 + each_entry_new.get_metrics());
                            each_entry_current.update_next_hop(sender_ip);
                            is_table_changed = true;

                        }

                    }

                }

            }

            if(!did_address_matched && !RIPPacket.get_sender_ip(StartRover.rover_id).equalsIgnoreCase(sender_ip)){
                is_table_changed = true;
                StartRover.rrt.add(new RoverRoutingTable (each_entry_new.get_destination_ip(),sender_ip,1 + each_entry_new.get_metrics()));
                System.out.println("Here 5");
            }

        }

        //print routing table
        
        if(is_table_changed){
            RoverRoutingTable.print();
        }

    }

}
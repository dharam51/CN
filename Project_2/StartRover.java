import java.net.InetAddress;
import java.net.MulticastSocket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class StartRover {

    public static String multicast_ip = "224.0.0.9";
    public static int port ;
    public static String rover_id ; 

    public static ArrayList<RoverRoutingTable> rrt = new ArrayList<>();
    public static RoverStatus rover_status;
    public static is_rover_dead is_dead = new is_rover_dead();
    public static send_update send_my_update = new send_update();
    public static process_receive_update process_my_update;
    

    public static void main(String[] args) {

        rover_id = args[0];
        port = Integer.parseInt(args[1]);

        try{
            InetAddress group = InetAddress.getByName(multicast_ip);
            MulticastSocket rover_socket = new MulticastSocket(port);
            rover_socket.joinGroup(InetAddress.getByName(multicast_ip));

            //initialise cost to itself as zero
            rrt.add(new RoverRoutingTable(RIPPacket.get_sender_ip(rover_id) , RIPPacket.get_sender_ip(rover_id) , 0));

            //Initialise timeout process table hashmap
            rover_status = new RoverStatus(new LinkedHashMap<String, LocalDateTime>());

            //Start is rover dead Thread
            is_dead.start();

            //start broadcast thread
            send_my_update.start();

            //start receive thread
            process_my_update = new process_receive_update(rover_socket);
            process_my_update.start();

            //send Request packet
            send_my_update.send_broadcast(RIPPacket.cmd_request);

            
        } catch(Exception e){
            System.out.println("Exception Occured in StartRover.java "+e);
        }

    }


}



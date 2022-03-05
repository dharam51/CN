import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;

public class StartRover {

    public static String multicast_ip = "224.0.0.9";
    public static int port = 6659;
    public static ArrayList<RoverRoutingTable> rrt = new ArrayList<>();
    public static String rover_id ; 
    public static RoverStatus rover_status;
    
    

    public static void main(String[] args) {

        rover_id = args[1];

       
        InetAddress group = InetAddress.getByName(multicast_ip);
        MulticastSocket rover_socket = new MulticastSocket(port);

        //Initialise timeout process table hashmap
        rover_socket.joinGroup(InetAddress.getByName(multicast_ip));
        process_receive_update(rover_socket);




        byte[] data;
        if(rrt.routing_table.size() == 0){
            

        }


    }


}



import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.net.*;

public class send_update extends Thread {
    
    public static int broadcast_interval  = 5;

    @Override
    public void run(){

        while(true){
            try{
                send_broadcast(rip_packet.cmd_response);
                Thread.sleep(send_update.broadcast_interval*1000);
            } catch (Exception e){
                System.out.println("Excpetion occured in send_update.java "+e);
            }
        }

        
    }

    public void send_broadcast(int type){

            rip_packet myRIP;

            if(type == rip_packet.cmd_response){
                myRIP = new rip_packet(rip_packet.cmd_response  , start_rover.rrt , start_rover.rover_id);
            }
            else{
                myRIP = new rip_packet(rip_packet.cmd_request  , start_rover.rrt , start_rover.rover_id);
            }
           
            byte[] data_to_send = myRIP.form_rip_packet();

            try {
                DatagramSocket mySocket = new DatagramSocket();
                InetAddress grp = null;
                grp = InetAddress.getByName(start_rover.multicast_ip);
            

                Integer port = start_rover.port;

                DatagramPacket packet = new DatagramPacket(data_to_send , data_to_send.length, grp, port);
                mySocket.send(packet);

            } catch (Exception e) {
                System.out.println("Exception Occured in send_update class "+e);
            }

    }

}
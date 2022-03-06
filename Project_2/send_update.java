import java.net.DatagramSocket;
import java.net.InetAddress;

public class send_update extends Thread {
    
    public static int broadcast_interval  = 5;

    @Override
    public void run(){

        while(true){
        
            send_broadcast(RIPPacket.cmd_response);
            Thread.sleep(send_update.broadcast_interval*1000);
        }

        
    }

    public void send_broadcast(int type){

            RIPPacket myRIP;

            if(type == RIPPacket.cmd_response){
                myRIP = new RIPPacket(RIPPacket.cmd_response  , StartRover.rrt , StartRover.rover_id);
            }
            else{
                myRIP = new RIPPacket(RIPPacket.cmd_request  , StartRover.rrt , StartRover.rover_id);
            }
           
            byte[] data_to_send = myRIP.form_rip_packet();

            DatagramSocket mySocket = new DatagramSocket();
            InetAddress grp = null;
            try {
                grp = InetAddress.getByName(StartRover.multicast_ip);
            } catch (UnknownHostException e) {
                System.out.println("Exception Occured in send_update class "+e);
            }

            Integer port = StartRover.port;

            DatagramPacket packet = new DatagramPacket(data_to_send , data_to_send.length, grp, port);
            mySocket.send(packet);

    }

}
/**
 * Author : @Dharmendra Rasikbhai Nasit
 */

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.net.*;
import java.io.*;
public class start_rover {

    public static String multicast_ip = "224.0.0.9";
    public static int port = 6659;
    public static rover_receiver rr = new rover_receiver();
    public static String rover_id;
    public static MulticastSocket rover_socket ;
    public static boolean is_image_formed = false;

    public static void main(String[] args) {

            rover_id = args[0];
            
            int is_rover_sending = Integer.parseInt(args[1]);
            
            try{

                //join rover socket to group
                Scanner sc = new Scanner(System.in);
                rover_socket = new MulticastSocket(port);
                InetAddress group = InetAddress.getByName(multicast_ip);
                
                rover_socket.joinGroup(InetAddress.getByName(multicast_ip));

                //start rover_receiver
                rr.start();

                if(is_rover_sending == 1){
                    System.out.println("Enter path of image to send to NASA !!");
                    String path = sc.next();
                    send_from_rover.send_cp_packet(0 , path);
                }
                
                      
            } catch (Exception  e){
                System.out.println("Exception Occured in socket creation !!" + e);
            }


    }
    
}

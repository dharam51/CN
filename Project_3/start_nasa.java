/**
 * Author : @Dharmendra Rasikbhai Nasit
 * 
 */
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.net.*;
import java.io.*;
public class start_nasa {

    public static String multicast_ip = "224.0.0.9";
    public static int port = 6659;
    public static String NASA_IP = "10.0.1.0";
    public static nasa_receiver nr = new nasa_receiver();
    public static MulticastSocket nasa_socket ;

    public static void main(String[] args) {
            
            
            int is_nasa_sending = Integer.parseInt(args[0]);
           
            try{

                //join NASA socket to group
                Scanner sc = new Scanner(System.in);
                nasa_socket = new MulticastSocket(port);
                InetAddress group = InetAddress.getByName(multicast_ip);
                
                nasa_socket.joinGroup(InetAddress.getByName(multicast_ip));

                  
                //start nasa_receiver
                nr.start();
                

                nasa_send ns = new nasa_send();
                ns.start();

                if (is_nasa_sending == 1){
                    System.out.println("Enter Command to send !!");
                    int command = sc.nextInt();
                    System.out.println("Enter destination rover to whom command needs to be send !!");
                    String dest = sc.next();
                    nasa_send.send_command_packet(command , dest);
                }
                

    
            } catch (Exception  e){
                System.out.println("Exception Occured in socket creation !!" + e);
            }


    }
    
}

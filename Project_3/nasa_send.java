/**
 * Author : @Dharmendra Rasikbhai Nasit
 */

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.net.*;
import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.net.*;

public class nasa_send extends Thread {

    
    public static ConcurrentHashMap<String , Integer> receive_status = new ConcurrentHashMap<>();
    
    
    //This method forms command packet and send it to destined rover
    public static void send_command_packet(int command , String dest){

        String key = "10.0."+dest+".0,1,"+command;
        receive_status.put(key,0);
       
        byte[] header = send_from_rover.return_header(0,-1,1,1,1,"10.0.1.0","10.0."+dest+".0");
        byte[] total_data_to_send = new byte[19];
        
        for(int i = 0 ; i < header.length ;i++) total_data_to_send[i] = header[i];
        total_data_to_send[18] = (byte) command;

        try {
            DatagramSocket mySocket = new DatagramSocket();
            InetAddress grp = null;
            grp = InetAddress.getByName(start_nasa.multicast_ip);
        

            Integer port = start_nasa.port;

            DatagramPacket packet = new DatagramPacket(total_data_to_send , total_data_to_send.length, grp, port);
            mySocket.send(packet);

        } catch (Exception e) {
            System.out.println("Exception Occured in nasa_send.java file :: "+e);
        }

    }

   
    @Override
    public void run() {
       while(true){
        try{
            Thread.sleep(5000);
            send_command_again();
        } catch (Exception e) {}
       }
    }

    /**
     * This method will send command again to that specific rover whose ack is not yet received
     */
    public static void send_command_again(){
    
        for (Map.Entry<String , Integer> each_entry : receive_status.entrySet()) {
        
            String key = each_entry.getKey();
            String[] key_arr = key.split(",");
            String destination_ip = key_arr[0];
            int command_data = Integer.parseInt(key_arr[2]);

            System.out.println("Sending Command again !!");

            byte[] total_data_to_send = new byte[19];
            total_data_to_send = send_from_rover.return_header(0,-1,1,1,1,"10.0.1.0",destination_ip);
            total_data_to_send[18] = (byte) command_data;
            try {
                DatagramSocket mySocket = new DatagramSocket();
                InetAddress grp = null;
                grp = InetAddress.getByName(start_nasa.multicast_ip);
                Integer port = start_nasa.port;
                DatagramPacket packet = new DatagramPacket(total_data_to_send , total_data_to_send.length, grp, port);
                mySocket.send(packet);
    
            } catch (Exception e) {
                System.out.println("Exception Occured in nasa_receiver.java file (send_command_again method) :: "+e);
            }
        }   
    }
}

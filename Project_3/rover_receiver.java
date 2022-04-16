/**
 * Author : @Dharmendra Rasikbhai Nasit
 */

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.net.*;
import java.io.*;
public  class rover_receiver extends Thread{

        public static LinkedHashMap<Integer, String> command_mapping = new LinkedHashMap<>();

        @Override
        public void run(){
            command_mapping.put(1, "Move Forward");
            command_mapping.put(2 , "Fly back to Earth");
            while(true){
                try{

                    byte[] data = new byte[18 + send_from_rover.size_of_packet];
                    DatagramPacket incomingPacket = new DatagramPacket(data, data.length);
                    start_rover.rover_socket.receive(incomingPacket);
                    byte[] total_data_to_send = incomingPacket.getData();
                    
                    process_receive_bytes(total_data_to_send);
                    
                } catch (Exception e){
                    System.out.println(" Exception Occured in Socket receive in rover_receiver.java :: "+e);
                }

            }
            
        }

    
    /**
     * This method will send packet to NASA based on condition
     * 1. Send ACK for command packet if its command packet
     * 2. Send Image Packet again if it's not command packet
     */
    public static void process_receive_bytes(byte[] arr){

        int i = 3;
        String destination_ip = Long.parseLong(nasa_receiver.to_hex(arr[++i]) ,16) + "."+ Long.parseLong(nasa_receiver.to_hex(arr[++i]) ,16) + "." + Long.parseLong(nasa_receiver.to_hex(arr[++i]) ,16) + "."+ Long.parseLong(nasa_receiver.to_hex(arr[++i]) ,16);
        
        if(destination_ip.equalsIgnoreCase("10.0."+start_rover.rover_id+".0")){
           

            int flags = Integer.parseInt(nasa_receiver.to_hex(arr[17]),16);
            
           
            //Send command ack to NASA
            if ((flags & (1 << (1 - 1))) > 0){

                int cmd = Integer.parseInt(nasa_receiver.to_hex(arr[18]),16);
                System.out.println("Received command from NASA to :: "+command_mapping.get(cmd));
                System.out.println("Sending ACK for the command received");
                
                byte[] total_data_to_send = new byte[19];

                byte[] header = send_from_rover.return_header(0,-1,1,1,1,destination_ip,"10.0.1.0");
                for (int j  = 0 ; j< header.length ; j++ ) total_data_to_send[j] = header[j];

                total_data_to_send[18] = arr[18];

                
                try {
                    DatagramSocket mySocket = new DatagramSocket();
                    InetAddress grp = null;
                    grp = InetAddress.getByName(start_rover.multicast_ip);
                
        
                    Integer port = start_rover.port;
        
                    DatagramPacket packet = new DatagramPacket(total_data_to_send , total_data_to_send.length, grp, port);
                    mySocket.send(packet);
        
                } catch (Exception e) {
                    System.out.println("Exception Occured in rover_receiver.java file (process_receive_bytes method) :: "+e);
                }
            
            }
       
             

            //resend dropped packet again
            else {
                int ackno = (arr[12] & 0xFF ) << 24 | (arr[13] & 0xFF) << 16 | (arr[14] & 0xFF) << 8 | (arr[15] & 0xFF); 
                System.out.println("Received Missing no from nasa :: "+ackno+ " sending this packet again");
                int length = Math.min(send_from_rover.size_of_packet , send_from_rover.data_to_send.length - ackno);
                int end = 0;        
                if ((flags & (1 << (2 - 1))) > 0) end  = 1;
                
                
                byte[] data_resend = new byte[length];
                int ind = 0;

                
                for (int j = ackno ; j < length ;j++) {
                    data_resend[ind] = send_from_rover.data_to_send[j];
                    ind ++;
                    
                }

                byte[] header = send_from_rover.return_header(ackno,-1,0,end,length,destination_ip,"10.0.1.0");

                byte[] total_data_to_send = new byte[header.length + length];

                for (int j = 0 ;j < header.length ;j++) total_data_to_send[j] = header[j];

                ind = header.length;

                for(int j = 0 ;j< data_resend.length ;j++) {
                    total_data_to_send[ind] = data_resend[j];
                    ind ++;
                }
               
                try {
                    DatagramSocket mySocket = new DatagramSocket();
                    InetAddress grp = null;
                    grp = InetAddress.getByName(start_rover.multicast_ip);
                
        
                    Integer port = start_rover.port;
        
                    DatagramPacket packet = new DatagramPacket(total_data_to_send , total_data_to_send.length, grp, port);
                    mySocket.send(packet);
                    mySocket.close();
                } catch (Exception e) {
                    System.out.println("Exception Occured in rover_receiver.java file (process_receive_bytes method inside else) :: "+e);
                }
            
            
            }
        }
    }
}

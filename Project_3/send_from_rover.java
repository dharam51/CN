/**
 * Author : @Dharmendra Rasikbhai NAsit
 */

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.net.*;
import java.io.*;

public class send_from_rover {
    
    public static byte[] data_to_send;
    public static int size_of_packet = 1300;

    public static byte[] image_to_byte(String filepath){

        try{

            File f = new File(filepath);
            FileInputStream ips = new FileInputStream(f);
            byte[] arr = new byte[(int)f.length()];
            ips.read(arr);
            ips.close();
            return arr;

        } catch (Exception e){
            System.out.println("Exception Occured in image to byte conversion !!");
            byte[] a = new byte[1];
            return a;
        }
        

    }

    /**
     * This method will form packets of image
     * Include header to packets and send it to NASA
     */
    public static void send_cp_packet(int is_command , String data){
    
        if ( is_command == 0){
            
            data_to_send = image_to_byte(data);
            
            int index = 0;

            while (index < data_to_send.length){

                

                byte[] total_data_to_send = new byte[18+size_of_packet];

                int is_end_packet = 0;


                if (index + size_of_packet >= data_to_send.length)  {
                    is_end_packet = 1;
                    index = Math.min(index + size_of_packet , data_to_send.length);
                    continue;
                }

                
                
                String src = "10.0."+start_rover.rover_id+".0";

                int length = Math.min(size_of_packet , data_to_send.length - index);


                byte[] header = return_header( index, 0 , is_command, is_end_packet, length, src, start_nasa.NASA_IP);

                for (int i = 0 ; i < header.length ;i++){

                    total_data_to_send[i] = header[i];

                }

                int ind = header.length;

                for (int i = index ; i < Math.min(index + size_of_packet , data_to_send.length) ; i++) total_data_to_send[ind++] = data_to_send[i];

                index = Math.min(index + size_of_packet , data_to_send.length);

                //send packet by packet to NASA
                try {
                    DatagramSocket mySocket = new DatagramSocket();
                    InetAddress grp = null;
                    grp = InetAddress.getByName(start_rover.multicast_ip);
                
    
                    Integer port = start_rover.port;
    
                    DatagramPacket packet = new DatagramPacket(total_data_to_send , total_data_to_send.length, grp, port);
                    mySocket.send(packet);
                    mySocket.close();
    
                } catch (Exception e) {
                    System.out.println("Exception Occured in send_from_rover.java file :: "+e);
                   
                }   
                
            }
        }

    }

    /**
     * This method will form header and return byte array of header
     */
    public static byte[] return_header(int seqno , int ackno , int is_command , int is_end_packet , int length , String src , String dest){

        byte[] arr = new byte[18];
        int i = 0;
        String[] s = src.split("\\.");
        for(int x = 0; x < 4; x++)  {
            arr[i++] = (byte) (Integer.parseInt(s[x],10));
        }

        s = dest.split("\\.");
        for(int x = 0; x < 4; x++)  {
            arr[i++] = (byte) (Integer.parseInt(s[x],10));
        }

        
        arr[i++] = (byte)(seqno >>> 24);
        arr[i++] =   (byte)(seqno >>> 16);
        arr[i++]  =  (byte)(seqno >>> 8);
        arr[i++]  =  (byte)(seqno);

        
        arr[i++] = (byte)(ackno >>> 24);
        arr[i++] =  (byte)(ackno >>> 16);
        arr[i++]  = (byte)(ackno >>> 8);
        arr[i++]  = (byte)(ackno);

        arr[i++] = (byte) length;

        byte b = 0;
        if (is_command == 1){
            b = (byte) (b | (1 << 0));
        }
        if (is_end_packet == 1){
           
            b = (byte) (b | (1 << 1));
        }
       
        arr[i++] = b;
        
        return arr;

    }
    
}

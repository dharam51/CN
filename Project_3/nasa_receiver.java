/**
 * Author : @Dharmendra Rasikbhai Nasit
 * 
 */
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.net.*;
import java.io.*;
import javax.imageio.ImageIO;


import java.awt.image.BufferedImage;
public class nasa_receiver extends Thread {

    /**
     * Below Data Structure progress and progress_set is used to track
     * progress from each rover regarding image received.
     * 
     **/
    public static ConcurrentHashMap<String , byte[]> progress = new ConcurrentHashMap<>();
    public static HashSet<String> progress_set = new HashSet<>();
    public static final char[] Hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9','a', 'b', 'c', 'd', 'e', 'f' }; 

    
    public static String to_hex(byte val){
        return Hex[(0xF0 & val) >>> 4] + "" +Hex[(0x0F & val)];
    }

    /**
     * Below Thread will run continuously to get data from socket
     */
        @Override
        public void run(){
            
           
            while(true){
                try{
                    

                    byte[] data = new byte[18+send_from_rover.size_of_packet];
                    DatagramPacket incomingPacket = new DatagramPacket(data, data.length);
                    start_nasa.nasa_socket.receive(incomingPacket);
                    byte[] data_received = incomingPacket.getData();
                    int length = Integer.parseInt(to_hex(data_received[16]), 16);
                    String src_ip = Long.parseLong(to_hex(data_received[0]) ,16) + "."+ Long.parseLong(to_hex(data_received[1]) ,16) + "." + Long.parseLong(to_hex(data_received[2]) ,16) + "."+ Long.parseLong(to_hex(data_received[3]) ,16);
                    String destination_ip = Long.parseLong(to_hex(data_received[4]) ,16) + "."+ Long.parseLong(to_hex(data_received[5]) ,16) + "." + Long.parseLong(to_hex(data_received[6]) ,16) + "."+ Long.parseLong(to_hex(data_received[7]) ,16);
                    int seq_no  = (data_received[8] & 0xFF)  << 24 | (data_received[9] & 0xFF) << 16 | (data_received[10] & 0xFF) << 8 | (data_received[11] & 0xFF); 

                    String h_k = src_ip+","+seq_no;
                    int flags = Integer.parseInt(to_hex(data_received[17]),16);
                    
                    //if packet is destined for NASA proceed further
                    if(destination_ip.equalsIgnoreCase("10.0.1.0")){
                        
                        //Condition to check if packet is received from new rover and packet is not command packet
                        if (!progress_set.contains(src_ip) && ! ((flags & (1 << (1 - 1))) > 0) ){
                          
                            for_each_rover_thread frt = new for_each_rover_thread(src_ip);
                            frt.start();
                            progress_set.add(src_ip);
                            progress.put(h_k, data_received);
                        }

                        //Condition to check if packet is ack for command packet and update data structure
                        else if ((flags & (1 << (1 - 1))) > 0 ) {
                            
                            //remove that key in receive hashmap as ack for that command is received
                            String hash_key = src_ip+",1,"+Integer.parseInt(to_hex(data_received[18]),16);
                            System.out.println("ACK received from "+src_ip+" for command "+data_received[18]);
                            if (nasa_send.receive_status.containsKey(hash_key)) nasa_send.receive_status.remove(hash_key);
                            
                        } 

                        else progress.put(h_k, data_received);
                    }
                    
                } catch (Exception e){
                    System.out.println(" Exception Occured in Socket receive in nasa_receiver.java :: "+e);
                }

            }
        }

    /**
     * This thread will run for each new rover and track its progress and if 
     * all packet received write image to disk
     * 
     */
    public class for_each_rover_thread extends Thread{

        public static TreeMap<Integer , byte[]> main_table = new TreeMap();
        
        public static int is_end = 0;
        public static boolean is_image_formed = false;
        
        static String scip;
        for_each_rover_thread(String sci){
            scip = sci;
        }

       
        @Override
        public void run() {
            receiver_thread rt = new receiver_thread(scip); 
            rt.start();
            while(true){
                boolean status = process_receive_bytes(scip);
                if ( status || is_image_formed) return;    
            }
       
        }

        /**
         * This method stores progress for each rover 
         * so for received packet no need to ask for data again
         */
        public static boolean process_receive_bytes( String scip ){
       
            for (Map.Entry<String , byte[]> each : progress.entrySet()) {

                String[] key_arr = each.getKey().split(",");
                String ss = key_arr[0];
                byte[] arr;

                if (ss.equalsIgnoreCase(scip)){
                

                    arr = each.getValue();

                    //remove from progress data structure as data with this seq number is already processed.
                    progress.remove(each.getKey());

                    int p = 3;
                    String destination_ip = Long.parseLong(nasa_receiver.to_hex(arr[++p]) ,16) + "."+ Long.parseLong(nasa_receiver.to_hex(arr[++p]) ,16) + "." + Long.parseLong(nasa_receiver.to_hex(arr[++p]) ,16) + "."+ Long.parseLong(nasa_receiver.to_hex(arr[++p]) ,16);
                    if (destination_ip.equalsIgnoreCase("10.0.1.0")){

                        int seq_no  = (arr[8] & 0xFF)  << 24 | (arr[9] & 0xFF) << 16 | (arr[10] & 0xFF) << 8 | (arr[11] & 0xFF); 

                        int hashed_key = seq_no;

                        int ui = 0;
                        byte[] _data = new byte[arr.length - 18];
                        for(int iii = 18; iii < arr.length ; iii++) {
                            _data[ui] = arr[iii];
                            ui ++;
                        }
                        if (!main_table.containsKey(hashed_key)) {
                            main_table.put(hashed_key , _data );
                        }
                        else main_table.put(hashed_key , _data );
                        int flags = Integer.parseInt(to_hex(arr[17]),16);
                        if ((flags & (1 << (2 - 1))) > 0) {
                            is_end = 1;           
                        }
                    }
                }   
            }   
            return false;                     
        }

        /**
         * This thread will track if any packet is missing
         * If its missing ask that specific rover to resend it again
         */
        public static class receiver_thread extends Thread{
            public String src_ip;
            receiver_thread(String src_ip){
                this.src_ip = src_ip;
            }
            @Override
            public void run(){

                while (true){
                    try{
                        Thread.sleep(5000);
                        boolean can_be_end = send_missing_data_packet(src_ip);
                    
                    if (can_be_end) {
                        progress_set.remove(src_ip);
                        return ;
                    }
                    } catch (Exception e){
                        System.out.println(" Exception Occured in sleep in nasa_receiver.java" + e);
                    }
                }
            }
        }

        public static boolean send_missing_data_packet(String src_ip){

            int next  = 0;
            int f = 1;
            int ackno = -1;
            for (Map.Entry<Integer , byte[]> each : main_table.entrySet()){

                //Some Intermediate packet is missing ask rover to resend
                if(each.getKey() != next){
                    System.out.println("Packet with No. "+next+" did not received requesting it again from rover !!");
                    
                    ackno = next;
                    f = 0;

                    byte[] total_data_to_send = send_from_rover.return_header(0,ackno,0, 0 ,0,"10.0.1.0",src_ip);
                        try {
                            DatagramSocket mySocket = new DatagramSocket();
                            InetAddress grp = null;
                            grp = InetAddress.getByName(start_nasa.multicast_ip);
                        
                        
                            Integer port = start_nasa.port;
                        
                            DatagramPacket packet = new DatagramPacket(total_data_to_send , total_data_to_send.length, grp, port);
                            mySocket.send(packet);
                            mySocket.close();
                        
                        } catch (Exception e) {
                            System.out.println("Exception Occured in nasa_receiver.java file (send_missing_data_packet method) :: "+e);
                        }
                        return false;

                }
                next += send_from_rover.size_of_packet;  
            }
            
            //Last packet is missing ask rover to resend
            if(is_end == 0){
                System.out.println("Last Packet did not received requesting it again !!");
                
                byte[] total_data_to_send = send_from_rover.return_header(0,next,0, 1 ,0,"10.0.1.0",src_ip);
                try {

                    DatagramSocket mySocket = new DatagramSocket();
                    InetAddress grp = null;
                    grp = InetAddress.getByName(start_nasa.multicast_ip);
                    Integer port = start_nasa.port;
                    DatagramPacket packet = new DatagramPacket(total_data_to_send , total_data_to_send.length, grp, port);
                    mySocket.send(packet);
                    mySocket.close();
                    f = 0;
                    return false;
                
                } catch (Exception e) {
                    System.out.println("Exception Occured in nasa_receiver.java file (send_missing_data_packet method) :: "+e);
                }
            }

            int len = 0;
            if (f == 1){
                for (Map.Entry<Integer , byte[]> each_entry : main_table.entrySet()) len += each_entry.getValue().length;
            }

            byte[] final_arr = new byte[len];
            int index = 0;

            /**
             * All packets received successfully 
             * From image and write it to disk
             */
            if (f == 1){

                for (Map.Entry<Integer , byte[]> each_entry : main_table.entrySet()) {
                    for(int k = 0 ; k < each_entry.getValue().length ; k++) final_arr[index++] = each_entry.getValue()[k];
                }

                System.out.println("All image packets received properly from "+scip);
                recreate_image(final_arr);
                return true;

            }
            return false;

        }

        //This method create image from byte array
        public static void recreate_image(byte[] arr){

            FileOutputStream fos;
            try{
                fos = new FileOutputStream("output_"+scip+".jpg");
                
                fos.write(arr);

                is_image_formed = true;
                start_rover.is_image_formed = true;
                fos.close();
                System.out.println("Image Created Successfully from  "+scip+" ; Path ==>  <output_source_rover_ip>.jpg in current folder ");
                
            } catch (Exception e){
                System.out.println(" Exception Occured in writing image to disk !!!" + e);               
            }
            

        }
    }
}


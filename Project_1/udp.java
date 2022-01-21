public class udp {
    
    public static void udp_data(byte arr[] , int start_index){

        int src_port  = ((arr[start_index] & 0xff) << 8) | arr[start_index+1] & 0xff;
        System.out.println("UDP: \t ----- UDP Header -----");
        System.out.println("UDP:");
        System.out.println("UDP: \t Source Port = "+src_port);

        int dest_port  = ((arr[start_index+2] & 0xff) << 8) | arr[start_index+3] & 0xff;
        System.out.println("UDP: \t Destination Port = "+dest_port);

        int length  = ((arr[start_index+4] & 0xff) << 8) | arr[start_index+5] & 0xff;
        System.out.println("UDP: \t Length = "+length);

        System.out.println("UDP: \t Checksum = 0x"+pktanalyzer.to_hex(arr[start_index+6])+pktanalyzer.to_hex(arr[start_index+7]));
        System.out.println("UDP:");
        System.out.println("UDP: \t Data: (first 64 bytes)");

        int iteration = 0;
        String d = "";

        int index = start_index+8;
        
        while(index <= Math.min(arr.length - 1 ,  64 + start_index+8)){
            iteration ++;
            d += pktanalyzer.to_hex(arr[index]);
            index ++;
            if(index == arr.length)  {
                System.out.println("UDP: \t "+d); 
                break;
            }
            d += pktanalyzer.to_hex(arr[index]);
            d += " ";
            if (iteration == 8){
                System.out.println("UDP: \t "+d);
                d = "";
                iteration = 0;
            }
            index++;
        }
        

    }

}

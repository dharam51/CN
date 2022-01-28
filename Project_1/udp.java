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
        String ch = "";

        int index = start_index+8;
        
        while(index <= Math.min(arr.length - 1 ,  64 + start_index+8)){
            iteration ++;
            d += pktanalyzer.to_hex(arr[index]);
            int xx = arr[index] & 0xff;
            if (xx >= 33 && xx <= 126) ch += (char) xx;
            else ch += ".";
            index ++;
            if(index == arr.length)  {
                System.out.println("UDP: \t "+d+"\t \t '"+ch+"'"); 
                break;
            }
            d += pktanalyzer.to_hex(arr[index]);
            xx = arr[index] & 0xff;
            if (xx >= 33 && xx <= 126) ch += (char) xx;
            else ch += ".";
            d += " ";
            if (iteration == 8){
                System.out.println("UDP: \t "+d+"\t \t '"+ch+"'");
                d = "";
                ch = "";
                iteration = 0;
            }
            index++;
        }
        

    }

}

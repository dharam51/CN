public class tcp {

    public static void tcp_data(byte[] arr , int start_index){

        System.out.println("TCP: \t ----- TCP Header -----");
        System.out.println("TCP:");

        int src_port = Integer.parseInt(pktanalyzer.to_hex(arr[start_index])+pktanalyzer.to_hex(arr[start_index+1]), 16);
        System.out.println("TCP: \t Source Port = "+src_port);

        int dest_port = Integer.parseInt(pktanalyzer.to_hex(arr[start_index+2])+pktanalyzer.to_hex(arr[start_index+3]), 16);
        System.out.println("TCP: \t Destination Port = "+dest_port);

        
        Long seq_number = Long.parseLong(pktanalyzer.to_hex(arr[start_index+4])+pktanalyzer.to_hex(arr[start_index+5] ) +pktanalyzer.to_hex(arr[start_index+6]) +pktanalyzer.to_hex(arr[start_index+7] ), 16);
        System.out.println("TCP: \t Sequence Number = "+seq_number);

        Long ack = Long.parseLong(pktanalyzer.to_hex(arr[start_index+8])+pktanalyzer.to_hex(arr[start_index+9] ) +pktanalyzer.to_hex(arr[start_index+10]) +pktanalyzer.to_hex(arr[start_index+11] ), 16);
        System.out.println("TCP: \t Acknowledgement Number = "+ack);

       
        int data_offset = pktanalyzer.get_extracted((arr[start_index+12] & 0xff),4,5);
        System.out.println("TCP: \t Data Offset = "+data_offset);

        String flags = pktanalyzer.to_hex((byte)(pktanalyzer.get_extracted((arr[start_index+12] & 0xff),4,1))) + pktanalyzer.to_hex(arr[start_index+13]);
        System.out.println("TCP: \t Flags = 0x"+flags);

        int flag = arr[start_index+13] & 0xff;
        if ((flag & (1 << (6 - 1) )) > 0) System.out.println("TCP: \t\t ..1. .... = Urgent Pointer ");
        else System.out.println("TCP: \t\t ..0. .... = No Urgent Pointer");

        if ((flag & (1 << (5 - 1) )) > 0) System.out.println("TCP: \t\t ...1 .... = Acknowledgment");
        else System.out.println("TCP: \t\t ...0 .... = No Acknowledgment");

        if ((flag & (1 << (4 - 1) )) > 0) System.out.println("TCP: \t\t .... 1... = Push");
        else System.out.println("TCP: \t\t .... 0... = No Push");

        if ((flag & (1 << (3 - 1) )) > 0) System.out.println("TCP: \t\t .... .1.. = reset");
        else System.out.println("TCP: \t\t .... .0.. = No reset");

        if ((flag & (1 << (2 - 1) )) > 0) System.out.println("TCP: \t\t .... ..1. = syn");
        else System.out.println("TCP: \t\t .... ..0. = No syn");

        if ((flag & (1 << (1 - 1) )) > 0) System.out.println("TCP: \t\t .... ...1 = fin");
        else System.out.println("TCP: \t\t .... ...0 = No fin");

        System.out.println("TCP: \t Window = "+Integer.parseInt(pktanalyzer.to_hex(arr[start_index+14]) + pktanalyzer.to_hex(arr[start_index+15]) , 16));

        String checksum = pktanalyzer.to_hex(arr[start_index+16]) + pktanalyzer.to_hex(arr[start_index+17]);
        System.out.println("TCP: \t Checksum = 0x"+checksum);

        System.out.println("TCP: \t Urgent Pointer = "+Integer.parseInt(pktanalyzer.to_hex(arr[start_index+18]) + pktanalyzer.to_hex(arr[start_index+19]) , 16));


        if(data_offset > 5) System.out.println("TCP: \t Options = Yes");
        else System.out.println("TCP: \t No Options");


        System.out.println("TCP:");
        System.out.println("TCP: \t Data: (first 64 bytes)");

        
        int iteration = 0;
        String d = "";

        int index = start_index+data_offset*4;

        while(index <= Math.min(arr.length - 1 ,  64+(start_index+data_offset*4))){
            iteration ++;
            d += pktanalyzer.to_hex(arr[index]);
            index ++;
            d += pktanalyzer.to_hex(arr[index]);
            d += " ";
            if (iteration == 8){
                System.out.println("TCP: \t "+d);
                d = "";
                iteration = 0;
            }
            index++;
        }

        

        //System.out.println(pktanalyzer.to_hex(arr[start_index+data_offset*4]));

        //11001101 01010110
        //11001101 1010110

    }
    
}

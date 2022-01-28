public class IP {
    
    public static int ip_data(byte[] arr){
        System.out.println("IP: \t ----- IP HEADER -----");
        System.out.println("IP:");

        int version_header_length  = arr[14] & 0xff;
        int header_length = (pktanalyzer.get_extracted(version_header_length,4,1)) * 4;
        int version = (pktanalyzer.get_extracted(version_header_length,4,5)) ;
        System.out.println("IP: \t Version = "+version);
        System.out.println("IP: \t Header Length = "+header_length + " bytes");
        
       
        System.out.println("IP: \t DCN = 0x"+(pktanalyzer.to_hex((byte)pktanalyzer.get_extracted(arr[15] & 0xff,6,3))));


        int ecn = (pktanalyzer.get_extracted(arr[15] & 0xff,2,1));
        System.out.println("IP: \t ECN = 0x"+pktanalyzer.to_hex((byte)ecn));
        
        if ((((arr[15] & 0xff) & (1 << (1 - 1) )) > 0) && (((arr[15] & 0xff) & (1 << (2 - 1) )) > 0)) System.out.println("IP: \t \t .... ..11 = Congestion Encountered");
        else if ((((arr[15] & 0xff) & (1 << (1 - 1) )) > 0) && !(((arr[15] & 0xff) & (1 << (2 - 1) )) > 0)) System.out.println("IP: \t \t .... ..10 = ECN Capable Transport , ECT(0)");
        else if (!(((arr[15] & 0xff) & (1 << (1 - 1) )) > 0) && (((arr[15] & 0xff) & (1 << (2 - 1) )) > 0)) System.out.println("IP: \t \t .... ..01 = ECN Capable Transport , ECT(1)");
        else System.out.println("IP: \t \t .... ..00 = Non ECN-Capable Transport, Non-ECT");



        int total_length = Integer.parseInt(pktanalyzer.to_hex(arr[16] )+pktanalyzer.to_hex(arr[17] ), 16);
        System.out.println("IP: \t Total length = "+total_length+" bytes");

        
        int identification = Integer.parseInt(pktanalyzer.to_hex(arr[18])+pktanalyzer.to_hex(arr[19] ), 16);
        System.out.println("IP: \t Identification = "+identification);
        
        
        int flags = (pktanalyzer.get_extracted(arr[20] & 0xff,3,6));
        System.out.println("IP: \t Flags = 0x"+pktanalyzer.to_hex((byte)flags));

        if (((arr[20] & 0xff) & (1 << (7 - 1) )) > 0) System.out.println("IP: \t\t .1. = do not fragment ");
        else System.out.println("IP: \t\t .0. = Fragment");

        if (((arr[20] & 0xff) & (1 << (6 - 1) )) > 0) System.out.println("IP: \t\t .1. = More Fragment ");
        else System.out.println("IP: \t\t ..0 = last fragment");

        byte frag_off = (byte)(pktanalyzer.get_extracted(arr[20] & 0xff,5,1));
        int fragment_offset = Integer.parseInt(pktanalyzer.to_hex(frag_off) +pktanalyzer.to_hex(arr[21]), 16);
        System.out.println("IP: \t Fragment Offset = "+fragment_offset+" bytes");

        System.out.println("IP: \t Time to live = "+(arr[22] & 0xff)+ " seconds/hops" );

        int protocol = arr[23] & 0xff;

        if(protocol == 6) System.out.println("IP: \t Protocol = "+(arr[23] & 0xff)+ " (TCP)" );
        else if(protocol == 1) System.out.println("IP: \t Protocol = "+(arr[23] & 0xff)+ " (ICMP)" );
        else if(protocol == 17) System.out.println("IP: \t Protocol = "+(arr[23] & 0xff)+ " (UDP)" );

        String header_checksum = "";
        for(int i = 24 ; i<= 25 ;i++){
            header_checksum += pktanalyzer.to_hex(arr[i]);
            
        }
        System.out.println("IP: \t Header Checksum = 0x"+header_checksum);

        String src_address = "";
        for(int i = 26 ; i<= 29 ;i++){
            src_address += (arr[i] & 0xff) +".";
        }
        System.out.println("IP: \t Source Address = "+src_address.substring(0,src_address.length()-1));

        String dest_address = "";
        for(int i = 30 ; i<= 33 ;i++){
            dest_address += (arr[i] & 0xff) +".";
        }
        System.out.println("IP: \t Destination Address = "+dest_address.substring(0,dest_address.length()-1));

        if(header_length > 20) System.out.println("IP: \t Options Present = Yes");
        else System.out.println("IP: \t No options");

        System.out.println("IP");

        

        return header_length;

        
        


    }

}

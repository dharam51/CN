/**
 * To get partial hex just take subset of bits in integer form and convert normal method to hex using Hex array
 */

public class IP {
    
    public static int ip_data(byte[] arr){
        System.out.println("IP: \t ----- IP HEADER -----");
        System.out.println("IP:");

        int version_header_length  = arr[14] & 0xff;
        int header_length = (pktanalyzer.get_extracted(version_header_length,4,1)) * 4;
        int version = (pktanalyzer.get_extracted(version_header_length,4,5)) ;
        System.out.println("IP: \t Version = "+version);
        System.out.println("IP: \t Header Length = "+header_length + " bytes");
        
       
        System.out.println("IP: \t Type of Service = 0x"+pktanalyzer.to_hex(arr[15]));

        int priority_decimal = arr[15] & 0xff;
        int precedence = (pktanalyzer.get_extracted(priority_decimal,3,6));
        
        String precendence_binary = "";
        if((precedence & (1 << (8 - 1) )) > 0) precendence_binary += 1;
        else precendence_binary += 0;

        if((precedence & (1 << (7 - 1) )) > 0) precendence_binary += 1;
        else precendence_binary += 0;

        if((precedence & (1 << (6 - 1) )) > 0) precendence_binary += 1;
        else precendence_binary += 0;

        System.out.println("IP: \t\t "+precendence_binary+". .... = "+precedence+" (precedence)");

        if ((priority_decimal & (1 << (5 - 1) )) > 0) System.out.println("IP: \t\t ...1 .... = Low Delay");
        else System.out.println("IP: \t\t ...0 .... = Normal Delay");

        if ((priority_decimal & (1 << (4 - 1) )) > 0) System.out.println("IP: \t\t .... 1... = High throughput");
        else System.out.println("IP: \t\t .... 0... = Normal throughput");

        if ((priority_decimal & (1 << (3 - 1) )) > 0) System.out.println("IP: \t\t .... .1.. = High Reliability");
        else System.out.println("IP: \t\t .... .0.. = Normal reliability");

        if ((priority_decimal & (1 << (2 - 1) )) > 0) System.out.println("IP: \t\t .... ..1. = Minimize monetary cost");
        else System.out.println("IP: \t\t .... ..0. = Normal monetary cost");

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
            header_checksum += pktanalyzer.Hex[(0xF0 & arr[i]) >>> 4];
            header_checksum  += pktanalyzer.Hex[(0x0F & arr[i])];
        }
        System.out.println("IP: \t Header Checksum = 0x"+header_checksum);

        String src_address = "";
        for(int i = 26 ; i<= 29 ;i++){
            src_address += (arr[i] & 0xff) +".";
        }
        System.out.println("IP: \t Source Address = "+src_address);

        String dest_address = "";
        for(int i = 30 ; i<= 33 ;i++){
            dest_address += (arr[i] & 0xff) +".";
        }
        System.out.println("IP: \t Destination Address = "+dest_address);

        if(header_length > 20) System.out.println("IP: \t Options Present = Yes");
        else System.out.println("IP: \t No options");

        System.out.println("IP");

        

        return header_length;

        
        


    }

}

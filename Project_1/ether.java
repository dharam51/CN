public class ether {

    public static String ether_data(byte[] arr){
        System.out.println("ETHER: \t ----- ETHER HEADER -----");
        System.out.println("ETHER:");
        System.out.println("ETHER: \t Packet Size = "+arr.length);
        String destination_mac = "";
        for(int i = 0 ; i <= 5 ; i++){
            destination_mac += pktanalyzer.to_hex(arr[i]);
            destination_mac += ":";
        }
        System.out.println("ETHER: \t Destination = "+destination_mac.substring(0,destination_mac.length()-1));

        String src_mac = "";
        for(int i = 6 ; i <= 11 ; i++){
            src_mac += pktanalyzer.to_hex(arr[i]);
            src_mac += ":";
        }
        System.out.println("ETHER: \t Source = "+src_mac.substring(0,src_mac.length()-1));

        String eth_type = "";
        for(int i = 12 ; i <= 13 ; i++){
            eth_type += pktanalyzer.to_hex(arr[i]);
        }
        System.out.println("ETHER: \t Ethertype = "+eth_type+ " (IP)");
        System.out.println("ETHER:");
        
        return eth_type;
        

    }

}

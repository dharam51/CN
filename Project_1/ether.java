public class ether {

    public static void ether_data(byte[] arr){
        System.out.println("ETHER: \t ----- ETHER HEADER -----");
        System.out.println("ETHER:");
        System.out.println("ETHER: \t Packet Size = "+arr.length);
        String destination_mac = "";
        for(int i = 0 ; i <= 5 ; i++){
            destination_mac += pktanalyzer.Hex[(0xF0 & arr[i]) >>> 4];
            destination_mac += pktanalyzer.Hex[(0x0F & arr[i])];
            destination_mac += ":";
        }
        System.out.println("ETHER: \t Destination = "+destination_mac);

        String src_mac = "";
        for(int i = 6 ; i <= 11 ; i++){
            src_mac += pktanalyzer.Hex[(0xF0 & arr[i]) >>> 4];
            src_mac += pktanalyzer.Hex[(0x0F & arr[i])];
            src_mac += ":";
        }
        System.out.println("ETHER: \t Source = "+src_mac);

        String eth_type = "";
        for(int i = 12 ; i <= 13 ; i++){
            eth_type += pktanalyzer.Hex[(0xF0 & arr[i]) >>> 4];
            eth_type += pktanalyzer.Hex[(0x0F & arr[i])];
            
        }
        System.out.println("ETHER: \t Ethertype = "+eth_type+ " (IP)");
        System.out.println("ETHER:");

    }

}

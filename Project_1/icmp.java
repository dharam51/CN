public class icmp {

    public static void icmp_data(byte[] arr , int start_index){

        System.out.println("ICMP: \t ----- ICMP Header -----");
        System.out.println("ICMP:");
        System.out.println("ICMP: \t Type = "+(arr[start_index]&0xff));
        System.out.println("ICMP: \t Type = "+(arr[start_index+1]&0xff));

        System.out.println("ICMP: \t Checksum = 0x"+pktanalyzer.to_hex(arr[start_index+2])+pktanalyzer.to_hex(arr[start_index+3]));
        System.out.println("ICMP:");

    }

}

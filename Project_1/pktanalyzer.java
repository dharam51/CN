/***
 * Authors : @Dharmendra Rasikbhai Nasit (drn1263)
 * Problem : Analyze packets 
 */

import java.util.*;
import java.io.*;

import java.lang.*;
public class pktanalyzer {
    public static final char[] Hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9','a', 'b', 'c', 'd', 'e', 'f' }; 
        public static void main( String[] args ) {
            try {
                if (args.length != 1 ){
                    System.out.println("Invalid Arguments Passed !");
                    System.exit(1);
                }
                String file_path = args[0];
                File file = new File(file_path);
                

                FileInputStream stream = new FileInputStream(file);
                byte[] buff = new byte[(int) file.length()];
                stream.read(buff);
                String eth_type =  ether.ether_data(buff);

                if(eth_type.equals("0800")){
                    int header_length = IP.ip_data(buff);
                    if ((buff[23] & 0xff) == 6) tcp.tcp_data(buff , 14 + header_length );
                    else if ((buff[23] & 0xff) == 17)udp.udp_data(buff,14+header_length);
                    else if ((buff[23] & 0xff) == 1) icmp.icmp_data(buff,14+header_length);
                    else System.out.println("Protocol Not Supported !!");
                }
                
                stream.close();
            }
            catch (Exception ex) {
                System.out.println(ex);
            }
    }

    public static int get_extracted(int number ,int k ,int p) {
        return (((1 << k) - 1) & (number >> (p - 1)));
    }

    public static String to_hex(byte val){
        return Hex[(0xF0 & val) >>> 4] + "" +Hex[(0x0F & val)];
    }

}

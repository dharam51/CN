//https://mkyong.com/java/java-how-to-convert-bytes-to-hex/
//https://stackoverflow.com/questions/16770742/convert-byte-array-to-decimal
//int x = Integer.parseInt("7eec",16);  
//https://www.hudatutorials.com/java/basics/java-arrays/java-byte-array
//https://stackoverflow.com/questions/46383382/java-how-to-extract-certain-portions-of-bits-using-bit-mask
//https://stackoverflow.com/questions/6090561/how-to-use-high-and-low-bytes
//https://stackoverflow.com/questions/8408918/extracting-bits-with-bitwise-operators ==> IMP
//https://www.oreilly.com/library/view/c-cookbook/0596003390/ch01s06.html
import java.util.*;
import java.io.*;
import java.lang.*;
public class pktanalyzer {
    public static final char[] Hex = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9','a', 'b', 'c', 'd', 'e', 'f' };
        public static void main( String[] arguments ) {
            try {
                File file = new File("new_tcp_packet1.bin");
                FileInputStream fis = new FileInputStream(file);
                byte[] buff = new byte[(int) file.length()];
                fis.read(buff);
                ether.ether_data(buff);
                int header_length = IP.ip_data(buff);
                
                //udp.udp_data(buff,14+header_length);
                //icmp.icmp_data(buff,14+header_length);
                tcp.tcp_data(buff , 14 + header_length );
                
                  
                
                /**
                for(int i = 0; i < buff.length ; i++){
                    
                    char[] result = new char[2];
                    result[0] = Hex[(0xF0 & buff[i]) >>> 4];
                    result[1] = Hex[(0x0F & buff[i])];
                    
                    int[] result1 = new int[1];
                    result1[0] = buff[i] & 0xff;

                    System.out.println(result);
                    System.out.println(result1[0]);
                    System.out.println("###################");
                    
                }
                **/
                fis.close();
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
    }

    public static int get_extracted(int number ,int k ,int p) {
        return (((1 << k) - 1) & (number >> (p - 1)));
    }

    public static String to_hex(byte val){
        return Hex[(0xF0 & val) >>> 4] + "" +Hex[(0x0F & val)];
    }

    

}

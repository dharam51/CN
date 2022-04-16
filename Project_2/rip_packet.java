/**
 * Author  : @Dharmendra Nasit (drn1263)
 * This file is responsible to genereate rip packet
 * 
 */

import java.util.ArrayList;

public class rip_packet {
    
    public static int rip_version = 2;
    public static int unused = 0;
    public static int cmd_request = 1;
    public static int cmd_response = 2;
    public static int unreachable = 16;

    int command ; 
    ArrayList<rover_routing_table> rrt = new ArrayList<>();
    String sender;

    public rip_packet(int command , ArrayList<rover_routing_table> rrt, String sender){

        this.command = command;
        this.rrt = rrt;
        this.sender = sender;

    }

    public rip_packet(){
        this.command = rip_packet.cmd_request;

    }

    public ArrayList<rover_routing_table> get_rrt(){
        return this.rrt;
    }

    public static String get_sender_ip(String sender){
        return "10.0."+sender+".0";
    }

    public byte[] form_rip_packet(){
        byte[] arr = new byte[(rrt.size()*20) + 4];
        int i = 0;
        if(command == (rip_packet.cmd_request)){
            byte b = (byte)rip_packet.cmd_request;
            arr[i++] = b;
        }
        else{
            byte b = (byte)rip_packet.cmd_response;
            arr[i++] = b;
        }
        arr[i++] = (byte)rip_packet.rip_version;
        arr[i++] = (byte)Integer.parseInt(sender);
        arr[i++] = (byte)rip_packet.unused;

        if(command == (rip_packet.cmd_response))  {

            for(int j = 0 ; j < rrt.size() ; j ++){
                rover_routing_table mycurrentrrt = rrt.get(j);
                int address_family_identified = mycurrentrrt.address_family_identified;
                byte low = (byte) (address_family_identified >> 8);
                byte high = (byte) (address_family_identified);
                arr[i++] = low;
                arr[i++] = high;

                int route_tag = mycurrentrrt.route_tag;
                low = (byte) (route_tag >> 8);
                high = (byte) route_tag;
                arr[i++] = low;
                arr[i++] = high;

                String destination_ip = mycurrentrrt.get_destination_ip();
                String[] s = destination_ip.split("\\.");
                for(int x = 0; x < 4; x++)  {
                    arr[i++] = (byte) (Integer.parseInt(s[x],10));
                }

                String subnet_mask = mycurrentrrt.subnet_mask;
                s = subnet_mask.split("\\.");
                for(int x = 0; x < 4; x++)  {
                    arr[i++] =(byte) (Integer.parseInt(s[x],10));
                }

                String next_hop = mycurrentrrt.get_next_hop();
                s = next_hop.split("\\.");
                for(int x = 0; x < 4; x++)  {
                    arr[i++] = (byte) (Integer.parseInt(s[x],10));
                }

                int metrics = mycurrentrrt.get_metrics();
                byte[] bytearr = new byte[4];
                bytearr[0] = (byte) (metrics >> 24);
                bytearr[1] = (byte) (metrics >> 16);
                bytearr[2] = (byte) (metrics >> 8);
                bytearr[3] = (byte) (metrics);
                for (byte b : bytearr) {
                    arr[i++] = b;
                }

            }

        }
        return arr;

    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameserver;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pantsu
 */
public class Matchmaker extends Thread{
  
   DatagramSocket dts = null;
   DatagramPacket packet = null;
   byte[] buf;
   int port=1600;
   public Matchmaker(int port) {
       try {
           this.port=port;
           dts = new DatagramSocket(port);
          
       } catch (SocketException ex) {
           Logger.getLogger(Matchmaker.class.getName()).log(Level.SEVERE, null, ex);
       }
   }
   
   @Override
   public void run() {
       List<String[]> list = new ArrayList<>();
       while(true) {
           buf = new byte[256];
           packet = new DatagramPacket(buf, buf.length);
           try {
               dts.receive(packet);
               
               InetAddress address = packet.getAddress();
               int port = packet.getPort();
               
               boolean good = false;
               
               for(int i = 0; i < list.size(); i++) {
                   String[] temp = list.get(i);
                   String address2 = temp[0];
                   String port2 = temp[1];
                   
                   DatagramSocket gameSocket1 = new DatagramSocket();
                   DatagramSocket gameSocket2 = new DatagramSocket();
                   
                   list.remove(i);
                   String prt = String.valueOf(gameSocket1.getLocalPort());
                   buf = ("player1" + " " + prt).getBytes();
                   packet = new DatagramPacket(buf, buf.length, address, port);
                   dts.send(packet);
                   
                   prt = String.valueOf(gameSocket2.getLocalPort());
                   buf = ("player2" + " " + prt).getBytes();
                   packet = new DatagramPacket(buf, buf.length, InetAddress.getByName(address2), Integer.parseInt(port2));
                   dts.send(packet);
                   
                   good = true;
                   Thread t = new GameState(address, port, InetAddress.getByName(address2), Integer.parseInt(port2), gameSocket1, gameSocket2);
                   t.start();
               }
               
               
               if(!good) {
                   String IP = address.toString().substring(1);
                   String pt = String.valueOf(port);
                   list.add(new String[]{IP, pt});
               }
               
           } catch (IOException ex) {
               Logger.getLogger(Matchmaker.class.getName()).log(Level.SEVERE, null, ex);
           }
       }
//       try {
//           match();
//       } catch (IOException ex) {
//           Logger.getLogger(Matchmaker.class.getName()).log(Level.SEVERE, null, ex);
//       }
   }
   
//    public void match() throws IOException{
//        while (true) {
//            ResultSet rs;
//            List<String[]> list = new ArrayList<>();
//            try {
//                PreparedStatement stmt = conn.prepareStatement(
//                        " SELECT * FROM online");
//                rs = stmt.executeQuery();
//                while (rs.next()) {
//                    String IP = rs.getString("IP");
//                    String Port = rs.getString("port");
//                    String busy = rs.getString("busy");
//                    list.add(new String[]{IP, Port, busy});
//                }
//            } catch (SQLException ex) {
//                Logger.getLogger(GameServerThread.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            for(int i = 0; i < list.size(); i++) {
//                String[] first = list.get(i);
//                for(int j = i + 1; j < list.size(); j++) {
//                    String[] second = list.get(j);
//                    if(first[2].equals("false") && second[2].equals("false")) {
//                        InetAddress IP1 = null;
//                        InetAddress IP2 = null;
//                        try {
//                            IP1 = InetAddress.getByName(first[0].substring(1));
//                            IP2 = InetAddress.getByName(second[0].substring(1));
//                        } catch (UnknownHostException ex) {
//                            Logger.getLogger(Matchmaker.class.getName()).log(Level.SEVERE, null, ex);
//                        }
//                            buf = new byte[256];
//                            String s = "DankSouls_GetShrektEdition start player1";
//                            buf = s.getBytes();
//                            
//                            packet = new DatagramPacket(buf, buf.length, IP1, Integer.parseInt(first[1]));
//                            dts.send(packet);
//                            
//                            s = "DankSouls_GetShrektEdition start player2";
//                            buf = s.getBytes();
//                            packet = new DatagramPacket(buf, buf.length, IP2, Integer.parseInt(second[1]));
//                            dts.send(packet);
//                            
//                            Thread t = new GameState(IP1, Integer.parseInt(first[1]), IP2, Integer.parseInt(second[1]));
//                            t.start();
//                    }
//                }
//            }
//        }
//    }
}

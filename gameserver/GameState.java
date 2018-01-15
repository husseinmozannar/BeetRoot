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
import java.util.logging.Level;
import java.util.logging.Logger;
 
/**
 *
 * @author pantsu
 */
public class GameState extends Thread{
    private InetAddress player1;
    private int player1Port;
    private InetAddress player2;
    private int player2Port;
    DatagramSocket socket1 = null;
    DatagramSocket socket2 = null;
    DatagramPacket packet1 = null;
    DatagramPacket packet2 = null;
    byte[] buf1;
    byte[] buf2;
 
    public GameState(InetAddress player1, int player1Port, InetAddress player2, int player2Port, DatagramSocket socket1, DatagramSocket socket2) {
        this.player1 = player1;
        this.player1Port = player1Port;
        this.player2 = player2;
        this.player2Port = player2Port;
        this.socket1 = socket1;
        this.socket2 = socket2;
    }
 
    @Override
    public void run() {
        while(true) {
            boolean skip = false;
            try {
                buf1 = new byte[256];
                buf2 = new byte[256];
                packet1 = new DatagramPacket(buf1, buf1.length);
                socket1.receive(packet1);
                packet2 = new DatagramPacket(buf2, buf2.length);
                socket2.receive(packet2);
                buf1 = packet1.getData();
                buf2 = packet2.getData();
                String s1 = new String(buf1);
                String s2 = new String(buf2);
                String[] arr1 = s1.split(" ");
                String[] arr2 = s2.split(" ");
 
                double HP1 = Double.parseDouble(arr1[3]);
                double HP2 = Double.parseDouble(arr2[3]);
 
                if(HP1 <= 0.0) {
                                skip = true;
                                String temp = "LOST asdf";
                                buf1 = temp.getBytes();
                                packet1 = new DatagramPacket(buf1, buf1.length, player1, player1Port);
                                socket1.send(packet1);
 
                                temp = "WIN asdf";
                                buf2 = temp.getBytes();
                                packet2 = new DatagramPacket(buf2, buf2.length, player2, player2Port);
                                socket2.send(packet2);
                            }
                            else{
                                packet2 = new DatagramPacket(buf1, buf1.length, player2, player2Port);
                                socket2.send(packet2);
                            }
 
                if(HP2 <= 0.0) {
                                skip = true;
                                String temp = "LOST asdf";
                                buf2 = temp.getBytes();
                                packet2 = new DatagramPacket(buf2, buf2.length, player2, player2Port);
                                socket2.send(packet2);
 
                                temp = "WIN asdf";
                                buf1 = temp.getBytes();
                                packet1 = new DatagramPacket(buf1, buf1.length, player1, player1Port);
                                socket1.send(packet1);
                            }
                else if (!skip) {
                                packet1 = new DatagramPacket(buf2, buf2.length, player1, player1Port);
                                socket1.send(packet1);
                            }
 
//                if(arr[0].equals("DankSouls_GetShrektEdition")) {
//                    double HP = Double.parseDouble(arr[3]);
//                    if(arr[6].equals("player1")) {
//                            if(HP <= 0.0) {
//                                String temp = "LOST asdf";
//                                buf = temp.getBytes();
//                                packet = new DatagramPacket(buf, buf.length, player1, player1Port);
//                                socket.send(packet);
//                                
//                                temp = "WIN asdf";
//                                buf = temp.getBytes();
//                                packet = new DatagramPacket(buf, buf.length, player2, player2Port);
//                                socket.send(packet);
//                            }
//                            else{
//                                packet = new DatagramPacket(buf, buf.length, player2, player2Port);
//                                socket.send(packet);
//                            }
//                    }
//                    if(arr[6].equals("player2")) {
//                            if(HP <= 0.0) {
//                                String temp = "LOST asdf";
//                                buf = temp.getBytes();
//                                packet = new DatagramPacket(buf, buf.length, player2, player2Port);
//                                socket.send(packet);
//                                
//                                temp = "WIN asdf";
//                                buf = temp.getBytes();
//                                packet = new DatagramPacket(buf, buf.length, player1, player1Port);
//                                socket.send(packet);
//                            }
//                            else {
//                                packet = new DatagramPacket(buf, buf.length, player1, player1Port);
//                                socket.send(packet);
//                            }
//                    }
//                }
            } catch (IOException ex) {
                Logger.getLogger(GameState.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
 
//            enemy.x = Integer.parseInt(arr[1]);
//            enemy.y = Integer.parseInt(arr[2]);
//            enemy.HP.value = Double.parseDouble(arr[3]);
//            enemy.HP.updateValue();
//            enemy.SP.value = Double.parseDouble(arr[4]);
//            enemy.SP.updateValue();
//            enemy.setAnimation(arr[5]);
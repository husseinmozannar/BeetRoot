/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package danksouls_getshrektedition;

import java.awt.Color;
import java.awt.EventQueue;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author pantsu
 */
public class start extends JFrame {

    public static DankSouls_GetShrektEdition gaem = null;
//        public static Socket socket;
//        public static DataInputStream input;
//        public static DataOutputStream output;
    public static DatagramSocket dts = null;
    public static DatagramPacket packet = null;
    public static byte[] buf;
    static boolean ready = false;
    public int port=1600;
    public start(int port) {

        try {
//            socket = new Socket("localhost", 1600);
//            input = new DataInputStream(socket.getInputStream());
//            output = new DataOutputStream(socket.getOutputStream());
            dts = new DatagramSocket();

        } catch (IOException ex) {
            Logger.getLogger(start.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.port=port;
        setSize(500, 300);
        setResizable(false);

        setTitle("DaNk SOuLs Start");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

    }

    public void Run(start e) {

        try {
            String s = "connect";
            e.setVisible(true);
            JPanel panel = new JPanel();
            JLabel label = new JLabel("Looking for game");
            label.setForeground(Color.WHITE);
            panel.add(label);
            panel.setBackground(Color.BLACK);
            e.add(panel);

            buf = new byte[256];
            buf = s.getBytes();
            InetAddress address = InetAddress.getByName("127.0.0.1");
            packet = new DatagramPacket(buf, buf.length, address, port);
            dts.send(packet);

            buf = new byte[256];
            packet = new DatagramPacket(buf, buf.length);

            new Thread() {
                @Override
                public void run() {
                    try {
                        dts.receive(packet);
                        String received = new String(packet.getData(), 0, packet.getLength());
                        String arr[] = received.split(" ");

                        gaem = new DankSouls_GetShrektEdition(arr[0], Integer.parseInt(arr[1]), dts);
                        //gaem = new DankSouls_GetShrektEdition(arr[0], port, dts);
                        e.setVisible(false);
                        gaem.setVisible(true);
                    } catch (IOException ex) {
                        Logger.getLogger(start.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

//                    s = input.readUTF();
//                    if(s.equals("ok")) {
//                        panel.remove(label);
//                        label = new JLabel("Searching for game");
//                        panel.add(label);
//                        while(true) {
//                            buf = new byte[256];
//                            packet = new ^DatagramPacket(buf, buf.length);
//                            dts.receive(packet);
//                            buf = packet.getData();
//                            String payload = new String(buf);
//                            String[] arr = payload.split(" ");
//                            if(arr[0].equals("DankSouls_GetShrektEdition") && arr[1].equals("start")) {
//                                DankSouls_GetShrektEdition game = new DankSouls_GetShrektEdition(arr[2], packet.getPort());
//                                e.setVisible(false);
//                                game.setVisible(true);
//                            }
//                        }
            }.start();
        } catch (UnknownHostException ex) {
            Logger.getLogger(start.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(start.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pantsu
 */
public class GameServerThread extends Thread {
   
    private Socket socket;
    private DataOutputStream output;
    private DataInputStream input;

    public GameServerThread(Socket socket) {
        this.socket = socket;
        try {
            output = new DataOutputStream(this.socket.getOutputStream());
            input = new DataInputStream(this.socket.getInputStream());
            
            
        } catch (IOException ex) {
            Logger.getLogger(GameServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void run() {
        
    }
    

}

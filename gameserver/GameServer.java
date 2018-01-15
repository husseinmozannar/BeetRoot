/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pantsu
 */
public class GameServer {

    private ServerSocket sSocket;

    public GameServer(int port) {
        try {
            sSocket = new ServerSocket(1599);
            Thread t = new Matchmaker(port);
            t.start();
        } catch (IOException ex) {
            Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void listen() {
        while (true) {
            try {
                Socket socket = sSocket.accept();
            } catch (IOException ex) {
                Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

   
}

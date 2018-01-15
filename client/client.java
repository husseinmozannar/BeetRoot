/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

/**
 *
 * @author pantsu
 */
public class client {
    Socket socket;
    ObjectInputStream input; 
    ObjectOutputStream output;
    public boolean isConnected=false;
    String servername;
    int port;
    //implement a disconect method to close sockets
    // implement a start method instead of constructor: so that when login is pressed we start connection and proceed 
    public boolean start() { 
		// try to connect to the server
		
        try {
            this.socket = new Socket(servername, port);
            input = new  ObjectInputStream(socket.getInputStream());
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush(); // try
            isConnected=true;
            
        } catch (IOException ex) {
            Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
            isConnected=false;
            return false;
        }
                return true;
                
                
                
	} 
    
    public client(String servername, int port) {
       this.servername=servername;
       this.port=port;
    }
 
    public boolean terminate ()
    {
        boolean ok=false;
        try {
            request quit =new request(); quit.command="logout";
            output.writeObject(quit);
            ok=true;
            isConnected=false;
        } catch (IOException ex) {
            Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
        }
         if (input!=null) try {
            input.close();
        } catch (IOException ex) {
            Logger.getLogger(chatclient.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(output!=null) try {
            output.close();
        } catch (IOException ex) {
            Logger.getLogger(chatclient.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (socket!=null) try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(chatclient.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ok;
    }
    
    public  request sendRequest (request s) { //used for any command: is given command_name + parameters seperated by whitespace
        request ok = new request();
        ok.response="false";
        try {
            if (isConnected==true)
            {
            output.writeObject(s); //send to server
            
            try {
                ok = (request) input.readObject();
                System.out.println("from client " + ok.response);//read back
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
        } catch (IOException ex) {
            Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ok;
    }

//    public String getFriends (String s)
//    {
//        String good="";
//        try {
//           output.writeUTF(s);
//           good=input.readUTF();
//     
//        } catch (IOException ex) {
//            Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return good;
//    }

    
}

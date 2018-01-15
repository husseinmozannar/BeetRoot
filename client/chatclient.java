/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import gui.ChatArea;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

/**
 *
 * @author Hussein
 */
public class chatclient {

    //for chat client we can use objectinputstream for messages to make it structured : no separation
    Socket socket;
    ObjectInputStream input;
    ObjectOutputStream output;
    public ChatArea chatarea; // make it private and make method setTextArea
    String servername;
    String user;
    public ArrayList<String> recipients = new ArrayList<String>();
    int port;
    private String type;
    private boolean isconnected = false;

    //implement a disconect method to close sockets
    // implement a start method instead of constructor: so that when login is pressed we start connection and proceed 
    public chatclient(String servername, int port, ChatArea txt, String user, ArrayList<String> r, String type) {
        this.servername = servername;
        this.port = port;
        chatarea = txt;
        this.type = type;
        this.user = user;
        recipients = new ArrayList<String>(r);
    }

    public boolean start() {
        // try to connect to the server
        boolean ok = false;
        try {
            this.socket = new Socket(servername, port);
            input = new ObjectInputStream(socket.getInputStream());
            output = new ObjectOutputStream(socket.getOutputStream());
            output.flush(); // flush output stream
            isconnected = true;
            ok = true;
            request r = null;

            
             if (type.equals("connectTTT")) {
                
                r = new request("connectTTT", user, ""); // tell server to switch to the chatThread
                
                sendRequest(r);
                r = (request) input.readObject();
                
                recipients = new ArrayList<String>(r.friends);
                chatarea.recipients = new ArrayList<String>(r.friends);
             }
             else if (type.equals("connectDANK"))
             {
                 r = new request("connectDANK", user, ""); // tell server to switch to the chatThread
                
                sendRequest(r);
                r = (request) input.readObject();
                
                recipients = new ArrayList<String>(r.friends);
                chatarea.recipients = new ArrayList<String>(r.friends);
                 
                 
                 
             }
            r = new request("chat", user, "");
            sendRequest(r);
            
            input = new ObjectInputStream(socket.getInputStream()); //need to reset the streams since we will now send different objects
            output = new ObjectOutputStream(socket.getOutputStream());
            new ListenFromServer().start(); //start listening
        } catch (IOException ex) {
            Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
            isconnected = false;

        } finally {
            return ok;
        }

    }

    public boolean terminate() {
        boolean ok = false;
        try {
            Message quit = new Message();
            isconnected = false;
            quit.command = "logout";
            output.writeObject(quit);
            isconnected = false;
            ok = true;
        } catch (IOException ex) {
            Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (input != null) {
            try {
                input.close();
            } catch (IOException ex) {
                Logger.getLogger(chatclient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (output != null) {
            try {
                output.close();
            } catch (IOException ex) {
                Logger.getLogger(chatclient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(chatclient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ok;
    }

    public void sendMessage(Message msg) {

        try {

            output.writeObject(msg);
            output.flush();
        } catch (IOException ex) {
            Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public request sendRequest(request s) { //used for any command: is given command_name + parameters seperated by whitespace
        request ok = new request();
        ok.response = "false";
        try {
            output.writeObject(s);
            output.flush();//send to server
            // System.out.println("from client " + ok.response);//read back
        } catch (IOException ex) {
            Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ok;
    }

    class ListenFromServer extends Thread {

        public void run() {
            while (isconnected) {

                Message msg;

                try {

                    msg = (Message) input.readObject();
                    if ((msg.response.equals("connectDANK") ||msg.response.equals("connectTTT") )&&msg.response.equals(type) ) 
                    
                    {
                        recipients = new ArrayList<String>(msg.recipients);
                        chatarea.recipients = new ArrayList<String>(msg.recipients);
                        chatarea.updateTable();
                        //update table also
                        chatarea.textarea.append(msg.date + " " + msg.sender + ":" + "\n" + msg.content + "\n");

                    }

                    if (msg.response.equals("logout")) {
                        break;
                    }
                    ArrayList<String> list1 = new ArrayList<String>(recipients);
                    ArrayList<String> list2 = new ArrayList<String>(msg.recipients);
                    Collections.sort(list1);
                    Collections.sort(list2);
                    if (list1.equals(list2)) {
                        if (msg.response.equals("messages")) {
                            for (int i = 0; i < msg.messages.size(); i++) {
                                chatarea.textarea.append(msg.messages.get(i)); // setting old messages on screen

                            }

                        }
                        else if (msg.response.equals("chat")) {
                            chatarea.textarea.append(msg.date + " " + msg.sender + ":" + "\n" + msg.content + "\n");
                        }
                    }
                } catch (IOException ex) {
                    Logger.getLogger(chatclient.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(chatclient.class.getName()).log(Level.SEVERE, null, ex);
                }

//					}
//					
                // can't happen with a String object but need the catch anyhow
            }
        }
    }

}

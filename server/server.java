/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import TTT.TTTserver;
import client.Message;
import client.request;
import gameserver.GameServer;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.sql.Array;
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
public class server {

    //add some log info for debugging
    private ServerSocket sSocket;
    private ArrayList<chatServerThread> al; //array of all current threads oppened
    private ArrayList<String> tttRoom = new ArrayList<String>();
    private ArrayList<String> dankRoom = new ArrayList<String>();

    public server(int port) {
        try {
            sSocket = new ServerSocket(port);

            al = new ArrayList<chatServerThread>();
            sql.connect(); // connect sql to server 

            new Thread() {
                @Override
                public void run() {
                    GameServer s = new GameServer(1600);
                    s.listen();
                }
            }.start();

            Thread t = new TTTserver(8901);
            t.start();

        } catch (IOException ex) {
            Logger.getLogger(server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void listen() {
        while (true) {
            try {

                Socket socket = sSocket.accept();
                Thread t = new ServerThread(socket);
                //al.add((ServerThread) t);//add to list 
                t.start();
            } catch (IOException ex) {
                Logger.getLogger(server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private synchronized void transmitMessage(Message msg) {
        ArrayList<Integer> ports = sql.getUsersPorts(msg.recipients);
        for (int i = al.size() - 1; i >= 0; i--) {
            chatServerThread ct = al.get(i);
            Integer port1 = ct.socket.getPort();
            if (ports.contains(port1)) {
                try {

                    ct.output.writeObject(msg);
                } catch (IOException ex) {
                    al.remove(i);
                    
                    Logger.getLogger(server.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        }

    }

    private synchronized void broadcast(String message) {
        // add HH:mm:ss and \n to the message
        //String time = sdf.format(new Date());
        //String messageLf = time + " " + message + "\n";
        // display message on console or GUI
//		if(sg == null)
//			System.out.print(messageLf);
//		else
//			sg.appendRoom(messageLf);     // append in the room window

        // we loop in reverse order in case we would have to remove a Client
        // because it has disconnected
        for (int i = al.size(); --i >= 0;) {
            chatServerThread ct = al.get(i);
            try {
                // try to write to the Client if it fails remove it from the list
                ct.output.writeUTF(message);
            } catch (IOException ex) {
                al.remove(i);
                Logger.getLogger(server.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public static void main(String[] args) {
        server s = new server(1555);
        s.listen();
    }

    public class ServerThread extends Thread {

        // idea for chatrooms: add ID to each serverthread, add a list of server threads in server, to each token add the ID corresponding to the server thread
        // when a client wants to send a message it sends with it the list of users to send to, then we look up their IDs from the sessions DB and broadcast to them
        //for sql commands , place them in sql class and use
        private Session s = null;
        private Socket socket;
        private ObjectOutputStream output;
        private ObjectInputStream input;
        private boolean isrunning = false;
        private String user;

        public ServerThread(Socket socket) {
            this.socket = socket;
            try {

                output = new ObjectOutputStream(this.socket.getOutputStream());
                input = new ObjectInputStream(this.socket.getInputStream());
                System.out.println("ServerThread port socke n# " + socket.getPort());
                isrunning = true;

            } catch (IOException ex) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void run() {
            try {
                request state = new request("false");
                String user = null;
                while (isrunning) {
                    // work on making this cleaner
                    state = new request();
                    request line = (request) input.readObject();
                    String command = line.command;
                    System.out.println("server decoder " + command);

                    if (command.equals("login")) {
                        user = line.username;
                        System.out.println(user + "is attempting to login");
                        if (sql.isTheresession(user)) {
                            System.out.println("Already LOGGED IN user " + user);
                            state.response = "false";
                            output.writeObject(state);
                        }
                        else {
                            boolean state1;
                            state1 = sql.validUser(line.username, line.password, 1);
                            if (state1 == true) // if successful login , create session
                            {
                                state.response = "true";
                                System.out.println("Successful login " + user);
                                this.user = line.username;
                                s = new Session(user, socket.getLocalAddress().toString(), socket.getPort(), "");
                            }
                            System.out.println(state.response);
                            output.writeObject(state);
                        }
                    }
                    else if (command.equals("authenticate")) {
                        boolean state1 = sql.validUser(line.username, line.password, 0);
                        if (state1) {
                            state.response = "true";
                        }
                        else {
                            state.response = "false";
                        }

                        output.writeObject(state);
                    }
                    else if (command.equals("register")) {
                        state = sql.register(line);
                        output.writeObject(state);
                    }
                    else if (command.equals("logout")) {
                        s.terminate();
                        isrunning = false;
                        if (socket != null) {
                            socket.close();
                        }
                        if (output != null) {
                            output.close();
                        }
                        if (input != null) {
                            input.close();
                        }
                        // we have closed the connections
                    }
                    else if (command.equals("getFriendsList")) {

                        state = sql.getFriendsList(line.username);
                        state.response = "list";

                        output.writeObject(state);

                    }
                    else if (command.equals("findUser")) {
                        if (sql.isThereUser(line.username)) {
                            state.friends.add(line.username);
                        }
                        output.writeObject(state);

                    }
                    else if (command.equals("sendFriendRequest")) {
                        // from in username , to in dof
                        if (sql.sendFriendRequest(line.username, line.user2)) {
                            state.response = "true";
                        }
                        else {
                            state.response = "false";
                        }

                        output.writeObject(state);

                    }
                    else if (command.equals("getFriendRequests")) {
                        state = sql.getFriendRequests(line.username);
                        state.response = "list";
                        output.writeObject(state);

                    }
                    else if (command.equals("acceptRequest")) // for the follwing friend requests: username==fromuser, user2=touser
                    {
                        boolean add = sql.addFriend(line.username, line.user2);
                        if (add) {
                            state.response = "true";
                            sql.rejectRequest(line.username, line.user2);
                            sql.rejectRequest(line.user2, line.username);
                        }
                        else {
                            state.response = "false";
                        }
                        output.writeObject(state);

                    }
                    else if (command.equals("rejectRequest")) {
                        boolean add = sql.rejectRequest(line.username, line.user2);
                        if (add) {
                            state.response = "true";
                        }
                        else {
                            state.response = "false";
                        }
                        output.writeObject(state);

                    }
                    else if (command.equals("removeFriend")) {

                        boolean add = sql.removeFriend(line.username, line.user2);
                        if (add) {
                            state.response = "true";
                        }
                        else {
                            state.response = "false";
                        }
                        output.writeObject(state);
                    }
                    else if (command.equals("getInfo")) {

                        state = sql.getInfo(line.username);
                        state.response = "info";
                        output.writeObject(state);

                    }
                    else if (command.equals("retrieveMessages")) {
                        state = sql.retreiveMessages(line.username); // username is the recipient and friends are the senders
                        state.response = "messages";
                        output.writeObject(state);

                    }
                    else if (command.equals("chat")) // only by chat client
                    {

                        System.out.println("server user is " + user);
                        //check if user is in sessions first
                        user = line.username;
                        if (sql.isTheresession(user)) {
                            System.out.println("Chat Connection now ONLINE");
                            Thread t = new chatServerThread(socket, user);
                            al.add((chatServerThread) t);//add to list
                            isrunning = false;
                            t.start();
                            state.response = "true";

                        }

                    }
                    else if (command.equals("getPreferences")) {
                        state = sql.getPreferences(line.username);
                        state.response = "preferences";
                        output.writeObject(state);

                    }
                    else if (command.equals("setPreferences")) {
                        sql.setPreferences(line);
                        state.response = "set preferences";
                        output.writeObject(state);

                    }
                    else if (command.equals("updateInfo")) //used only for edit profile
                    {
                        sql.updateInfo(line);
                        state.response = "update info";
                        output.writeObject(state);

                    }
                    else if (command.equals("setStatus")) {
                        sql.setStatus(line.username, line.statuss.get(0));
                        output.writeObject(state);

                    }
                    else if (command.equals("changePassword")) {
                        boolean resp = sql.changePassword(line.username, line.password, line.firstname);
                        if (resp) {
                            state.response = "true";
                        }
                        else {
                            state.response = "false";
                        }
                        output.writeObject(state);

                    }

                    else if (command.equals("connectTTT")) {
                        if(!line.username.equals(" ")) tttRoom.add(line.username); //added to chatroom
                        state.friends = new ArrayList<String>(tttRoom);
                        state.response = "chatRoomRecipients";
                        output.writeObject(state);

                    }

                    else if (command.equals("connectDANK")) {
                       if(!line.username.equals(" ")) dankRoom.add(line.username); //added to chatroom
                        state.friends = new ArrayList<String>(dankRoom);
                        state.response = "chatRoomRecipients";
                        output.writeObject(state);

                    }

                    else if (command.equals("inviteGame")) {
                        sql.inviteGame(line.username, line.user2, line.number, line.dof); // from , to ,game
                        output.writeObject(state);

                    }
                    else if (command.equals("removeInvite")) {
                        sql.removeInvite(line.username, line.user2, line.number, line.dof); // from , to ,game
                        output.writeObject(state);

                    }

                    else if (command.equals("getInvites")) {
                        state = sql.getInvites(line.username);
                        state.response = "invites";
                        output.writeObject(state);

                    }
                    else if (command.equals("createTTTserver")) {
                        Thread t = new TTTserver(Integer.valueOf(line.dof));
                        t.start();
                        state.response = "createServer";
                        output.writeObject(state);

                    }
                    else if (command.equals("createDANKserver")) {

                        new Thread() {
                            @Override
                            public void run() {
                                GameServer s = new GameServer(Integer.valueOf(line.dof));
                                s.listen();
                            }
                        }.start();
                        state.response = "createServer";
                        output.writeObject(state);

                    }
                    
                    else if (command.equals("createSession"))
                    {
                     sql.createChatSession(line.username, "6666", line.number, Integer.valueOf(line.dof), "game");
                     output.writeObject(state);
                    }
                    else if (command.equals("removeSession"))
                    {
                        sql.removeSessionGame(line.username, line.number,line.dof,"game");
                        output.writeObject(state);
                    }
                    else if (command.equals("getWinsLosses"))
                    {
                        state=sql.getWinsLosses(line.username);
                        output.writeObject(state);
                        
                    }
                    else if (command.equals("getMatchHistory"))
                    {
                     state=sql.getMatchHistory(line.username, line.user2);
                     output.writeObject(state);
                        
                    }
                    else {
                        output.writeObject(state);
                    }

                    
                    
                    
                    
                    
                    
                    
                    
                    
                    
                }

            } catch (IOException ex) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public class chatServerThread extends Thread {

        private Socket socket;
        private ObjectOutputStream output;
        private ObjectInputStream input;
        private Session sChat = null; // chat session
        private String userChat;
        private String token;

        public chatServerThread(Socket socket, String user) {
            this.socket = socket;
            try {
                output = new ObjectOutputStream(this.socket.getOutputStream());// from
                //output=new ObjectOutputStream (this.socket.getOutputStream()); //to : now can send OBJECTS 
                input = new ObjectInputStream(this.socket.getInputStream());
                this.userChat = user;
                token = userChat + socket.getPort();
                sql.createChatSession(userChat, token, socket.getLocalAddress().toString(), socket.getPort(), "chat");

            } catch (IOException ex) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        @Override
        public void run() {
            try {

                while (true) {
                    // work on making this cleaner
                    System.out.println("in chat server");
                    Message msg = (Message) input.readObject();

                    String command = msg.command;
                    System.out.println("chatserver decoder " + command);

                    if (command.equals("chat")) {
                        msg.response = "chat";
                        if (msg.type.equals("personal")) {
                            sql.sendMessages(msg); // update database to save messages

                        }

                        transmitMessage(msg);
                    }

                    else if (command.equals("chatTTT")) {

                        msg.response = "connectTTT"; // msg has recipients set just like 
                        transmitMessage(msg);

                    }
                    else if (command.equals("chatDANK")) {
                        msg.response = "connectDANK";
                        transmitMessage(msg);
                    }

                    else if (command.equals("logout")) {
                        sql.removeSession(userChat, token);
                        msg.response = "logout";
                        output.writeObject(msg);
                        al.remove(this);
                        if (socket != null) {
                            socket.close();
                        }
                        if (output != null) {
                            output.close();
                        }
                        if (input != null) {
                            input.close();
                        }

                    }

                    else if (command.equals("retreiveMessagesFromUser")) {
                        Message resp;
                        resp = sql.retreiveMessagesFromUser(msg.sender, msg.recipients.get(1));
                        resp.response = "messages"; // we place response in recipients
                        resp.recipients = new ArrayList<String>(msg.recipients);
                        output.writeObject(resp);

                    }
                    else if (command.equals("setRead")) {
                        sql.setRead(msg.sender, msg.recipients.get(1));

                    }

                }

            } catch (IOException ex) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

}

/**
 *
 * @author pantsu
 */

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import client.Message;
import client.request;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static server.Session.JDBC_DRIVER;

/**
 *
 * @author Hussein
 */
public class sql {

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    //Change this to point to your database
    static final String DB_URL = "jdbc:mysql://localhost/practice";  //11235813crpto@! for hussein
    //  Database credentials
    static final String USER = "root";
    static final String PASS = "11235813crpto@!";
    static boolean connected = false;
    //Vars to hold links to DB
    static Connection conn = null;

    static public void connect() {
        try {
            conn = conn = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException ex) {
            Logger.getLogger(sql.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            Class.forName(JDBC_DRIVER); //sql
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(sql.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
 static public void inviteGame(String from, String to, String game,String port) {



       
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO invitations"
                    + " (fromuser,touser,game,port) "
                    + "VALUES (?, ?,?,?)");

            stmt.setString(1, from);
            stmt.setString(2, to);
            stmt.setString(3, game);
            stmt.setString(4,port);

            int ok = stmt.executeUpdate(); //Returns the number of rows affected by the execution of the SQL statement. 
     

        } catch (SQLException ex) {
            Logger.getLogger(server.ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
        
        }
    }
    
    
 static public void removeInvite(String from, String to, String game,String port) {



       
        try {
            PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM invitations WHERE fromuser = ? AND touser=? AND game=? AND port=?");

            stmt.setString(1, from);
            stmt.setString(2, to);
            stmt.setString(3, game);
            stmt.setString(4, port);

            int ok = stmt.executeUpdate(); //Returns the number of rows affected by the execution of the SQL statement. 
     

        } catch (SQLException ex) {
            Logger.getLogger(server.ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
        
        }
    }
    
     static public request getInvites(String user) {
         request ok=new request();
         ResultSet rs=null;
        try {
            PreparedStatement stmt = conn.prepareStatement(
                     "Select * FROM invitations where touser=?");

            stmt.setString(1, user);

            rs=stmt.executeQuery();
             while (rs.next())
             {
              ok.friends.add(rs.getString("fromuser")+" "+rs.getString("game")+" "+rs.getString("port"));
                 
             }

        } catch (SQLException ex) {
            Logger.getLogger(server.ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
        return ok;
        }
    }
    
    
    
    static public request register(request line) {

        request good = new request("false");

        if (isThereUser(line.username) || isThereEmail(line.email)) {
            return good;
        }
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO users"
                    + " (username,password,firstName,lastName,dof,gender,number,email,status) "
                    + "VALUES (?, MD5(?), ?, ?, ?, ?, ?, ?,?)");

            stmt.setString(1, line.username);
            stmt.setString(2, line.password);
            stmt.setString(3, line.firstname);
            stmt.setString(4, line.lastname);
            stmt.setString(5, line.dof);
            stmt.setString(6, line.gender);
            stmt.setString(7, line.number);
            stmt.setString(8, line.email);
            stmt.setString(9, "Offline");
            int ok = stmt.executeUpdate(); //Returns the number of rows affected by the execution of the SQL statement. 
            System.out.println(ok);
            if (ok == 1) {
                good.response = "true";
            }

        } catch (SQLException ex) {
            Logger.getLogger(server.ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return good;
        }
    }

    
    static public String getUserFromPortIp(String ip,String port)
    {
        String user="";
         ResultSet ok;
   
        try {
            System.out.println("ip iss "+ip + "pors is "+port);
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT username FROM sessions WHERE address = ? AND port=? AND chat='game'");
            stmt.setString(1, ip);
            stmt.setString(2, port);
            ok = stmt.executeQuery();
            if (ok.next()) {
                user=ok.getString("username");
            }
            return user;
        } catch (SQLException ex) {
            Logger.getLogger(server.ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return user;
        }
        
        
        
    }
    
    
    
    
    
    static public void insertMatchRecord( String ip1 , String port1 ,String ip2 , String port2, String game ,String outcome)
    {
        int ok;
   
      
        
            try {
                String user1= sql.getUserFromPortIp(ip1,port1);
                String user2 =sql.getUserFromPortIp(ip2,port2);
                System.err.println("userr 1 and 22  "+ user1 +user2);
                PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO matches"
                        + " (user1,user2,game,outcome) "
                        + "VALUES (?,?,?,?)");
                stmt.setString(1, user1);
                stmt.setString(2, user2);
              stmt.setString(3, game);
                stmt.setString(4, outcome);
                
                
                ok = stmt.executeUpdate();
               
            } catch (SQLException ex) {
                Logger.getLogger(server.ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            } 

        
        
        
        
    }
    static public boolean changePassword(String username,String oldpass,String newpass) {
        int ok;
        boolean good = false;
        if (validUser(username, oldpass, 0))
        {
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE users\n"
                    + "SET password=MD5(?)\n"
                    + "WHERE username = ?;");
            stmt.setString(1, newpass);
            stmt.setString(2, username);
            ok = stmt.executeUpdate();
            if (ok==1) good=true;
            
        } catch (SQLException ex) {
            Logger.getLogger(server.ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } 
        }
        else good=false;
            return good;
        

    }
    
    
    
    
      static public boolean setPreferences(request pref) {
        int ok;
        boolean good = false;
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE preferences\n"
                    + "SET firstName=?, lastName=?, country=? , number=?, bio=?, avatar=?\n"
                    + "WHERE username = ?;");
            stmt.setString(1, pref.firstname);
            stmt.setString(2, pref.lastname);
            stmt.setString(3, pref.nationality);
            stmt.setString(4, pref.number);
            stmt.setString(5, pref.bio);
            stmt.setString(6, pref.avatar);
            stmt.setString(7, pref.username);
            ok = stmt.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.getLogger(server.ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return good;
        }

    }
    
          static public boolean updateInfo(request pref) {
        int ok;
        boolean good = false;
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE users\n"
                    + "SET firstName=?, lastName=?, nationality=? , number=? , bio=?\n"
                    + "WHERE username = ?;");
            stmt.setString(1, pref.firstname);
            stmt.setString(2, pref.lastname);
            stmt.setString(3, pref.nationality);
            stmt.setString(4, pref.number);
            stmt.setString(5, pref.bio);
            stmt.setString(6, pref.username); // add avatar later
            ok = stmt.executeUpdate();
            
        } catch (SQLException ex) {
            Logger.getLogger(server.ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return good;
        }

    }
    
      
    static public boolean setStatus(String user, String status) {
        int ok;
        boolean good = false;
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE users\n"
                    + "SET Status =?\n"
                    + "WHERE username = ?;");
            stmt.setString(1, status);
            stmt.setString(2, user);
            ok = stmt.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(server.ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return good;
        }

    }

    static public boolean sendFriendRequest(String fromuser, String touser) {
        int ok;
        boolean good = false;
        if (areWeFriends(fromuser, touser)) {
            return false;
        }
        else {
            try {

                PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO friendrequests"
                        + " (fromuser,touser) "
                        + "VALUES (?,?)");
                stmt.setString(1, fromuser);
                stmt.setString(2, touser);
                ok = stmt.executeUpdate();
                if (ok == 1) {
                    good = true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(server.ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                return good;
            }

        }

    }

    static public boolean isThereEmail(String email) {
        ResultSet ok;
        boolean good = false;
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT email FROM users WHERE email = ?");
            stmt.setString(1, email);
            ok = stmt.executeQuery();
            if (ok.next()) {
                good = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(server.ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return good;
        }
    }

    
    static public boolean isThereUser(String username) {
        ResultSet ok;
        boolean good = false;
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT username FROM users WHERE username = ?");
            stmt.setString(1, username);
            ok = stmt.executeQuery();
            if (ok.next()) {
                good = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(server.ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return good;
        }
    }

    static public boolean validUser(String username, String pass,int type) { // type indicates if login or authenticate, 1 for login , 0 for authenticate
        ResultSet ok;
        boolean good = false;
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT password FROM users WHERE username = ? AND password = MD5(?)");
            stmt.setString(1, username);
            stmt.setString(2, pass);
            ok = stmt.executeQuery();
            if (ok.next()) {
                good = true;
                if (type==1)
                setStatus(username, "Online");
            }
        } catch (SQLException ex) {
            Logger.getLogger(server.ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return good;
        }
    }

    static public boolean removeSession(String user, String token) {

        boolean good = false;

        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM sessions WHERE username = ? AND token=MD5(?)");
            stmt.setString(1, user);
            stmt.setString(2, token);
            int ok = stmt.executeUpdate();
            if (ok == 1) {
                System.out.println("deleted chat session row");
                good = true;
            }

        } catch (SQLException ex) {
            Logger.getLogger(server.ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return good;
        }

    }

    
     static public boolean removeSessionGame(String user, String ip,String port,String chat) {

        boolean good = false;

        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM sessions WHERE username = ? AND address=? AND port=? AND chat=?");
            stmt.setString(1, user);
            stmt.setString(2, ip);
            stmt.setString(3,port);
            stmt.setString(4, "game");
            int ok = stmt.executeUpdate();
        

        } catch (SQLException ex) {
            Logger.getLogger(server.ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return good;
        }

    }
    
    
    
    
    static public void createChatSession(String user, String token, String address, int port, String chat) {

        try {

            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO sessions (username, token, address, port,chat) values (?, MD5(?), ?, ?,?)");
            stmt.setString(1, user);
            stmt.setString(2, token);
            stmt.setString(3, address);
            stmt.setString(4, Integer.toString(port));
            stmt.setString(5, chat);
            int ok = stmt.executeUpdate();
            if (ok == 1) {
                System.out.println("new session");
            }

        } catch (SQLException ex) {
            Logger.getLogger(server.ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    static public boolean isTheresession(String user) {
        PreparedStatement stmt;
        ResultSet rs = null;
        boolean good = false;
        try {
            stmt = conn.prepareStatement(
                    "SELECT username FROM sessions  WHERE username = ?"); // was select from xD
            stmt.setString(1, user);
            rs = stmt.executeQuery();

            if (rs.first()) {
                good = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
        }
        return good;
    }

    static public boolean areWeFriends(String user1, String user2) {
        ResultSet ok;
        boolean good = false;
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT user1 FROM friendslist WHERE user1 = ? AND user2=?");
            stmt.setString(1, user1);
            stmt.setString(2, user2);
            ok = stmt.executeQuery();
            if (ok.next()) {
                good = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(server.ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return good;
        }
    }

    static public boolean addFriend(String user1, String user2) {
        boolean good = false;
        if (areWeFriends(user1, user2)) {
            return good;
        }
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "INSERT INTO friendslist"
                    + " (user1,user2) "
                    + "VALUES (?,?)");
            stmt.setString(1, user1);
            stmt.setString(2, user2);
            int ok = stmt.executeUpdate(); //Returns the number of rows affected by the execution of the SQL statement. 
            int yes = ok;
            stmt = conn.prepareStatement(
                    "INSERT INTO friendslist"
                    + " (user1,user2) "
                    + "VALUES (?,?)");

            stmt.setString(1, user2);
            stmt.setString(2, user1);
            ok = stmt.executeUpdate(); //Returns the number of rows affected by the execution of the SQL statement. 
            yes += ok;
            if (yes == 2) {
                good = true;
            }
            //rejectRequest(user1, user2); // to update friendrequests
            //rejectRequest(user2, user1);
        } catch (SQLException ex) {
            Logger.getLogger(server.ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return good;
        }

    }

    static public ArrayList<Integer> getUsersPorts(ArrayList<String> recipients) {
        ArrayList<Integer> ports = new ArrayList<>();
        ResultSet rs;
        try {
            for (int i = 0; i < recipients.size(); i++) {
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT port FROM sessions WHERE username=? AND chat=?");
                stmt.setString(1, recipients.get(i));
                stmt.setString(2, "chat");
                rs = stmt.executeQuery();
                System.out.println("gerpots itt" + i);
                while (rs.next()) {
                    System.out.println("there is port" + rs.getInt("port"));

                    ports.add(rs.getInt("port"));
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(server.ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return ports;
        }

    }

    static public void sendMessages(Message msg) {

        try {
            for (int i = 0; i < msg.recipients.size(); i++) {
                PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO messages (sender,date,recipient,content) values (?,?,?,?)");
                //INSERT INTO sessions (username, token, address, port,chat) values (?, MD5(?), ?, ?,?)"
                stmt.setString(1, msg.sender);
                stmt.setString(2, msg.date);
                stmt.setString(3, msg.recipients.get(i));
                stmt.setString(4, msg.content);
                int ok = stmt.executeUpdate();

            }

        } catch (SQLException ex) {
            Logger.getLogger(server.ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

       static public request getWinsLosses (String user)
    {
         ResultSet ok;
         request resp=new request();
        try {
           int wins=0,losses=0;
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT user1 FROM matches WHERE user1=?");
            stmt.setString(1, user);
  
            ok = stmt.executeQuery();
            while (ok.next()) {
                
                wins++;
            }
           stmt = conn.prepareStatement(
                    "SELECT user1 FROM matches WHERE user2=?");
            stmt.setString(1, user);
  
            ok = stmt.executeQuery();
            while (ok.next()) {
                
                losses++;
            }
            
            resp.username=String.valueOf(wins);
            resp.user2=String.valueOf(losses);
            
            

        } catch (SQLException ex) {
            Logger.getLogger(server.ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return resp;
        }
        
        
    }
    
    
      static public request getMatchHistory(String user1, String user2)
    {
         ResultSet ok;
         request resp=new request();
        try {
           int wins=0,losses=0;
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT user1 FROM matches WHERE user1=? AND user2=?");
            stmt.setString(1, user1);
            stmt.setString(2, user2);
  
            ok = stmt.executeQuery();
            while (ok.next()) {
                
                wins++;
            }
           stmt = conn.prepareStatement(
                    "SELECT user1 FROM matches WHERE user1=? AND user2=?");
            stmt.setString(1, user2);
            stmt.setString(2, user1);
  
            ok = stmt.executeQuery();
            while (ok.next()) {
                
                losses++;
            }
            
            resp.username=String.valueOf(wins);
            resp.user2=String.valueOf(losses);
            
            

        } catch (SQLException ex) {
            Logger.getLogger(server.ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return resp;
        }
        
        
    }
    
    
    
    
    
    
    
    
    
    static public request retreiveMessages (String recipient)
    {
         ResultSet ok;
         request resp=new request();
        try {
            System.out.println("Recip is "+recipient);
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT sender FROM messages WHERE (recipient=? AND readMessage=?)");
            stmt.setString(1, recipient);
            stmt.setString(2,"false");
            ok = stmt.executeQuery();
            while (ok.next()) {
                
                resp.friends.add(ok.getString("sender"));
            }

        } catch (SQLException ex) {
            Logger.getLogger(server.ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return resp;
        }
        
        
    }
    
    static public void setRead ( String touser, String fromuser) // sets all messages between these two users as read
    {

        try {
     
            PreparedStatement stmt = conn.prepareStatement(
                    "UPDATE messages SET readMessage=? WHERE ( sender=? AND recipient=?) OR (sender=? AND recipient=?) ");
            stmt.setString(1, "true");
            stmt.setString(2, fromuser);
            stmt.setString(3, touser);
            stmt.setString(4, touser);
            stmt.setString(5, fromuser);
             stmt.executeUpdate();
            
         

        } catch (SQLException ex) {
            Logger.getLogger(server.ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
    
    
    static public Message retreiveMessagesFromUser (String touser, String fromuser)
    {
         ResultSet ok;
         Message resp=new Message();
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM messages WHERE ( sender=? AND recipient=?) OR (sender=? AND recipient=?) ");
            stmt.setString(1, fromuser);
            stmt.setString(2, touser);
            stmt.setString(3, touser);
            stmt.setString(4, fromuser);
            ok = stmt.executeQuery();
            
            while (ok.next()) {
                String messages = ok.getString("date") + " " +ok.getString("sender") +":"+"\n"+ok.getString("content")+"\n" ;
                resp.messages.add(messages);
            }

        } catch (SQLException ex) {
            Logger.getLogger(server.ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return resp;
        }

        
        
    }
    static public boolean removeFriend(String user1, String user2) {
        boolean good = false;
        if (!areWeFriends(user1, user2)) {
            return good;
        }
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM friendslist WHERE user1 = ? AND user2=?");
            stmt.setString(1, user1);
            stmt.setString(2, user2);
            int ok = stmt.executeUpdate(); //Returns the number of rows affected by the execution of the SQL statement. 
            int yes = ok;
            stmt = conn.prepareStatement(
                    "DELETE FROM friendslist WHERE user1 = ? AND user2=?");
            stmt.setString(1, user2);
            stmt.setString(2, user1);
            ok = stmt.executeUpdate(); //Returns the number of rows affected by the execution of the SQL statement. 
            yes += ok;
            if (yes == 2) {
                good = true;
            }

        } catch (SQLException ex) {
            Logger.getLogger(server.ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return good;
        }

    }

    static public boolean rejectRequest(String user1, String user2) {
        boolean good = false;

        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "DELETE FROM friendrequests WHERE fromuser = ? AND touser=?");
            stmt.setString(1, user1);
            stmt.setString(2, user2);
            int ok = stmt.executeUpdate(); //Returns the number of rows affected by the execution of the SQL statement. 
            if (ok == 1) {
                good = true;
            }

        } catch (SQLException ex) {
            Logger.getLogger(server.ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return good;
        }

    }

    static public String getFriends(String user1) {
        ResultSet ok;
        String good = "";
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT user2 FROM friendslist WHERE user1 = ?");
            stmt.setString(1, user1);
            ok = stmt.executeQuery();
            while (ok.next()) {
                good += ok.getString("user2");
                
            }

        } catch (SQLException ex) {
            Logger.getLogger(server.ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return good;
        }

    }

    static public request getFriendsList(String user) {
        request ok = null;
        ResultSet rs = null;
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT status, username\n"
                    + "FROM practice.users\n"
                    + "WHERE EXISTS (SELECT user1 FROM practice.friendslist WHERE user2 = practice.users.username AND user1=?)");
            stmt.setString(1, user);
            rs = stmt.executeQuery();
            ok = new request();
            while (rs.next()) {

                ok.friends.add(rs.getString("username"));
                ok.statuss.add(rs.getString("status"));

            }

        } catch (SQLException ex) {
            Logger.getLogger(server.ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return ok;
        }
    }

     static public request getPreferences(String user) {
        request ok = new request();
        ResultSet rs = null;
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM preferences WHERE username=?");
            stmt.setString(1, user);
            rs = stmt.executeQuery();
            ok = new request();

            if (rs.next()) {

                ok.username = rs.getString("username");
                ok.firstname = rs.getString("firstName");
                ok.lastname = rs.getString("lastName");
                ok.bio = rs.getString("bio");
                ok.nationality = rs.getString("country");

                ok.avatar = rs.getString("avatar");
                ok.number = rs.getString("number");

            }

        } catch (SQLException ex) {
            Logger.getLogger(server.ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return ok;
        }

    }
     
     
     
     
     
     
     
    static public request getInfo(String user) {
        request ok = null;
        ResultSet rs = null;
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT * FROM users WHERE username=?");
            stmt.setString(1, user);
            rs = stmt.executeQuery();
            ok = new request();

            if (rs.next()) {

                ok.username = rs.getString("username");
                ok.dof = rs.getString("dof");
                ok.firstname = rs.getString("firstName");
                ok.lastname = rs.getString("lastName");
                ok.gender = rs.getString("gender");
                ok.email = rs.getString("email");
                ok.bio = rs.getString("bio");
                ok.nationality = rs.getString("nationality");
                ok.avatar = rs.getString("avatar");
                ok.number = rs.getString("number");

            }

        } catch (SQLException ex) {
            Logger.getLogger(server.ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return ok;
        }

    }

    static public request getFriendRequests(String user) {
        request ok = null;
        ResultSet rs = null;
        try {
            PreparedStatement stmt = conn.prepareStatement(
                    "SELECT fromuser \n"
                    + "FROM friendrequests\n"
                    + "WHERE touser=? ");
            stmt.setString(1, user);
            rs = stmt.executeQuery();
            ok = new request();
            while (rs.next()) {

                ok.friends.add(rs.getString("fromuser"));

            }

        } catch (SQLException ex) {
            Logger.getLogger(server.ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            return ok;
        }
    }

}

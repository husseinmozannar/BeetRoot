/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import static server.sql.setStatus;

/**
 *
 * @author pantsu
 */
public class Session {

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    //Change this to point to your database
    static final String DB_URL = "jdbc:mysql://localhost/practice"; // remmeber to CHANGE

    //  Database credentials
    static final String USER = "root";
    static final String PASS = "11235813crpto@!"; //CHANGE

    //Vars to hold links to DB
      public Connection conn = null;
    public String token = null;

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    private String SessionUser = null;
    public String SessionAddress = null;
    public int SessionPort = 0;

    public String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return sb.toString();
    }

    public Session(String user, String address, int port,String chat) {
        try {

            Class.forName(JDBC_DRIVER);
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            if (!isThere(user)) { 
                token = randomString(64);
                SessionUser = user;
                SessionAddress = address;
                SessionPort = port;
                PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO sessions (username, token, address, port,chat) values (?, MD5(?), ?, ?,?)");
                stmt.setString(1, user);
                stmt.setString(2, token);
                stmt.setString(3, address);
                stmt.setString(4, Integer.toString(port));
                stmt.setString(5, chat);
                int ok=stmt.executeUpdate();
                System.out.println("Trying to put session "+chat);
                if (ok==1){  System.out.println("new session");}
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

  
    
    public void terminate() {
        PreparedStatement stmt;
        try {
            stmt = conn.prepareStatement(
                    "DELETE FROM sessions WHERE username = ?");
            stmt.setString(1, SessionUser);
            int ok=stmt.executeUpdate();
            if(ok==1){System.out.println("deleted row");
            setStatus(SessionUser,"Offline");
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean isThere(String user) {
        PreparedStatement stmt;
        ResultSet rs = null;
        boolean good = false;
        try {
            stmt = conn.prepareStatement(
                    "SELECT username FROM sessions  WHERE username = ?"); // was select from xD
            stmt.setString(1, user);
            rs = stmt.executeQuery();
            
            if(rs.first()) {
                good = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
        }
        return good;
    }
    
    public String getAddress(String user) {
        PreparedStatement stmt;
        ResultSet rs = null;
        String address = "";
        try {
            if (isThere(user)) { 
                stmt = conn.prepareStatement( "SELECT  address FROM sessions  WHERE username = ?");
                stmt.setString(1, user);
                rs = stmt.executeQuery();
                if(rs.next())
                address = rs.getString("address");
                System.out.println("address"+address);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
        }
        return address;
    }
    
        public int getPort(String user) {
        PreparedStatement stmt;
        ResultSet rs = null;
        int port = 0;
        try {
            if (isThere(user)) { // was !isthere , should enter if we have users
                stmt = conn.prepareStatement(
                        "SELECT port FROM sessions  WHERE username = ?");
                stmt.setString(1, user);
                rs = stmt.executeQuery();
                port = Integer.parseInt(rs.getString("port"));
                System.out.println("[prttt "+port);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
        }
        return port;
    }
    
   
    public boolean compare(String other) {
        PreparedStatement stmt;
        ResultSet rs;
        boolean good = false;
        try {
            stmt = conn.prepareStatement(
                    "SELECT token FROM sessions  WHERE username = ? AND token = MD5(?)");
            stmt.setString(1, SessionUser);
            stmt.setString(2, other);
            rs = stmt.executeQuery();
            if (rs.next()) {
                good = true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(Session.class.getName()).log(Level.SEVERE, null, ex);
        }
        return good;
    }
}

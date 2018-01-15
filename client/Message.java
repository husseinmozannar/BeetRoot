/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.util.ArrayList;

/**
 *
 * @author Hussein
 */
public class Message implements java.io.Serializable {

    public String sender = "";
    public String content = "";
    public String date = "";
    public ArrayList<String> recipients = new ArrayList<String>();
    public String response = ""; //for server info used
    public String command = "";
    public String type="";
    public  ArrayList<String> messages = new ArrayList<String>();
    public Message(String s, String c, String d, ArrayList<String> r) {
        command = "chat";
        sender = s;
        content = c;
        date = d;
        recipients = r;
    }

    public Message() {
        sender = "";
        content = "";
        date = "";
        messages = new ArrayList<String>();
        recipients = new ArrayList<String>();
        response = ""; //for server info used
        command = "";

    }
}

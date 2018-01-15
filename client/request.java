/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.sql.Array;
import java.sql.ResultSet;
import java.util.ArrayList;

/**
 *
 * @author Hussein
 * */
 
public class request implements java.io.Serializable { //serializable for 
    public String command;
    public String username;
    public String user2;
    public String password;
    public String email;
    public String firstname;
    public String lastname;
    public String dof;
    public String gender;
    public String number;
    public String response;
    public String avatar;
    public String bio;
    public String nationality;
    public ArrayList<String> friends=new ArrayList<>();
    public ArrayList<String> statuss=new ArrayList<>();
    public request (String response) //used for server responses to requests: true false or otheer
    {
        this.response=response;
    }
    
    public request(String command,String username,String password) //used for login
    {
        this.command=command;
        this.username=username;
        this.password=password;
    }
    public request(String command,String username,String password ,String firstname,String lastname,String dof,String gender,String number,String email) //used for register
    {
        this.command=command;
        this.username=username;
        this.password=password;
        this.firstname=firstname;
        this.lastname=lastname;
        this.dof=dof;
        this.gender=gender;        
        this.number=number;
        this.email=email;
        
    }
   
    public request()
    {
        this.command= "";
        this.username= "";
        this.password= "";
        this.firstname= "";
        this.lastname= "";
        this.dof= "";
        this.gender= "";        
        this.number= "";
        this.email= "";

    }
    


     
}


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author p1509283
 */
public class Server {

    private static Socket socket;
    private static String state;
    private static String[] messages;
    
    static {
        messages = new String[3];
        messsages[0] = "From: John Doe <jdoe@machine.example>
    To: Mary Smith <mary@example.net>
    Subject: Saying Hello
    Date: Fri, 21 Nov 1997 09:55:06 
    -
    0600
    Message
    -
    ID: <1234@local.machine.example>
    This is a message just to
    say hello.
    So, "Hello".
    . "
    }
    
    public static void main(String[] args) {
        Server.state = "closed";
        initSocket();
        connect();
        Server.state = "authorization";
        //Server Ready here

    }

    public static void initSocket() {
        try {
            ServerSocket sock = new ServerSocket(110);
            System.out.println("Waiting for a client");
            Server.socket = sock.accept();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void connect() {
        if (Server.socket == null) {
            System.out.println("Err : fail to connect");
        } else {
            System.out.println("Client found");
            try {
                OutputStream os = Server.socket.getOutputStream();
                BufferedOutputStream bos = new BufferedOutputStream(os);
                byte[] data = "+OK POP3 server ready".getBytes();
                bos.write(data);
                bos.flush();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static void process(){
        try {
            InputStream is = Server.socket.getInputStream();
            byte[] data = new byte[2048];
            int res = 0;
            String result = "";
            int offset = 0;
            do{
                res = is.read(data, offset, 0);
                String s = new String(data);
                result += s.trim();
                data = new byte[2048];
                offset+=res;
            }while (res == 2048 && res != -1);
            
            String[] command = result.split(" ");
            if (command.length == 1){
                command[1]="-1";
            }
            switch(command[0]){
                case("APOP") : 
                    apop(command[1], command[2]);
                    break;
                case("STAT") : 
                    stat();
                    break;
                case("RETR") : 
                    retr(command[1]);
                    break;
                case("QUIT") : 
                    quit();
                    break;
            }
            
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }

    private static void apop(String name, String checksum) {
        switch (Server.state){
            case "closed":
                break;
                
            case "authorization":
                break;
                
            case "transaction":
                break;
                
            case "update":
                break;
        }
        
    }
    
     private static void stat() {
         switch (Server.state){
            case "closed":
                break;
                
            case "authorization":
                break;
                
            case "transaction":
                break;
                
            case "update":
                break;
        }
    }

    private static void retr(String par) {
        switch (Server.state){
            case "closed":
                break;
                
            case "authorization":
                break;
                
            case "transaction":
                break;
                
            case "update":
                break;
        }
    }

    private static void quit() {
        switch (Server.state){
            case "closed":
                break;
                
            case "authorization":
                break;
                
            case "transaction":
                break;
                
            case "update":
                break;
        }
    }

    

}

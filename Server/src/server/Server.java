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
    
    public static void main(String[] args) {
        initSocket();
        connect();
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
                    apop(command[1]);
                    break;
                case("STAT") : 
                    stat();
                    break;
                case("RETR") : 
                    retr();
                    break;
                case("QUIT") : 
                    quit();
                    break;
            }
            
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }

    private static void apop() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
     private static void stat() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static void retr() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private static void quit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    

}

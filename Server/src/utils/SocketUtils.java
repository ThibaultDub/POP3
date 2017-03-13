/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Server;

/**
 *
 * @author p1509019
 */
public class SocketUtils {
    public static String read(Socket socket) {
        try {
            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
            String result = "";
            String currentChar = null;
            String lastChar = null;
            do {
                lastChar = currentChar;
                currentChar = Character.toString((char)bis.read());
                result += currentChar;
            }while (!(currentChar.contains("\n") && lastChar.contains("\r")));
            result = result.substring(0,result.length()-2);
            result = result.toUpperCase();
            return result;
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
    
    public static String read(Socket socket, int size) {
        try {
            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
            String result = "";
            String currentChar = null;
            String lastChar = null;
            for(int i=0; i<size;i++) {
                lastChar = currentChar;
                currentChar = Character.toString((char)bis.read());
                result += currentChar;
            }
            result = result.substring(0,result.length()-2);
            result = result.toUpperCase();
            return result;
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
    
    public static void write(Socket socket, String message) {
        try {
            OutputStream os = socket.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(os);
            byte[] data = (message+"\r\n").getBytes();
            bos.write(data);
            bos.flush();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

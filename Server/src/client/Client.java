/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Server;
import static server.Server.process;
import utils.SocketUtils;

/**
 *
 * @author p1509019
 */
public class Client {

    private static Socket socket;
    private static String state;
    private static String userName;
    private static final String address = "127.0.0.1";
    private static final int port = 110;
    private static String timestamp;

    public static void main(String[] args) {
        Client.state = "closed";     
        initSocket();
        if(!connect()){
           System.exit(0); 
        }
        try {
            String checksum = "mary" + Arrays.toString(MessageDigest.getInstance("MD5").digest(timestamp.getBytes()));
            Client.apop("mary", checksum);
            //System.out.println(Client.retr(1));
            //System.out.println(Client.retr(2));
            Client.quit();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void initSocket() {
        try {
            Client.socket = new Socket(InetAddress.getByName(Client.address), Client.port);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean connect() {
        String res = SocketUtils.read(Client.socket);
        if (res.split(" ")[0].equals("+OK")) {
            timestamp = res.split(";")[1];
            System.out.println("Connecté au serveur POP3.");
            return true;
        }
        else{
            System.out.println("La connexion au serveur POP3 a échoué.");
            return false;
        }
    }
    
    public static void disconnect(){
        if (Client.socket == null) {
            System.out.println("No connection to close");
        }
        else{
            try {
                Client.socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public static void send(String command){
        SocketUtils.write(Client.socket, command);
        System.out.println(SocketUtils.read(Client.socket));
    }
    
    public static boolean apop(String username, String checksum){
        SocketUtils.write(Client.socket,"APOP "+username+" "+checksum);
        String res = SocketUtils.read(Client.socket);
        return res.split(" ")[0].toUpperCase().equals("+OK");
    }
    
    public static String[] stat(){
        SocketUtils.write(Client.socket, "STAT");
        String res = SocketUtils.read(Client.socket);
        String[] split = res.split(" ");
        if(split[0].toUpperCase().equals("+OK"))
            return new String[]{split[1],split[2]};
        else
            return null;
    }
    
    public static String retr(int number){
        SocketUtils.write(Client.socket,"RETR "+number);
        String res = SocketUtils.read(Client.socket);
        int size = Integer.parseInt(res.split(" ")[1]);
        String message = SocketUtils.read(Client.socket, size);
        return message;
    }
    
    public static void quit(){
        SocketUtils.write(Client.socket, "QUIT");
        System.exit(0);
    }
}

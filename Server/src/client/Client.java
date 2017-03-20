/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import server.Server;
import static server.Server.process;
import utils.Console;
import utils.SocketUtils;

/**
 *
 * @author p1509019
 */
public class Client {

    //private static Socket socket;
    private static SSLSocket socket;
    private static String state;
    private static String userName;
    private static String address;
    private static final int port = 58008;
    private static String timestamp;

    public static void main(String[] args) {
        Client.state = "closed";
        boolean connected = false;
        while(!connected){
            Console.display("Saisir l'adresse de la boite :");
            Client.address = Console.read();
            connected = initSecureSocket();
            if (connected) {
                if(!connect())
                    Console.display("La connexion a échoué.");
            }
            else{
                Console.display("La connexion a échoué.");
            }
        }
        Client.state = "authorization";
        boolean authentified = false;
        while (!authentified) {
            Console.display("Nom de la boite aux lettres :");
            String login = Console.read().trim();
            Console.display("Clef :");
            String password = Console.read().trim();
            authentified = Client.apop(login, SocketUtils.md5(password + timestamp));
            if (!authentified) {
                Console.display("L'authentification a échoué.");
            }
        }
        Client.state = "transaction";
        boolean exit = false;
        while (!exit) {
            int command = Console.displayInt("Entrer une fonctionnalité : \n"
                    + "1. Nombre de mesages et taille de la boite\n"
                    + "2. Recupérer un message\n"
                    + "3. Quitter");
            switch (command) {
                case 1:
                    String[] statRes = Client.stat();
                    if (statRes != null) {
                        Console.display("Il y a " + statRes[0] + " courriers dans la boite (" + statRes[1] + " octets).");
                    }
                    break;
                case 2:
                    command = Console.displayInt("Entrez le numéro du message : ");
                    Console.display(retr(command));
                    break;
                case 3:
                    Console.display("Fermeture de la boite aux lettres.");
                    exit = true;
                    break;
                default:
                    Console.display("La boite aux lettres ne peut pas faire cela.");
                    break;
            }
            Console.display("Appuyez sur la touche 'Entrée' pour continuer.");
            Console.read();
        }
        Client.state = "closed";
    }

    /*public static void initSocket() {
        try {
            Client.socket = new Socket(InetAddress.getByName(Client.address), Client.port);
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
    
    public static boolean initSecureSocket() {
        try {
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            try{
                Client.socket = (SSLSocket) factory.createSocket(Client.address, Client.port);
            }catch(Exception e){
                return false;
            }
            String[] suites = Client.socket.getSupportedCipherSuites();
            Client.socket.setEnabledCipherSuites(suites);
            Client.socket.startHandshake();
            return true;
        }catch (IOException e1) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, e1);
            return false;
        }catch(Exception e2){
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, e2);
            return false;
        }
    }

    public static boolean connect() {
        String res = SocketUtils.read(Client.socket);
        if (res.split(" ")[0].equals("+OK")) {
            timestamp = res.split(";")[1];
            Console.display("Connecté au serveur POP3.");
            return true;
        } else {
            Console.display("La connexion au serveur POP3 a échoué.");
            return false;
        }
    }

    public static void disconnect() {
        if (Client.socket == null) {
            Console.display("No connection to close");
        } else {
            try {
                Client.socket.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void send(String command) {
        SocketUtils.write(Client.socket, command);
        Console.display(SocketUtils.read(Client.socket));
    }

    public static boolean apop(String username, String checksum) {
        if (checksum == null) {
            SocketUtils.write(Client.socket, "APOP " + username);
        } else {
            SocketUtils.write(Client.socket, "APOP " + username + " " + checksum);
        }
        String res = SocketUtils.read(Client.socket);
        return res.split(" ")[0].toUpperCase().equals("+OK");
    }

    public static String[] stat() {
        SocketUtils.write(Client.socket, "STAT");
        String res = SocketUtils.read(Client.socket);
        String[] split = res.split(" ");
        if (split[0].toUpperCase().equals("+OK")) {
            return new String[]{split[1], split[2]};
        } else {
            return null;
        }
    }

    public static String retr(int number) {
        SocketUtils.write(Client.socket, "RETR " + number);
        String res = SocketUtils.read(Client.socket);
        if (res.split(" ")[0].equals("+OK")) {
            int size = Integer.parseInt(res.split(" ")[1]);
            String message = SocketUtils.read(Client.socket, size);
            return message;
        } else {
            return "Le message n'existe pas.";
        }
    }

    public static void quit() {
        SocketUtils.write(Client.socket, "QUIT");
        System.exit(0);
    }
}

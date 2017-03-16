/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.management.ManagementFactory;
import java.net.*;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.Console;
import utils.FileUtils;
import utils.SocketUtils;

/**
 *
 * @author p1509283
 */
public class Server {

    private static ServerSocket ss;
    private static Socket socket;
    private static String state;
    private static String userName;
    private static String timestamp;

    public static void main(String[] args) {
        Server.state = "closed";
        while (true) {
            initSocket();
            connect();
            Server.state = "authorization";
            process();
        }
    }

    public static void initSocket() {
        try {
            Server.ss = new ServerSocket(110);
            Console.display("Waiting for a client");
            Server.socket = ss.accept();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void connect() {
        if (Server.socket == null) {
            Console.display("Fail to connect");
        } else {
            Console.display("Client found");
            String date = new SimpleDateFormat("dd mm yy hh:mm:ss zzz").format(new Date());
            String pid = ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
            String hostname = Server.socket.getInetAddress().toString().split("/")[1];
            timestamp = pid + "." + date + "@" + hostname;
            SocketUtils.write(Server.socket, "+OK POP3 server ready;" + timestamp);
        }
    }

    public static void disconnect() {
        if (Server.socket == null) {
            Console.display("No connection to close");
        } else {
            try {
                Server.socket.close();
                Server.ss.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void process() {
        String result = SocketUtils.read(Server.socket);
        ArrayList<String> command = new ArrayList<String>(Arrays.asList(result.split(" ")));
        Console.display("Commande reçue : '" + result + "'");
        switch (command.get(0)) {
            case ("APOP"):
                if (command.size() == 2) {
                    apop(command.get(1), null);
                } else if (command.size() == 3) {
                    apop(command.get(1), command.get(2));
                } else {
                    invalidCommand();
                }
                break;
            case ("STAT"):
                if (command.size() == 1) {
                    stat();
                } else {
                    invalidCommand();
                }
                break;
            case ("RETR"):
                if (command.size() == 2) {
                    retr(command.get(1));
                } else {
                    invalidCommand();
                }
                break;
            case ("QUIT"):
                if (command.size() == 1) {
                    quit();
                } else {
                    invalidCommand();
                }
                return;
            default:
                commandNotFound();
                break;
        }
        process();
    }

    private static void apop(String name, String checksum) {
        String answer;
        switch (Server.state) {
            case "closed":
                break;
            case "authorization":
                boolean permission = false;
                String mails;
                if (checksum == null) {
                    mails = "";
                    try {
                        Server.userName = name;
                        permission = true;
                    } catch (Exception e) {
                        permission = false;
                    }
                } else {
                    Server.userName = name;
                    try{
                        String checksumLocal = FileUtils.readFile(Server.userName + ".pass").trim();
                        checksumLocal += timestamp;
                        checksumLocal = SocketUtils.md5(checksumLocal);
                        permission = checksum.trim().equals(checksumLocal.trim());
                    }
                    catch(Exception e){
                        permission = false;
                    }
                }
                if (permission) {
                    Server.state = "transaction";
                    mails = FileUtils.readFile(Server.userName + ".mail");
                    String[] splitFile = mails.split("\r\n[.]\r\n");
                    int number = splitFile.length;
                    answer = "+OK maildrop has " + number + (number > 1 ? " messages" : " message");
                    SocketUtils.write(Server.socket, answer);
                } else {
                    Server.userName = null;
                    SocketUtils.write(Server.socket, "-ERR unknown user");
                }
                break;
            case "transaction":
                break;
            case "update":
                break;
        }

    }

    private static void stat() {
        String answer;
        switch (Server.state) {
            case "closed":
                answer = "-ERR : Permission Denied";
                SocketUtils.write(Server.socket, answer);
                break;
            case "authorization":
                answer = "-ERR : Permission Denied";
                SocketUtils.write(Server.socket, answer);
                break;
            case "transaction":
                String mails = FileUtils.readFile(Server.userName + ".mail");
                String[] splitFile = mails.split("\r\n[.]\r\n");
                int number = splitFile.length;
                int size = mails.getBytes().length + 2;
                answer = "+OK " + number + " " + size;
                SocketUtils.write(Server.socket, answer);
                break;
            case "update":
                break;
        }
    }

    private static void retr(String n) {
        String answer;
        switch (Server.state) {
            case "closed":
                answer = "-ERR : Permission Denied";
                SocketUtils.write(Server.socket, answer);
                break;
            case "authorization":
                answer = "-ERR : Permission Denied";
                SocketUtils.write(Server.socket, answer);
                break;
            case "transaction":
                try {
                    String[] splitFile = FileUtils.readFile(Server.userName + ".mail").split("\r\n[.]\r\n");
                    String mail = splitFile[Integer.parseInt(n) - 1];
                    int size = mail.getBytes().length + 2;
                    SocketUtils.write(Server.socket, "+OK " + size + " octets");
                    SocketUtils.write(Server.socket, mail);
                } catch (Exception e) {
                    SocketUtils.write(Server.socket, "-ERR message " + n + " not found.");
                }
                break;
            case "update":
                break;
        }
    }

    private static void quit() {
        String answer;
        switch (Server.state) {
            case "closed":
                break;
            case "authorization":
                answer = "+OK " + Server.userName + " POP3 server signing off";
                SocketUtils.write(Server.socket, answer);
                Server.userName = null;
                Server.disconnect();
                Server.state = "closed";
                break;
            case "transaction":
                Server.userName = null;
                Server.disconnect();
                Server.state = "closed";
                break;
            case "update":
                break;
        }
    }

    private static void commandNotFound() {
        SocketUtils.write(Server.socket, "-ERR command not found");
        Console.display("Command not found");
    }

    private static void invalidCommand() {
        SocketUtils.write(Server.socket, "-ERR invalid command");
        Console.display("Invalid command");
    }
}

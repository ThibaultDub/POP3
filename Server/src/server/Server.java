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
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author p1509283
 */
public class Server {

    private static ServerSocket ss;
    private static Socket socket;
    private static String state;
    private static String userName;

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
            System.out.println("Waiting for a client");
            Server.socket = ss.accept();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void connect() {
        if (Server.socket == null) {
            System.out.println("-ERR : fail to connect");
        } else {
            System.out.println("Client found");
            write("+OK POP3 server ready");
        }
    }
    
    public static void disconnect(){
        if (Server.socket == null) {
            System.out.println("No connection to close");
        }
        else{
            try {
                Server.socket.close();
                Server.ss.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void process() {
        String result = read();

        ArrayList<String> command = new ArrayList<String>(Arrays.asList(result.split(" ")));
        switch (command.get(0)) {
            case ("APOP"):
                if(command.size() == 2)
                    apop(command.get(1), null);
                else if (command.size() == 3)
                    apop(command.get(1), command.get(2));
                else
                    invalidCommand();
                break;
            case ("STAT"):
                if(command.size() == 1)
                    stat();
                else
                    invalidCommand();
                break;
            case ("RETR"):
                if(command.size() == 2)
                    retr(command.get(1));
                else
                    invalidCommand();
                break;
            case ("QUIT"):
                if(command.size() == 1)
                    quit();
                else
                    invalidCommand();
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
                if(checksum == null){
                    mails = "";
                    try{
                        Server.userName = name;
                        mails = readFile();
                        permission = true;
                    }
                    catch(Exception e){
                        permission = false;
                    }                    
                }else{
                    throw new UnsupportedOperationException();
                }
                
                if(permission){
                    Server.state = "transaction";
                    String[] splitFile = mails.split("\r\n[.]\r\n");
                    int number = splitFile.length;
                    answer = "+OK maildrop has " + number + (number>1?" messages":" message");
                    write(answer);
                }else{
                    write("-ERR unknown user");
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
                write(answer);
                break;

            case "authorization":
                answer = "-ERR : Permission Denied";
                write(answer);
                break;

            case "transaction":
                String mails = readFile();
                String[] splitFile = mails.split("\r\n[.]\r\n");
                int number = splitFile.length;
                double size = mails.getBytes().length;
                answer = "+OK " + number + " " + size;
                write(answer);
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
                write(answer);
                break;

            case "authorization":
                answer = "-ERR : Permission Denied";
                write(answer);
                break;

            case "transaction":
                try {
                    String[] splitFile = readFile().split("\r\n[.]\r\n");
                    String mail = splitFile[Integer.parseInt(n)-1];
                    double size = mail.getBytes().length;
                    write("+OK " + size + " octets");
                    write(mail);
                } catch (Exception e) {
                    write("-ERR message "+ n +" not found.");
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
                answer = "+OK "+Server.userName+" POP3 server signing off";
                write(answer);
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
        write("-ERR command not found");
    }
    
    private static void invalidCommand() {
        write("-ERR invalid command");
    }

    public static String readFile() {
        BufferedReader br = null;
        String everything = "";
        try {
            br = new BufferedReader(new FileReader(Server.userName + ".mail"));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            everything = sb.toString();
        } finally {
            try {
                br.close();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
            return everything;
        }
    }

    /*
    public static double fileSize() {
        File file = new File("mails.conf");
        if (file.exists()) {
            return file.length();
        }
        return -1;
    }
    */

    public static String read() {
        try {
            BufferedInputStream bis = new BufferedInputStream(Server.socket.getInputStream());
            byte[] data = new byte[2048];
            String result = "";
            String currentChar = null;
            String lastChar = null;
            do {
                lastChar = currentChar;
                currentChar = Character.toString((char)bis.read());
                result += currentChar;
                data = new byte[2048];
            }while (!(currentChar.contains("\n") && lastChar.contains("\r")));
            result = result.substring(0,result.length()-2);
            result = result.toUpperCase();
            System.out.println("command: " + result);
            return result;
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }
    
    public static void write(String message) {
        try {
            OutputStream os = Server.socket.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(os);
            byte[] data = (message+"\r\n").getBytes();
            bos.write(data);
            bos.flush();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

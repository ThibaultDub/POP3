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

    private static Socket socket;
    private static String state;

    public static void main(String[] args) {
        Server.state = "closed";
        initSocket();
        connect();
        Server.state = "authorization";
        Server.state = "transaction";
        process();

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
            write("+OK POP3 server ready");
        }
    }

    public static void process() {
        String result = read();

        ArrayList<String> command = new ArrayList<String>(Arrays.asList(result.split(" ")));
        if (command.size() == 1) {
            command.add("-1");
        }
        switch (command.get(0)) {
            case ("APOP"):
                apop(command.get(1), command.get(2));
                break;
            case ("STAT"):
                stat();
                System.out.println("on est passés dans stat");
                break;
            case ("RETR"):
                retr(command.get(1));
                break;
            case ("QUIT"):
                quit();
                break;
        }
        process();
    }

    private static void apop(String name, String checksum) {
        switch (Server.state) {
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
        switch (Server.state) {
            case "closed":
                break;

            case "authorization":
                break;

            case "transaction":
                String mails = readFile();
                String[] splitFile = mails.split("\r\n[.]\r\n");
                int number = splitFile.length;
                double size = mails.getBytes().length;
                String answer = "+OK " + number + " " + size;
                write(answer);
                break;

            case "update":
                break;
        }
    }

    private static void retr(String n) {
        switch (Server.state) {
            case "closed":
                break;

            case "authorization":
                break;

            case "transaction":
                String[] splitFile = readFile().split("\r\n[.]\r\n");
                String mail = splitFile[Integer.parseInt(n)];
                double size = mail.getBytes().length;
                write("+OK " + size + " octets");
                write(mail);
                break;

            case "update":
                break;
        }
    }

    private static void quit() {
        switch (Server.state) {
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

    public static String readFile() {
        BufferedReader br = null;
        String everything = "";
        try {
            br = new BufferedReader(new FileReader("mails.conf"));
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

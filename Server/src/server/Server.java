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
			write("+OK POP3 server ready");
        }
    }
    
    public static void process(){
		String result = read();
		
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
				int number = readFile().split("./n").length;
				int size = fileSize();
				String answer = "+OK " + number + " " + size;
				write(answer);
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
	
	public static String readFile(){
		BufferedReader br = new BufferedReader(new FileReader("mails.conf"));
		String everything = "";
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			everything = sb.toString();
		} finally {
			br.close();
			return everything;
		}
	}
	
	public static int fileSize(){
		File file =new File("mails.conf");
		if(file.exists()){
			return file.length();
		}
		return -1;
	}

	public static String read(){
		try{
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
			return result;
		} catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
			return "";
		}
	}
	
	public static void write(String message){
		try {
			OutputStream os = Server.socket.getOutputStream();
			BufferedOutputStream bos = new BufferedOutputStream(os);
			byte[] data = message.getBytes();
			bos.write(data);
			bos.flush();
		} catch (IOException ex) {
			Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}

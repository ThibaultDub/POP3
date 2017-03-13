/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.net.Socket;

/**
 *
 * @author p1509019
 */
public class Connection {
    private Socket socket;
    private String state;
    private static final String STATE_CLOSED = "closed";
    private static final String STATE_TRANSACTION = "transaction";
    private static final String STATE_AUTHORIZATION = "authorization";

    public Connection(Socket socket) {
        this.socket = socket;
        this.state = Connection.STATE_CLOSED;
    }
    
}

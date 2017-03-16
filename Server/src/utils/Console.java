/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Louis
 */
public abstract class Console {

    public static final Integer NOINPUTINT = -1;

    public static void display(String s) {
        System.out.println(s);
    }

    public static String read() {
        try {
            BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
            String s = bufferRead.readLine();
            return s;
        } catch (IOException e) {
            e.printStackTrace();
            display("Mauvaise saisie.");
            return "error";
        }
    }

    public static Integer displayInt(String question) {
        Console.display(question);
        try {
            String answer = read().trim();
            if ("".equals(answer)) {
                return NOINPUTINT;
            }
            return Integer.parseInt(answer);
        } catch (Exception e) {
            display("Mauvaise saisie.");
            return displayInt(question);
        }
    }
}

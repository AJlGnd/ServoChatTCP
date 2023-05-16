package com.mycompany.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class client1 {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public String sendMessage(String msg) throws IOException {
        out.println(msg);
        String resp = in.readLine();
        return resp;
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }

      public static void main(String[] args) throws IOException {
        Scanner input = new Scanner(System.in);
        client1 client = new client1();
        client.startConnection("127.0.0.1", 8080);
            System.out.print("1. Send message");
            System.out.print("  2. Calculator");
            System.out.print("  3. Quit\n");
             
            while (true) {
            System.out.print("/>> ");
            String choice = input.next();
            input.nextLine();

            switch(choice) {
                case "1":
                    System.out.print("Write Message: ");
                    String msg = input.nextLine();
                    String response = client.sendMessage(msg);
                    System.out.println(response);
                    break;
                case "2":
                    System.out.println("Calculator - use format: operation number number (e.g., 'add 5 3')");
                    String calcInput = input.nextLine();
                    String calcResponse = client.sendMessage(calcInput);
                    System.out.println(calcResponse);
                    break;
                case "3":
                    client.stopConnection();
                    return;
                default:
                    System.out.println("Invalid choice. Please choose 1, 2, or 3.");
                    break;
            }
        }
        
      }
    }

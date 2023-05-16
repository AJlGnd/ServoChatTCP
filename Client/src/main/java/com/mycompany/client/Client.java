
package com.mycompany.client;
import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
            try {
            DatagramSocket socket = new DatagramSocket();
            InetAddress serverAddress = InetAddress.getByName("localhost");
            int serverPort = 5000;
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.print("Enter message: ");
                String message = scanner.nextLine();
                byte[] buffer = message.getBytes();
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, serverAddress, serverPort);
                socket.send(packet);

                buffer = new byte[4096];
                
                packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                message = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Server response: " + message);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    }


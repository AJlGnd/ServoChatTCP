package com.mycompany.servertcp;
import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private int port;
    private boolean running = false;
    private Map<String, ClientHandler> clients = new HashMap<>();

    public Server(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        running = true;
        InetAddress bindAddr = InetAddress.getByName("127.0.0.1"); 
        try (ServerSocket serverSocket = new ServerSocket(port, 50, bindAddr)) {
            while (running) {
                Socket clientSocket = serverSocket.accept();
                String clientId = UUID.randomUUID().toString();
                ClientHandler client = new ClientHandler(clientSocket, clientId);
                clients.put(clientId, client);
                new Thread(client).start();
            }
        }
    }
    public void stop() {
        running = false;
    }

    public void sendMessageToAll(String message) {
        for (ClientHandler client : clients.values()) {
            client.sendMessage(message);
        }
    }

    public void sendMessageToClient(String clientId, String message) {
        ClientHandler client = clients.get(clientId);
        if (client != null) {
            client.sendMessage(message);
        }
    }

    class ClientHandler implements Runnable {
        private Socket clientSocket;
        private BufferedReader in;
        private PrintWriter out;
        private String clientId;

        public ClientHandler(Socket clientSocket, String clientId) throws IOException {
            this.clientSocket = clientSocket;
            this.clientId = clientId;
            this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.out = new PrintWriter(clientSocket.getOutputStream(), true);
        }

        public void sendMessage(String message) {
            out.println(message);
        }
        
        public void sendMessageToAll(String message) {
        for (Map.Entry<String, ClientHandler> entry : clients.entrySet()) {
        ClientHandler client = entry.getValue();
        if (client != this) {
            client.sendMessage("Server: " + message);
        }
    }
}
    private String handleCalculatorRequest(String message) throws IOException {
    String[] tokens = message.split(" ");
    if (tokens.length != 3) {
        return "Error: Invalid request";
    }
    double a = Double.parseDouble(tokens[1]);
    double b = Double.parseDouble(tokens[2]);
    String result = "";
    switch (tokens[0]) {
        case "add":
            result = String.valueOf(a + b);
            break;
        case "subtract":
            result = String.valueOf(a - b);
            break;
        case "multiply":
            result = String.valueOf(a * b);
            break;
        case "divide":
            if (b != 0) {
                result = String.valueOf(a / b);
            } else {
                result = "Error: Division by zero";
            }
            break;
        default:
            result = "Error: Invalid operation";
            break;
    }
    return result;
}

   @Override
    public void run() {
        try {
        String inputLine;
            while ((inputLine = in.readLine()) != null) {
            System.out.println("Received message from client " + clientId + ": " + inputLine);
            String outputLine;
            if (inputLine.startsWith("add") || inputLine.startsWith("subtract") || inputLine.startsWith("multiply") || inputLine.startsWith("divide")) {
                outputLine = handleCalculatorRequest(inputLine);
            }
            else if(inputLine.startsWith("broadcast")){
                sendMessageToAll(inputLine.substring("broadcast".length()));
                outputLine = "Server: Broadcast message sent";
            }
            else {
                outputLine = "Server: " + inputLine;
            }
            out.println(outputLine);
            if (inputLine.equals("quit")) {
                break;
            }
        }
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        try {
            in.close();
            out.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            clients.remove(clientId); 
         }
        }
      }
    }
}
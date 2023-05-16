package com.mycompany.serverudp;

import java.net.*;
import java.util.HashMap;
import java.util.ArrayDeque;
import java.util.Deque;


public class Server {

    public static void main(String[] args) throws Exception {
        DatagramSocket socket = new DatagramSocket(5000);
        HashMap<InetAddress, Integer> clients = new HashMap<>();

        byte[] buffer = new byte[4096];

        
        while (true) {
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

            InetAddress clientAddress = packet.getAddress();
            int clientPort = packet.getPort();

            if (!clients.containsKey(clientAddress)) {
                clients.put(clientAddress, clientPort);
                System.out.println("New client connected: " + clientAddress.getHostAddress() + ":" + clientPort);
            }
            
            String message = new String(packet.getData(), 0, packet.getLength());
            System.out.println("Received message from " + clientAddress.getHostAddress() + ":" + clientPort + ": " + message.length());
        
            if (message.equals("calculator")) {
                System.out.println("Client " + clientAddress.getHostAddress() + ":" + clientPort + " requested calculator");
                
                byte[] mess = "Calculator requested".getBytes();

                packet = new DatagramPacket(mess,mess.length,clientAddress,clientPort);
                socket.send(packet);
                
                packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String expression = new String(buffer, 0, packet.getLength());
                System.out.println("Expression: " + expression);

                double result = Calculator.calculate(expression);
                String response = String.format("%.2f", result);

                buffer = response.getBytes();
                packet = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
                socket.send(packet);
                System.out.println("Result sent to client " + clientAddress.getHostAddress() + ":" + clientPort + ": " + response);

                        }
        else {
                        buffer = message.getBytes();
                        packet = new DatagramPacket(buffer, buffer.length, clientAddress, clientPort);
                        socket.send(packet);
                        System.out.println("Echoed message to client " + clientAddress.getHostAddress() + ":" + clientPort + ": " + message);
                    }
                }
    }
    
 


    public class Calculator {
            public static double calculate(String expression) {
            Deque<Double> operands = new ArrayDeque<>();
            Deque<Character> operators = new ArrayDeque<>();

            for (int i = 0; i < expression.length(); i++) {
                char c = expression.charAt(i);
                if (Character.isDigit(c)) {
                    int j = i;
                    while (j < expression.length() && Character.isDigit(expression.charAt(j))) {
                        j++;
                    }
                    double operand = Double.parseDouble(expression.substring(i, j));
                    operands.push(operand);
                    i = j - 1;
                } else if (c == '+' || c == '-' || c == '*' || c == '/') {
                    while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(c)) {
                        double b = operands.pop();
                        double a = operands.pop();
                        char op = operators.pop();
                        double result = applyOperator(a, op, b);
                        operands.push(result);
                    }
                    operators.push(c);
                } else if (c == '(') {
                    operators.push(c);
                } else if (c == ')') {
                    while (!operators.isEmpty() && operators.peek() != '(') {
                        double b = operands.pop();
                        double a = operands.pop();
                        char op = operators.pop();
                        double result = applyOperator(a, op, b);
                        operands.push(result);
                    }
                    operators.pop();
                }
            }

        while (!operators.isEmpty()) {
            double b = operands.pop();
            double a = operands.pop();
            char op = operators.pop();
            double result = applyOperator(a, op, b);
            operands.push(result);
        }

        return operands.pop();
    }

    private static int precedence(char op) {
        if (op == '+' || op == '-') {
            return 1;
        } else if (op == '*' || op == '/') {
            return 2;
        } else {
            return 0;
        }
    }

    private static double applyOperator(double a, char op, double b) {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                return a / b;
            default:
                throw new IllegalArgumentException("Invalid operator: " + op);
        }
        
    }
}}

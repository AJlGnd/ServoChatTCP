
package com.mycompany.servertcp;
import java.io.IOException;


public class ServerTCp {
    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = new Server(8080);
        server.start();
    }
}

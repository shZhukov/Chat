package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

public class Server{
    List<ClientHandler> clients;
    private AuthService authService;

    private static int PORT = 8189;
    ServerSocket server = null;
    Socket socket = null;

    public Server() {
        clients = new Vector<>();
        authService = new SimpleAuthService();

        try {
            server = new ServerSocket(PORT);
            System.out.println("Сервер запущен");

            while (true) {
                socket = server.accept();
                System.out.println("Клиент подключился");


                new ClientHandler(this, socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcastMsg(ClientHandler sender, String msg) {
        String message = String.format("%s : %s", sender.getNickName(), msg);
        for (ClientHandler client : clients) {
            client.sendMsg(message);
        }
    }

    public void subscribe(ClientHandler clientHandler) {
        clients.add(clientHandler);
    }

    public void unsubscribe(ClientHandler clientHandler) {
        clients.remove(clientHandler);
    }

    public void privateMsg (ClientHandler sender, String receiver, String msg){
        for (ClientHandler c: clients){
            if (c.getNickName().equals(receiver)){
                c.sendMsg(msg);
                if(!c.equals(sender)){
                    sender.sendMsg(msg);
                }
                return;
            }
        }
    }

    public AuthService getAuthService(){
        return authService;
    }
}

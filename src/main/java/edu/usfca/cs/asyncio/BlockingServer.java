package edu.usfca.cs.asyncio;


import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class BlockingServer {

    public static void main(String[] args) throws Exception {

        Map<Socket, ConnectionStats> statMap = new HashMap<>();

        ServerSocket ssock = new ServerSocket(7777);

        while (true) {
            Socket client = ssock.accept();
            System.out.println("Connection established: " + client.getInetAddress());

            statMap.put(client, new ConnectionStats());

            while (true) {
                BufferedInputStream buffIn = new BufferedInputStream(client.getInputStream());
                DataInputStream input = new DataInputStream(client.getInputStream());
                try {
                    int messageSize = input.readInt();
                    byte[] payload = new byte[messageSize];
                    input.read(payload);
                    String message = new String(payload);
                    ConnectionStats stats = statMap.get(client);
                    if (stats != null) {
                        stats.messages.add(message);
                        stats.bytes += payload.length;
                    }
                } catch (EOFException e) {
                    System.out.println("Connection lost: " + client.getInetAddress());
                    System.out.println(statMap.get(client));
                    statMap.remove(client);
                    break;
                }
            }
        }
    }

}

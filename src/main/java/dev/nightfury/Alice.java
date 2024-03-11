package dev.nightfury;

import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;

public class Alice {
    private static ServerSocket serverSocket;
    private static Socket socket;
    private static BufferedReader reader;
    private static BufferedWriter writer;

    public static void main(String[] args) throws IOException {
        serverSocket = new ServerSocket(25565);

        while (true) {
            // Warte bis Bob bereit ist die Daten anzunehmen
            socket = serverSocket.accept();

            // reader um Daten von Bob auszulesen und writer um Daten an Bob zu senden
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            // Liste mit zufällig generierten QBits
            List<QBit> bits = generateQBits(20);

            // Sende die QBits zu Bob
            sendBitsToBob(bits);

            // Verbindung wird geschlossen
            closeNetworking();
        }
    }

    public static void sendBitsToBob(List<QBit> bits) throws IOException {
        Gson gson = new Gson();

        for (QBit bit : bits) {
            writer.write(gson.toJson(bit));
            writer.newLine();
        }

        writer.flush();
    }

    public static List<QBit> generateQBits(int amount) {
        List<QBit> bits = new LinkedList<>();

        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 20; i++) {
            char base = '+';

            // 50/50 chance, ob die Basis auf x geändert wird
            if (random.nextBoolean()) {
                base = 'x';
            }

            bits.add(new QBit(random.nextInt(2), base));
        }

        return bits;
    }

    public static void closeNetworking() throws IOException {
        reader.close();
        writer.close();
        socket.close();
    }
}
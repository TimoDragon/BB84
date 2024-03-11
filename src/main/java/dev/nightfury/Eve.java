package dev.nightfury;

import com.google.gson.Gson;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;

public class Eve {
    private static ServerSocket serverSocket;
    private static Socket bobSocket, aliceSocket;
    private static BufferedReader bobReader, aliceReader;
    private static BufferedWriter bobWriter, aliceWriter;

    public static void main(String[] args) throws IOException {
        // Um mit Bob und Alice zu kommunizieren
        setupNetworking();

        List<QBit> qBits = readAliceBits();
        // Zufällig generierte Basen von Eve
        List<Character> eveBases = generateBasis(qBits.size());


    }

    public static void setupNetworking() throws IOException {
        // Server socket
        serverSocket = new ServerSocket(25565);

        // Stelle verbindung mit Bob her
        bobSocket = serverSocket.accept();

        // reader um Daten von Bob auszulesen und writer um Daten an Bob zu senden
        bobReader = new BufferedReader(new InputStreamReader(bobSocket.getInputStream()));
        bobWriter = new BufferedWriter(new OutputStreamWriter(bobSocket.getOutputStream()));

        // Stelle verbindung mit Alice her
        aliceSocket = new Socket("localhost", 25566);

        // reader um Daten von Alice auszulesen und writer um Daten an Alice zu senden
        aliceReader = new BufferedReader(new InputStreamReader(aliceSocket.getInputStream()));
        aliceWriter = new BufferedWriter(new OutputStreamWriter(aliceSocket.getOutputStream()));
    }

    public static List<QBit> readAliceBits() throws IOException {
        List<QBit> list = new LinkedList<>();
        Gson gson = new Gson();

        String s = "";
        while ((s = aliceReader.readLine()) != null) {
            if (s.equals("END")) {
                break;
            }

            list.add(gson.fromJson(s, QBit.class));
        }

        return list;
    }

    public static List<Character> generateBasis(int amount) {
        List<Character> bobBases = new LinkedList<>();

        SecureRandom random = new SecureRandom();
        for (int i = 0; i < amount; i++) {
            char b = '+';

            // 50/50 chance, ob die Basis auf x geändert wird
            if (random.nextBoolean()) {
                b = 'x';
            }

            bobBases.add(b);
        }

        return bobBases;
    }
}
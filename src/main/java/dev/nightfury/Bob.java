package dev.nightfury;

import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.List;

public class Bob {
    private static Socket socket;
    private static BufferedReader reader;
    private static BufferedWriter writer;

    public static void main(String[] args) throws IOException {
        // Stelle verbindung mit Alice her
        socket = new Socket("localhost", 25565);

        // reader um Daten von Alice auszulesen und writer um Daten an Alice zu senden
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        // Bits von Alice
        List<QBit> bits = readBitsFromAlice();
        // Zufällig generierte Basen von Bob
        List<Character> bobBases = generateBasis(bits.size());

        // Bob vergleicht die Bits von Alice mit seiner Basis und erstellt den Schlüssel
        List<Integer> key = generateKey(bits, bobBases);

        // Verbindung wird geschlossen
        closeNetworking();
    }

    public static List<QBit> readBitsFromAlice() throws IOException {
        List<QBit> bits = new LinkedList<>();
        Gson gson = new Gson();

        String s = "";
        while ((s = reader.readLine()) != null) {
            bits.add(gson.fromJson(s, QBit.class));
        }

        return bits;
    }

    public static List<Character> generateBasis(int amount) {
        List<Character> bobBases = new LinkedList<>();

        SecureRandom random = new SecureRandom();
        for (int i = 0; i < amount; i++) {
            char b = '+';

            if (random.nextBoolean()) {
                b = 'x';
            }

            bobBases.add(b);
        }

        return bobBases;
    }

    public static List<Integer> generateKey(List<QBit> bits, List<Character> bobBases) {
        // TODO: Fertig machen
        return null;
    }

    public static void closeNetworking() throws IOException {
        reader.close();
        writer.close();
        socket.close();
    }
}
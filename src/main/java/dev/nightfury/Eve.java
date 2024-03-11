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

        // Eve vergleicht die Bits von Alice mit ihrer Basis
        List<Integer > comparedBits = compare(qBits, eveBases);

        sendQBitsToBob(qBits, eveBases);
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

    public static List<Integer> compare(List<QBit> bits, List<Character> bobBases) {
        List<Integer> bitList = new LinkedList<>();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < bits.size(); i++) {
            QBit qbit = bits.get(i);
            char base = bobBases.get(i);

            if (qbit.getBase() == base) { // Die Basis von Alice ist dieselbe wie die von Bob
                bitList.add(qbit.getBit());
            } else { // Die Basis von Alice ist anders als von Bob was dafür sorgt, dass Bobs messung zufällig ist
                bitList.add(random.nextInt(2));
            }
        }

        return bitList;
    }

    public static void sendQBitsToBob(List<QBit> qBits, List<Character> eveBases) throws IOException {
        Gson gson = new Gson();

        for (int i = 0; i < qBits.size(); i++) {
            QBit newQBit = new QBit(qBits.get(i).getBit(), eveBases.get(i));

            bobWriter.write(gson.toJson(newQBit));
            bobWriter.newLine();
        }

        bobWriter.write("END");
        bobWriter.newLine();
        bobWriter.flush();
    }
}
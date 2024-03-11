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
        serverSocket = new ServerSocket(25566);

        // Warte bis Bob bereit ist die Daten anzunehmen
        socket = serverSocket.accept();

        // reader um Daten von Bob auszulesen und writer um Daten an Bob zu senden
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        // Liste mit zufällig generierten QBits
        List<QBit> bits = generateQBits(20);

        // Sende die QBits zu Bob
        sendBitsToBob(bits);

        // Bob hat seine Basen an Alice gesendet
        List<Character> bobBases = getBobBases();

        // Alice vergleicht ihre Basen mit denen von Bob. In diesem Fall übergeben wir einfach die QBits, weil es einfacher zu programmieren ist
        // da sonst die Basen in eine neue Liste übergeben werden müssen und ich habe keinen Bock drauf weil zu viel Arbeit
        // Die Liste enthält die Positionen wo die Basen von Bob und Alice gleich sind
        List<Integer> matching = compareBases(bits, bobBases);

        // Alice sendet die Positionen von den gleichen Basen an Bob
        sendMatchesToBob(matching);

        // Bob und Alice entfernen die Bits an den jeweiligen Positionen
        List<Integer> keeped = keepMatchingBits(qbitsToBits(bits), matching);

        // Der Schlüssel wird ausgegeben
        System.out.println("Der Schlüssel lautet:");
        for (int i : keeped) {
            System.out.print(i);
        }

        // Verbindung wird geschlossen
        closeNetworking();
    }

    public static void sendBitsToBob(List<QBit> bits) throws IOException {
        Gson gson = new Gson();

        for (QBit bit : bits) {
            writer.write(gson.toJson(bit));
            writer.newLine();
        }

        // Sende Ende
        writer.write("END");
        writer.newLine();
        writer.flush();
    }

    public static List<QBit> generateQBits(int amount) {
        List<QBit> bits = new LinkedList<>();

        SecureRandom random = new SecureRandom();
        for (int i = 0; i < amount; i++) {
            char base = '+';

            // 50/50 chance, ob die Basis auf x geändert wird
            if (random.nextBoolean()) {
                base = 'x';
            }

            bits.add(new QBit(random.nextInt(2), base));
        }

        return bits;
    }

    public static List<Character> getBobBases() throws IOException {
        List<Character> bases = new LinkedList<>();

        String s = "";
        while ((s = reader.readLine()) != null) {
            if (s.equals("END")) {
                break;
            }

            bases.add(s.toCharArray()[0]);
        }

        return bases;
    }

    public static List<Integer> compareBases(List<QBit> aliceBits, List<Character> bobBases) {
        List<Integer> matching = new LinkedList<>();

        for (int i = 0; i < aliceBits.size(); i++) {
            if (aliceBits.get(i).getBase() == bobBases.get(i)) {
                matching.add(i);
            }
        }

        return matching;
    }

    public static void sendMatchesToBob(List<Integer> matches) throws IOException {
        for (int i : matches) {
            writer.write(String.valueOf(i));
            writer.newLine();
        }

        writer.write("END");
        writer.newLine();
        writer.flush();
    }

    public static List<Integer> qbitsToBits(List<QBit> qBits) {
        List<Integer> bits = new LinkedList<>();

        for (QBit qBit: qBits) {
            bits.add(qBit.getBit());
        }

        return bits;
    }

    public static List<Integer> keepMatchingBits(List<Integer> bits, List<Integer> matching) {
        List<Integer> keepedBits = new LinkedList<>();

        for (int i = 0; i < bits.size(); i++) {
            if (matching.contains(i)) {
                keepedBits.add(bits.get(i));
            }
        }

        return keepedBits;
    }

    public static void closeNetworking() throws IOException {
        reader.close();
        writer.close();
        socket.close();
        serverSocket.close();
    }
}
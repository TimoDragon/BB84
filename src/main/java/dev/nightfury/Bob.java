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
        List<QBit> qBits = readBitsFromAlice();
        // Zufällig generierte Basen von Bob
        List<Character> bobBases = generateBasis(qBits.size());

        // Bob vergleicht die Bits von Alice mit seiner Basis
        List<Integer > comparedBits = compare(qBits, bobBases);

        // Bob sendet seine Basen an Alice. Dies passiert über einen offenen Kanal
        sendBasesToAlice(bobBases);

        // Bob liest auf die Antwort von Alice, welche Basen gleich sind und welche nicht
        List<Integer> matching = readBaseMatches();

        // Bob und Alice entfernen die Bits an den jeweiligen Positionen
        List<Integer> keeped = keepMatchingBits(comparedBits, matching);

        // Der Schlüssel wird ausgegeben
        System.out.println("Der Schlüssel lautet:");
        for (int i : keeped) {
            System.out.print(i);
        }

        // Verbindung wird geschlossen
        closeNetworking();
    }

    public static List<QBit> readBitsFromAlice() throws IOException {
        List<QBit> bits = new LinkedList<>();
        Gson gson = new Gson();

        String s = "";
        while ((s = reader.readLine()) != null) {
            if (s.equals("END")) { // Ende von Alice
                break;
            }

            bits.add(gson.fromJson(s, QBit.class));
        }

        return bits;
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

    public static void sendBasesToAlice(List<Character> bases) throws IOException {
        for (char c : bases) {
            writer.write(String.valueOf(c));
            writer.newLine();
        }

        writer.write("END");
        writer.newLine();
        writer.flush();
    }

    public static List<Integer> readBaseMatches() throws IOException {
        List<Integer> matches = new LinkedList<>();

        String s = "";
        while ((s = reader.readLine()) != null) {
            if (s.equals("END")) {
                break;
            }

            matches.add(Integer.parseInt(s));
        }

        return matches;
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
    }
}
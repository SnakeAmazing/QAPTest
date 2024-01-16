package me.snakeamazing.qaptest.input;

import java.text.Normalizer;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

public class ManualInput implements Input {

    private final Map<String, Integer> frequencies;
    private final Scanner scanner;

    public ManualInput(Map<String, Integer> frequencies) {
        this.frequencies = frequencies;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void computeFrequencies() {
        System.out.println("Vols introduir les freqüències ja calculades, o que les calculem?");
        System.out.println("1. Introduir freqüències");
        System.out.println("2. Calcular freqüències");

        int option = scanner.nextInt();

        while (option != 1 && option != 2) {
            System.out.println("Opció incorrecta");
            option = scanner.nextInt();
        }

        if (option == 1) readFrequencies();
        else readText();

        System.out.println("Frequències calculades");
    }

    @Override
    public void storeFrequencies() {

    }

    private void readFrequencies() {
        System.out.println("Introdueix una paraula amb la seva frequencia separada per un espai");
        System.out.println("Per acabar, introdueix una línia buida");

        while (scanner.hasNext()) {
            String word = scanner.next();

            normalize(word);
            word = word.toUpperCase();

            int frequency = scanner.nextInt();

            frequencies.put(word, frequency);
        }
    }

    private void readText() {
        System.out.println("Introdueix el text");
        while (scanner.hasNext()) {
            String word = scanner.next();

            if (Objects.equals(word, "eof")) break;

            word = normalize(word);

            word = word.toUpperCase();

            if (frequencies.containsKey(word)) frequencies.put(word, frequencies.get(word) + 1);
            else frequencies.put(word, 1);
        }
    }

    private String normalize(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }
}

package me.snakeamazing.qaptest.input;

import java.util.Map;

public class FileInput implements Input {

    private final String path;
    private final Map<String, Integer> frequencies;

    public FileInput(String path, Map<String, Integer> frequencies) {
        this.path = path;
        this.frequencies = frequencies;
    }

    @Override
    public void computeFrequencies() {

    }

    @Override
    public void storeFrequencies() {

    }
}

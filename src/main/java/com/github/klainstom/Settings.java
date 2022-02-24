package com.github.klainstom;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.List;

public class Settings {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();
    private static final File SETTINGS_FILE = ExtensionMain.DATA_DIRECTORY.resolve("settings.json").toFile();

    private static SettingsState currentSettings = null;

    public static void read() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(SETTINGS_FILE));
            currentSettings = GSON.fromJson(reader, SettingsState.class);
        } catch (FileNotFoundException e) {
            currentSettings = new SettingsState();
            try {
                write();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void write() throws IOException {
        String json = GSON.toJson(currentSettings);
        Writer writer = new FileWriter(SETTINGS_FILE);
        writer.write(json);
        writer.close();
    }

    public static List<String> getSettingsLines() {
        return List.of(
                // lines to log
        );
    }

    private static class SettingsState {
        // Options available

        private SettingsState() {
            // Set default values for options
        }

    }

    // Getters for option values
}

package com.github.klainstom;

import net.minestom.server.MinecraftServer;
import net.minestom.server.extensions.Extension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class ExtensionMain extends Extension {
    public static Path DATA_DIRECTORY;

    @Override
    public void preInitialize() {
         DATA_DIRECTORY = Objects.requireNonNull(MinecraftServer.getExtensionManager()
                 .getExtension("$Name"), "Extension installed but not found!").getDataDirectory();

        if (!DATA_DIRECTORY.toFile().exists()) {
            try {
                Files.createDirectory(DATA_DIRECTORY);
            } catch (IOException e) {
                MinecraftServer.LOGGER.error("Could not create terminus data directory", e);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void initialize() {
        Settings.read();

        MinecraftServer.LOGGER.info("====== &NAME ======");
        Info.printVersionLines();
        Info.printSettingsLines();
        MinecraftServer.LOGGER.info("======================");
    }

    @Override
    public void terminate() {

    }
}

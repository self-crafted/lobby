package com.github.selfcrafted.lobby;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.event.entity.EntityDamageEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.extensions.Extension;
import net.minestom.server.instance.AnvilLoader;
import net.minestom.server.instance.Instance;
import net.minestom.server.tag.Tag;
import org.jglrxavpok.hephaistos.nbt.NBTCompound;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

public class ExtensionMain extends Extension {
    public static Path DATA_DIRECTORY;

    public static Instance LOBBY_INSTANCE;
    public static Pos LOBBY_SPAWN;

    @Override
    public void preInitialize() {
         DATA_DIRECTORY = Objects.requireNonNull(MinecraftServer.getExtensionManager()
                 .getExtension("&Name"), "Extension &Name installed but not found!").getDataDirectory();

        if (!DATA_DIRECTORY.toFile().exists()) {
            try {
                Files.createDirectory(DATA_DIRECTORY);
            } catch (IOException e) {
                MinecraftServer.LOGGER.error("Could not create &Name data directory", e);
                e.printStackTrace();
            }
        }

        LOBBY_INSTANCE = MinecraftServer.getInstanceManager()
                .createInstanceContainer(new AnvilLoader(ExtensionMain.DATA_DIRECTORY.resolve("anvil")));

        NBTCompound compound =
                Objects.requireNonNull(LOBBY_INSTANCE.getTag(Tag.NBT)).getCompound("Data");
        if (compound != null) {
            LOBBY_SPAWN = new Pos(compound.getAsDouble("SpawnX"),
                    compound.getAsDouble("SpawnY") + 1,
                    compound.getAsDouble("SpawnZ"));

            if ("false".equals(Objects.requireNonNull(compound.getCompound("GameRules"))
                    .getString("doDaylightCycle"))) {
                LOBBY_INSTANCE.setTimeRate(0);
                LOBBY_INSTANCE.setTime(-compound.getLong("Time"));
            } else LOBBY_INSTANCE.setTime(compound.getLong("Time"));
        } else {
            LOBBY_SPAWN = new Pos(0, 100, 0);
        }
    }

    @Override
    public void initialize() {
        Settings.read();

        MinecraftServer.LOGGER.info("====== &NAME ======");
        Info.printVersionLines();
        Info.printSettingsLines();
        MinecraftServer.LOGGER.info("======================");

        // TODO: 24.02.22 Add support for the automatic network configuration and server menu
        // ServerMenu.activateFor(LOBBY_INSTANCE);
        getEventNode().addListener(PlayerLoginEvent.class, event -> {
            event.setSpawningInstance(LOBBY_INSTANCE);
            event.getPlayer().setRespawnPoint(LOBBY_SPAWN);
            event.getPlayer().setGameMode(GameMode.ADVENTURE);
        });
        getEventNode().addListener(EntityDamageEvent.class, event -> {
            if (event.getEntity().getInstance() != LOBBY_INSTANCE) return;
            if (event.getDamageType() != DamageType.VOID) return;
            if (!(event.getEntity() instanceof Player player)) return;
            player.teleport(LOBBY_SPAWN);
            event.setCancelled(true);
        });
    }

    @Override
    public void terminate() {
        // TODO: 24.02.22 Add support for the automatic network configuration and server menu
        // ServerMenu.deactivateFor(LOBBY_INSTANCE);
    }
}

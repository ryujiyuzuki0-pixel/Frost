package dev.reforgedfrost;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class ReforgedFrostPlugin extends JavaPlugin implements Listener {

    private static final String MODEL_RESOURCE = "models/reforged_frost_armor.bbmodel";

    private PackHost packHost;
    private SetBonus setBonus;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        if (Bukkit.getPluginManager().getPlugin("MythicArmors") == null) {
            getLogger().warning("MythicArmors not found. Items can be given, but the 3D armor "
                    + "will not render until MythicArmors is installed.");
        } else if (getConfig().getBoolean("install-model", true)) {
            installModel();
        }

        PluginCommand cmd = getCommand("frost");
        if (cmd != null) {
            FrostCommand handler = new FrostCommand(this);
            cmd.setExecutor(handler);
            cmd.setTabCompleter(handler);
        }

        startPackHost();
        Bukkit.getPluginManager().registerEvents(this, this);

        setBonus = new SetBonus(this);
        Bukkit.getPluginManager().registerEvents(setBonus, this);
        setBonus.start();
    }

    @Override
    public void onDisable() {
        if (setBonus != null) setBonus.clearAll();
        if (packHost != null) {
            packHost.stop();
            packHost = null;
        }
    }

    /** Re-read config and restart the pack host. */
    public void reload() {
        reloadConfig();
        if (packHost != null) {
            packHost.stop();
            packHost = null;
        }
        startPackHost();
    }

    public PackHost packHost() {
        return packHost;
    }

    private void startPackHost() {
        if (!getConfig().getBoolean("pack.enabled", false)) return;
        File file = new File(getDataFolder().getParentFile(), getConfig().getString("pack.file", "MythicArmors/pack.zip"));
        packHost = new PackHost(this, file);
        try {
            packHost.start(getConfig().getInt("pack.port", 8123));
        } catch (IOException e) {
            getLogger().severe("Could not start pack host: " + e.getMessage());
            packHost = null;
        }
    }

    private void installModel() {
        File target = new File(getDataFolder().getParentFile(), "MythicArmors/models/reforged_frost_armor.bbmodel");
        try (InputStream in = getResource(MODEL_RESOURCE)) {
            if (in == null) {
                getLogger().severe("Bundled model missing from jar.");
                return;
            }
            byte[] bundled = in.readAllBytes();
            if (target.isFile() && Arrays.equals(Files.readAllBytes(target.toPath()), bundled)) return;

            Files.createDirectories(target.getParentFile().toPath());
            Files.write(target.toPath(), bundled);
            getLogger().info("Installed reforged_frost_armor.bbmodel into MythicArmors/models.");

            // Wait until the server finished loading, then rebuild the pack.
            Bukkit.getScheduler().runTask(this, () ->
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "ma reload"));
        } catch (IOException e) {
            getLogger().severe("Failed to install model: " + e.getMessage());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (packHost != null) {
            // small delay so the client is fully in the world
            Bukkit.getScheduler().runTaskLater(this, () -> packHost.send(event.getPlayer()), 20L);
        }
    }
}

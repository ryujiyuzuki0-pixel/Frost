package dev.reforgedfrost;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.bukkit.entity.Player;

import com.sun.net.httpserver.HttpServer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

/** Tiny HTTP server that serves the MythicArmors-generated pack.zip. */
public final class PackHost {

    private final ReforgedFrostPlugin plugin;
    private final File file;
    private HttpServer server;

    private byte[] cachedHash;
    private long cachedModified = -1;

    public PackHost(ReforgedFrostPlugin plugin, File file) {
        this.plugin = plugin;
        this.file = file;
    }

    public void start(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/pack.zip", exchange -> {
            try {
                if (!file.isFile()) {
                    exchange.sendResponseHeaders(404, -1);
                    return;
                }
                byte[] data = Files.readAllBytes(file.toPath());
                exchange.getResponseHeaders().add("Content-Type", "application/zip");
                exchange.sendResponseHeaders(200, data.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(data);
                }
            } finally {
                exchange.close();
            }
        });
        server.setExecutor(java.util.concurrent.Executors.newSingleThreadExecutor());
        server.start();
        plugin.getLogger().info("Pack host listening on port " + port);
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            server = null;
        }
    }

    public void send(Player player) {
        if (!file.isFile()) {
            plugin.getLogger().warning("pack.zip not found at " + file.getPath()
                    + " (is MythicArmors installed and reloaded?)");
            return;
        }
        byte[] hash = hash();
        if (hash == null) return;

        String url = plugin.getConfig().getString("pack.public-url", "");
        boolean required = plugin.getConfig().getBoolean("pack.required", false);
        Component prompt = MiniMessage.miniMessage()
                .deserialize(plugin.getConfig().getString("pack.prompt", "Reforged Frost resource pack"));
        player.setResourcePack(url, hash, prompt, required);
    }

    private synchronized byte[] hash() {
        long modified = file.lastModified();
        if (cachedHash != null && modified == cachedModified) return cachedHash;
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            cachedHash = sha1.digest(Files.readAllBytes(file.toPath()));
            cachedModified = modified;
            return cachedHash;
        } catch (NoSuchAlgorithmException | IOException e) {
            plugin.getLogger().severe("Could not hash pack: " + e.getMessage());
            return null;
        }
    }
}

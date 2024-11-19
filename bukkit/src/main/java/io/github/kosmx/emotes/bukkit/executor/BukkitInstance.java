package io.github.kosmx.emotes.bukkit.executor;

import io.github.kosmx.emotes.bukkit.BukkitWrapper;
import io.github.kosmx.emotes.executor.EmoteInstance;
import io.github.kosmx.emotes.executor.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

public class BukkitInstance extends EmoteInstance implements Logger {
    protected final BukkitWrapper plugin;

    public BukkitInstance(BukkitWrapper plugin){
        this.plugin = plugin;
    }

    @Override
    public Logger getLogger() {
        return this;
    }

    @Override
    public void writeLog(Level level, String msg, Throwable throwable) {
        this.plugin.getLogger().log(level, msg, throwable);
    }

    @Override
    public void writeLog(Level level, String msg) {
        this.plugin.getLogger().log(level, msg);
    }

    @Override
    public boolean isClient() {
        return false;
    }

    @Override
    public Path getGameDirectory() {
        return Paths.get("");
    }
}

package io.github.kosmx.emotes.velocity.executor;

import io.github.kosmx.emotes.executor.EmoteInstance;
import io.github.kosmx.emotes.executor.Logger;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;

public class VelocityInstance extends EmoteInstance implements Logger {
    protected final java.util.logging.Logger logger;

    public VelocityInstance(java.util.logging.Logger logger) {
        this.logger = logger;
    }

    @Override
    public Logger getLogger() {
        return this;
    }

    @Override
    public void writeLog(Level level, String msg, Throwable throwable) {
        this.logger.log(level, msg, throwable);
    }

    @Override
    public void writeLog(Level level, String msg) {
        this.logger.log(level, msg);
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

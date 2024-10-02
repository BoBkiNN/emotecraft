package io.github.kosmx.emotes.server.serializer.type;

import java.io.PrintStream;

public class EmoteSerializerException extends RuntimeException {
    final String type;
    final Exception internalException;

    public EmoteSerializerException(String msg, String type) {
        this(msg, type, null);
    }

    public EmoteSerializerException(String msg, String type, Exception exception) {
        super(msg, exception);
        this.type = type;
        this.internalException = exception;
    }

    public String getType() {
        return type;
    }

    public Exception getInternalException() {
        return internalException;
    }

    @Override
    public void printStackTrace(PrintStream s) {
        super.printStackTrace(s);
    }
}

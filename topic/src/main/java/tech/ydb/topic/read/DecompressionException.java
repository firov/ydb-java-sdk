package tech.ydb.topic.read;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * @author Nikolay Perfilov
 */
public class DecompressionException extends UncheckedIOException {
    private final byte[] rawData;
    public DecompressionException(String message, IOException cause, byte[] rawData) {
        super(message, cause);
        this.rawData = rawData;
    }

    /**
     * @return Raw message byte data that failed be decompressed
     */
    public byte[] getRawData() {
        return rawData;
    }
}
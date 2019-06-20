package tech.ydb.table.impl.pool;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;


/**
 * @author Sergey Polovko
 */
public interface AsyncPool<T> {

    int getAcquiredCount();

    /**
     * Zero timeout will be treated as "return object immediately or fail".
     */
    CompletableFuture<T> acquire(Duration timeout);

    void release(T object);

    void close();
}
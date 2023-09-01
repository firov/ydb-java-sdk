package tech.ydb.topic.read.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tech.ydb.proto.topic.YdbTopic;
import tech.ydb.topic.TopicRpc;
import tech.ydb.topic.read.Message;
import tech.ydb.topic.read.PartitionSession;
import tech.ydb.topic.read.SyncReader;
import tech.ydb.topic.read.events.DataReceivedEvent;
import tech.ydb.topic.settings.ReaderSettings;

/**
 * @author Nikolay Perfilov
 */
public class SyncReaderImpl extends ReaderImpl implements SyncReader {
    private static final Logger logger = LoggerFactory.getLogger(SyncReaderImpl.class);
    private static final int POLL_INTERVAL_SECONDS = 5;
    private final Queue<MessageBatchWrapper> batchesInQueue = new LinkedList<>();
    private MessageBatchWrapper currentMessageBatch = null;
    private int currentMessageIndex = 0;

    public SyncReaderImpl(TopicRpc topicRpc, ReaderSettings settings) {
        super(topicRpc, settings);
    }

    private static class MessageBatchWrapper {
        private final List<Message> messages;
        private final CompletableFuture<Void> future;

        private MessageBatchWrapper(List<Message> messages, CompletableFuture<Void> future) {
            this.messages = messages;
            this.future = future;
        }
    }

    @Override
    public void init() {
        initImpl();
    }

    @Override
    public void initAndWait() {
        initImpl().join();
    }

    @Override
    @Nullable
    public Message receive(long timeout, TimeUnit unit) throws InterruptedException {
        if (isStopped.get()) {
            throw new RuntimeException("Reader was stopped");
        }
        synchronized (batchesInQueue) {
            if (currentMessageBatch == null) {
                if (batchesInQueue.isEmpty()) {
                    logger.info("No messages in queue. Waiting for {} ms...",
                            TimeUnit.MILLISECONDS.convert(timeout, unit));
                    batchesInQueue.wait(TimeUnit.MILLISECONDS.convert(timeout, unit));
                    if (currentMessageBatch == null) {
                        logger.info("Still no messages to read. Returning null");
                        return null;
                    }
                } else {
                    logger.info("Taking next batch from queue");
                    currentMessageBatch = batchesInQueue.poll();
                    currentMessageIndex = 0;
                }
            }
            logger.info("Taking a message with index {} from batch", currentMessageIndex);
            Message result = currentMessageBatch.messages.get(currentMessageIndex);
            currentMessageIndex++;
            if (currentMessageIndex >= currentMessageBatch.messages.size()) {
                logger.info("Batch is read. signalling core reader impl");
                currentMessageBatch.future.complete(null);
                currentMessageBatch = null;
            }
            return result;
        }
    }

    @Override
    public Message receive() throws InterruptedException {
        Message result;
        // Poll to prevent infinite wait in case if reader was stopped
        do {
            result = receive(POLL_INTERVAL_SECONDS, TimeUnit.SECONDS);
        } while (result == null);
        return result;
    }

    @Override
    protected CompletableFuture<Void> handleDataReceivedEvent(DataReceivedEvent event) {
        logger.info("handleDataReceivedEvent called");
        // Completes when all messages from this event are read by user
        final CompletableFuture<Void> resultFuture = new CompletableFuture<>();

        if (isStopped.get()) {
            resultFuture.completeExceptionally(new RuntimeException("Reader was stopped"));
            return resultFuture;
        }

        MessageBatchWrapper newBatch = new MessageBatchWrapper(event.getMessages(), resultFuture);

        synchronized (batchesInQueue) {
            if (currentMessageBatch == null) {
                currentMessageBatch = newBatch;
                currentMessageIndex = 0;
                logger.info("Putting a message and notifying in case receive method is waiting");
                batchesInQueue.notify();
            } else {
                logger.info("Just putting a message and notifying in case receive method is waiting");
                batchesInQueue.add(newBatch);
            }
        }
        return resultFuture;
    }

    @Override
    protected void handleStartPartitionSessionRequest(YdbTopic.StreamReadMessage.StartPartitionSessionRequest request) {
        sendStartPartitionSessionResponse(request, null);
    }

    @Override
    protected void handleStopPartitionSession(YdbTopic.StreamReadMessage.StopPartitionSessionRequest request,
                                              @Nullable Long partitionId) {
        sendStopPartitionSessionResponse(request.getPartitionSessionId());
    }

    @Override
    protected void handleClosePartitionSession(PartitionSession partitionSession) {
        logger.debug("ClosePartitionSession event received. Ignoring.");
    }

    @Override
    protected void handleCloseReader() {
        logger.debug("CloseReader event received. Ignoring.");
    }

    @Override
    protected CompletableFuture<Void> shutdownImpl() {
        return super.shutdownImpl();
    }

    @Override
    public void shutdown() {
        shutdownImpl().join();
    }
}

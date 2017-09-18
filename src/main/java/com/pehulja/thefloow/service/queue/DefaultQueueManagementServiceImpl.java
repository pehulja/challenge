package com.pehulja.thefloow.service.queue;

import com.pehulja.thefloow.service.queue.statistics.QueueStatisticsService;
import com.pehulja.thefloow.storage.documents.QueueItem;
import com.pehulja.thefloow.storage.repository.CustomQueueItemRepository;
import com.pehulja.thefloow.storage.repository.QueueItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * Created by eyevpek on 2017-09-11.
 */
@Slf4j
@Service
public class DefaultQueueManagementServiceImpl implements QueueManagementService, InitializingBean, DisposableBean, Supplier<Runnable> {
    @Autowired
    private QueueItemRepository queueItemRepository;

    @Value("${mongo.insert-thread-number}")
    private Integer pushThreadNumber;

    @Autowired
    private QueueStatisticsService queueStatisticsService;

    @Autowired
    private CustomQueueItemRepository customQueueItemRepository;

    @Value("${queue.listener.delay}")
    private Integer fixedDelay;

    @Value("${queue.listener.threads}")
    private Integer pollThreadNumber;

    @Value("${queue.listener.enabled}")
    private boolean isListenerPoolingEnabled;

    // Thread pool that push items to the Mongo in background. It is redundant for new version of Mongo driver and Spring Data with Async support
    // but I still use old driver instead of "reactive approach"
    private ExecutorService pushExecutorService;

    // Polling queue thread pool
    private ScheduledExecutorService pollingExecutorService;

    // Set of callbacks executors for every polled queue item
    private Set<Consumer<QueueItem>> subscribers;

    @Override
    public Future<QueueItem> push(QueueItem queueItem) {
        return pushExecutorService.submit(() -> queueItemRepository.insert(queueItem));
    }

    @Override
    public void registerSubscriber(Consumer<QueueItem> subscriber) {
        subscribers.add(subscriber);
    }

    /**
     * Gets a result.
     *
     * @return a result
     */
    @Override
    public Runnable get() {
        return new PollingTask();
    }

    @Override
    public void destroy() throws Exception {
        pushExecutorService.shutdown();
        if (!pushExecutorService.awaitTermination(1, TimeUnit.MINUTES)) {
            pushExecutorService.shutdownNow();
        }

        if (isListenerPoolingEnabled && pollingExecutorService != null) {
            pollingExecutorService.shutdown();

            if (!pollingExecutorService.awaitTermination(1, TimeUnit.MINUTES)) {
                pollingExecutorService.shutdownNow();
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        subscribers = new HashSet<>();
        //TODO: Check usage of blocking queue to avoid OutOfMemory
        pushExecutorService = Executors.newFixedThreadPool(pushThreadNumber);

        if (isListenerPoolingEnabled) {
            pollingExecutorService = Executors.newScheduledThreadPool(pollThreadNumber);
            Stream.generate(this).limit(pollThreadNumber).forEach(queueListenerTask -> pollingExecutorService.scheduleWithFixedDelay(queueListenerTask, fixedDelay, fixedDelay, TimeUnit.MILLISECONDS));
        }
    }

    class PollingTask implements Runnable {

        /**
         * Poll the queue and execute callbacks
         */
        @Override
        public void run() {
            Optional<QueueItem> optionalQueueItem = customQueueItemRepository.poll();
            optionalQueueItem.ifPresent(queueItem -> subscribers.parallelStream().forEach(queueItemConsumer -> queueItemConsumer.accept(queueItem)));
        }
    }
}

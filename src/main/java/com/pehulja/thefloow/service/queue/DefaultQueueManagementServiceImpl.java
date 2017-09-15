package com.pehulja.thefloow.service.queue;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.pehulja.thefloow.service.queue.statistics.QueueStatisticsService;
import com.pehulja.thefloow.storage.documents.QueueItem;
import com.pehulja.thefloow.storage.repository.QueueItemRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by eyevpek on 2017-09-11.
 */
@Slf4j
@Service
public class DefaultQueueManagementServiceImpl implements QueueManagementService, InitializingBean, DisposableBean, Supplier<Runnable>
{
    @Autowired
    private QueueItemRepository queueItemRepository;

    @Value ("${mongo.insert-thread-number}")
    private Integer pushThreadNumber;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private QueueStatisticsService queueStatisticsService;

    @Value ("${queue.listener.delay}")
    private Integer fixedDelay;

    @Value ("${queue.listener.threads}")
    private Integer pollThreadNumber;

    @Value ("${queue.listener.enabled}")
    private boolean isListenerPoolingEnabled;

    private ExecutorService pushExecutorService;
    private ScheduledExecutorService pollingExecutorService;

    private Set<Consumer<QueueItem>> subscribers;

    @Override
    public Future<QueueItem> push(QueueItem queueItem)
    {
        return pushExecutorService.submit(() -> queueItemRepository.insert(queueItem));
    }

    @Override
    public void registerSubscriber(Consumer<QueueItem> subscriber)
    {
        subscribers.add(subscriber);
    }

    /**
     * Gets a result.
     *
     * @return a result
     */
    @Override
    public Runnable get()
    {
        return new PollingTask();
    }

    protected Optional<QueueItem> poll()
    {
        Query query = new Query();
        query.limit(1);

        return Optional.ofNullable(mongoTemplate.findAndRemove(query, QueueItem.class));
    }

    @Override
    public void destroy() throws Exception
    {
        pushExecutorService.shutdown();

        if (isListenerPoolingEnabled && pollingExecutorService != null && !pollingExecutorService.isShutdown())
        {
            pollingExecutorService.shutdown();
            pollingExecutorService.awaitTermination(10, TimeUnit.SECONDS);
        }

        pushExecutorService.awaitTermination(10, TimeUnit.SECONDS);
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        subscribers = new HashSet<>();
        //TODO: Check usage of blocking queue to avoid OutOfMemory
        pushExecutorService = Executors.newFixedThreadPool(pushThreadNumber);

        if (isListenerPoolingEnabled)
        {
            pollingExecutorService = Executors.newScheduledThreadPool(pollThreadNumber);
            Stream.generate(this).limit(pollThreadNumber).forEach(queueListenerTask -> pollingExecutorService.scheduleWithFixedDelay(queueListenerTask, fixedDelay, fixedDelay, TimeUnit.MILLISECONDS));
        }
    }

    class PollingTask implements Runnable
    {

        /**
         * Computes a result, or throws an exception if unable to do so.
         *
         * @return computed result
         * @throws Exception
         *         if unable to compute a result
         */
        @Override
        public void run()
        {
            Optional<QueueItem> optionalQueueItem = poll();
            //log.info(String.format("Thread %d Polled %s", Thread.currentThread().getId(), optionalQueueItem));
            optionalQueueItem.ifPresent(queueItem -> subscribers.parallelStream().forEach(queueItemConsumer -> queueItemConsumer.accept(queueItem)));
        }
    }
}

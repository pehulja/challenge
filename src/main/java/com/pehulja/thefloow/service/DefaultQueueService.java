package com.pehulja.thefloow.service;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.pehulja.thefloow.queue.QueueItem;
import com.pehulja.thefloow.repository.QueueItemRepository;

/**
 * Created by eyevpek on 2017-09-11.
 */
@Service
public class DefaultQueueService implements QueueService, InitializingBean, DisposableBean
{
    @Autowired
    private QueueItemRepository queueItemRepository;

    @Value ("${mongo.insert-thread-number}")
    private Integer threadNumber;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private QueueStatisticsService queueStatisticsService;

    private ExecutorService executorService;

    @Override
    public Future<QueueItem> push(QueueItem queueItem)
    {
        return executorService.submit(() -> queueItemRepository.insert(queueItem));
    }

    @Override
    public Optional<QueueItem> poll()
    {
        Query query = new Query();
        query.limit(1);

        return Optional.ofNullable(mongoTemplate.findAndRemove(query, QueueItem.class));
    }

    @Override
    public void destroy() throws Exception
    {
        executorService.shutdown();
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        //TODO: Check usage of blocking queue to avoid OutOfMemory
        executorService = Executors.newFixedThreadPool(threadNumber);
    }
}

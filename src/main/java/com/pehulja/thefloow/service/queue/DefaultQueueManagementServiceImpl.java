package com.pehulja.thefloow.service.queue;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

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

/**
 * Created by eyevpek on 2017-09-11.
 */
@Service
public class DefaultQueueManagementServiceImpl implements QueueManagementService, InitializingBean, DisposableBean
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

        executorService.awaitTermination(10, TimeUnit.SECONDS);
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        //TODO: Check usage of blocking queue to avoid OutOfMemory
        executorService = Executors.newFixedThreadPool(threadNumber);
    }
}

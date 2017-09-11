package com.pehulja.thefloow.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
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

    private ExecutorService executorService;

    @Override
    public Future<QueueItem> push(QueueItem queueItem)
    {
        return executorService.submit(() -> queueItemRepository.insert(queueItem));
    }

    @Override
    public Optional<QueueItem> poll()
    {
        PageRequest pageRequest = new PageRequest(0, 1);
        Query query = new Query();
        query.limit(1);

        List<QueueItem> list = mongoTemplate.findAllAndRemove(query, QueueItem.class);

        if(list == null || list.isEmpty()){
            return Optional.empty();
        } else {
            return Optional.of(list.get(0));
        }
    }

    @Override
    public void destroy() throws Exception
    {
        executorService.shutdown();

        executorService.awaitTermination(10, TimeUnit.MINUTES);
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        //TODO: Check usage of blocking queue to avoid OutOfMemory
        executorService = Executors.newFixedThreadPool(threadNumber);
    }
}

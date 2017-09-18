package com.pehulja.thefloow.storage.repository;

import com.pehulja.thefloow.storage.documents.QueueItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created by baske on 17.09.2017.
 */
@Repository
public class CustomQueueItemRepositoryImpl implements CustomQueueItemRepository {
    @Autowired
    private QueueItemRepository queueItemRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    public Optional<QueueItem> poll() {
        Query query = new Query();
        query.limit(1);

        return Optional.ofNullable(mongoTemplate.findAndRemove(query, QueueItem.class));
    }
}

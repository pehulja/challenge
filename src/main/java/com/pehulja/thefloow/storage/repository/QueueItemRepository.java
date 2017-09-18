package com.pehulja.thefloow.storage.repository;

import com.pehulja.thefloow.storage.documents.QueueItem;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by eyevpek on 2017-09-11.
 */
public interface QueueItemRepository extends MongoRepository<QueueItem, String> {
}

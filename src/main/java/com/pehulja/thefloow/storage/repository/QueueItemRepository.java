package com.pehulja.thefloow.storage.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.pehulja.thefloow.storage.documents.QueueItem;

/**
 * Created by eyevpek on 2017-09-11.
 */
public interface QueueItemRepository extends MongoRepository<QueueItem, String>
{
}

package com.pehulja.thefloow.storage.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.pehulja.thefloow.storage.documents.QueueStatistics;

/**
 * Created by eyevpek on 2017-09-12.
 */
public interface QueueStatisticsRepository extends MongoRepository<QueueStatistics, String>
{
}

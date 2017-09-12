package com.pehulja.thefloow.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.pehulja.thefloow.queue.QueueStatistics;

/**
 * Created by eyevpek on 2017-09-12.
 */
public interface QueueStatisticsRepository extends MongoRepository<QueueStatistics, String>
{
}

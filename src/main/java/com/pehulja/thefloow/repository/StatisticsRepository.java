package com.pehulja.thefloow.repository;

import com.pehulja.thefloow.queue.QueueItem;
import com.pehulja.thefloow.statistics.Statistics;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

/**
 * Created by baske on 11.09.2017.
 */

public interface StatisticsRepository extends MongoRepository<Statistics, String> {
}

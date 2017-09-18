package com.pehulja.thefloow.storage.repository;

import com.pehulja.thefloow.storage.documents.QueueStatistics;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by eyevpek on 2017-09-12.
 */
public interface QueueStatisticsRepository extends MongoRepository<QueueStatistics, String> {
}

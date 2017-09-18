package com.pehulja.thefloow.service.queue.statistics;

import com.pehulja.thefloow.storage.documents.QueueStatistics;

/**
 * Created by eyevpek on 2017-09-12.
 */
public interface QueueStatisticsService {
    QueueStatistics incrementQueue();

    QueueStatistics incrementSuccessfullyProcessed();

    QueueStatistics incrementFailedToProcess();

    QueueStatistics getQueueStatistics();
}

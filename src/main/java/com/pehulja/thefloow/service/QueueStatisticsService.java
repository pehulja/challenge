package com.pehulja.thefloow.service;

import com.pehulja.thefloow.queue.QueueStatistics;

/**
 * Created by eyevpek on 2017-09-12.
 */
public interface QueueStatisticsService
{
    QueueStatistics incrementQueue();

    QueueStatistics incrementSuccessfullyProcessed();

    QueueStatistics incrementFailedToProcess();

    QueueStatistics getQueueStatistics();
}

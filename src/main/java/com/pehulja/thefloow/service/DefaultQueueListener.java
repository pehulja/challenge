package com.pehulja.thefloow.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pehulja.thefloow.UnableUpdateDocumentException;
import com.pehulja.thefloow.queue.QueueItem;
import com.pehulja.thefloow.statistics.Statistics;

/**
 * Created by eyevpek on 2017-09-12.
 */
@Component
public class DefaultQueueListener implements QueueListener
{
    @Autowired
    private QueueService queueService;

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private QueueStatisticsService queueStatisticsService;

    @Scheduled (fixedDelayString = "${queue.listener.delay}")
    public Optional<Statistics> processQueueItem()
    {
        Optional<QueueItem> optionalQueueItem = queueService.poll();

        return optionalQueueItem.map(queueItem ->
        {
            try
            {
                Statistics statistics = statisticsService.processQueueItem(queueItem);

                queueStatisticsService.incrementSuccessfullyProcessed();

                return statistics;
            }
            catch (UnableUpdateDocumentException ex)
            {
                queueStatisticsService.incrementFailedToProcess();

                throw new RuntimeException(String.format("Unable to process queue item: %s", queueItem), ex);
            }
        });
    }
}

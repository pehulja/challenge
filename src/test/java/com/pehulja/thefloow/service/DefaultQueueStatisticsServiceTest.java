package com.pehulja.thefloow.service;

import static com.pehulja.thefloow.service.DefaultQueueStatisticsService.QUEUE_STATISTICS_DOC_ID;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pehulja.thefloow.AbstractTestWithMongo;
import com.pehulja.thefloow.queue.QueueStatistics;
import com.pehulja.thefloow.repository.QueueStatisticsRepository;

/**
 * Created by eyevpek on 2017-09-12.
 */
@RunWith (SpringRunner.class)
@SpringBootTest
public class DefaultQueueStatisticsServiceTest extends AbstractTestWithMongo
{
    @Autowired
    private DefaultQueueStatisticsService defaultQueueStatisticsService;

    @Autowired
    private QueueStatisticsRepository queueStatisticsRepository;

    @Test
    public void incrementQueue() throws Exception
    {
        QueueStatistics expected = QueueStatistics.builder().id(QUEUE_STATISTICS_DOC_ID).pushedToQueue(1l).build();
        QueueStatistics actual = defaultQueueStatisticsService.incrementQueue();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void incrementSuccessfullyProcessed() throws Exception
    {
        defaultQueueStatisticsService.incrementQueue();
        QueueStatistics expected = QueueStatistics.builder().id(QUEUE_STATISTICS_DOC_ID).pushedToQueue(1l).successfullyProcessed(1l).build();
        QueueStatistics actual = defaultQueueStatisticsService.incrementSuccessfullyProcessed();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void incrementFailedToProcess() throws Exception
    {
        defaultQueueStatisticsService.incrementQueue();
        QueueStatistics expected = QueueStatistics.builder().id(QUEUE_STATISTICS_DOC_ID).pushedToQueue(1l).failedToProcess(1l).build();
        QueueStatistics actual = defaultQueueStatisticsService.incrementFailedToProcess();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void getQueueStatistics() throws Exception
    {
        defaultQueueStatisticsService.incrementQueue();
        defaultQueueStatisticsService.incrementQueue();
        QueueStatistics expected = QueueStatistics.builder().id(QUEUE_STATISTICS_DOC_ID).pushedToQueue(2l).successfullyProcessed(1l).possibleLostOrInProgress(1l).build();
        defaultQueueStatisticsService.incrementSuccessfullyProcessed();
        QueueStatistics actual = defaultQueueStatisticsService.getQueueStatistics();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

}
package com.pehulja.thefloow.service.queue.statistics;

import com.pehulja.thefloow.AbstractTestWithMongo;
import com.pehulja.thefloow.storage.documents.QueueStatistics;
import com.pehulja.thefloow.storage.repository.QueueStatisticsRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.pehulja.thefloow.service.queue.statistics.DefaultQueueStatisticsServiceImpl.QUEUE_STATISTICS_DOC_ID;

/**
 * Created by eyevpek on 2017-09-12.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DefaultQueueStatisticsServiceImplTest extends AbstractTestWithMongo {
    @Autowired
    private DefaultQueueStatisticsServiceImpl defaultQueueStatisticsServiceImpl;

    @Autowired
    private QueueStatisticsRepository queueStatisticsRepository;

    @Test
    public void incrementQueue() throws Exception {
        QueueStatistics expected = QueueStatistics.builder().id(QUEUE_STATISTICS_DOC_ID).pushedToQueue(1l).build();
        QueueStatistics actual = defaultQueueStatisticsServiceImpl.incrementQueue();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void incrementSuccessfullyProcessed() throws Exception {
        defaultQueueStatisticsServiceImpl.incrementQueue();
        QueueStatistics expected = QueueStatistics.builder().id(QUEUE_STATISTICS_DOC_ID).pushedToQueue(1l).successfullyProcessed(1l).build();
        QueueStatistics actual = defaultQueueStatisticsServiceImpl.incrementSuccessfullyProcessed();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void incrementFailedToProcess() throws Exception {
        defaultQueueStatisticsServiceImpl.incrementQueue();
        QueueStatistics expected = QueueStatistics.builder().id(QUEUE_STATISTICS_DOC_ID).pushedToQueue(1l).failedToProcess(1l).build();
        QueueStatistics actual = defaultQueueStatisticsServiceImpl.incrementFailedToProcess();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void getQueueStatistics() throws Exception {
        defaultQueueStatisticsServiceImpl.incrementQueue();
        defaultQueueStatisticsServiceImpl.incrementQueue();
        QueueStatistics expected = QueueStatistics.builder().id(QUEUE_STATISTICS_DOC_ID).pushedToQueue(2l).successfullyProcessed(1l).possibleLostOrInProgress(1l).build();
        defaultQueueStatisticsServiceImpl.incrementSuccessfullyProcessed();
        QueueStatistics actual = defaultQueueStatisticsServiceImpl.getQueueStatistics();

        Assertions.assertThat(actual).isEqualTo(expected);
    }

}
package com.pehulja.thefloow.service;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pehulja.thefloow.AbstractTestWithMongo;
import com.pehulja.thefloow.filereader.FileChunk;
import com.pehulja.thefloow.queue.QueueItem;
import com.pehulja.thefloow.repository.QueueItemRepository;
import com.pehulja.thefloow.repository.StatisticsRepository;
import com.pehulja.thefloow.statistics.Statistics;

/**
 * Created by eyevpek on 2017-09-12.
 */
@RunWith (SpringRunner.class)
@SpringBootTest (properties = {"queue.listener.enabled=true"})
public class DefaultQueueListenerTest extends AbstractTestWithMongo
{
    @Autowired
    private QueueItemRepository queueItemRepository;

    @Autowired
    private StatisticsRepository statisticsRepository;

    private final String FILE_ID = "fileId";
    private final String FILE_NAME = "fileName";

    @Before
    public void fillQueue()
    {

        String textToParse = "hello>hi. hello\nhi hello-hi hello_hi hi\n";

        for (long i = 0; i < 2; i++)
        {
            queueItemRepository.insert(QueueItem.builder()
                    .fileChunk(FileChunk.builder()
                            .content(textToParse)
                            .chunkId(i)
                            .fileId(FILE_ID)
                            .fileName(FILE_NAME)
                            .build())
                    .build());
        }

    }

    @Test
    public void processQueueItem() throws Exception
    {
        Statistics expected = Statistics.builder()
                .fileId(FILE_ID)
                .fileName(FILE_NAME)
                .statistic("hello", 6l)
                .statistic("hi", 8l)
                .statistic("hello_hi", 2l)
                .version(2l)
                .build();

        Thread.sleep(10 * 1000);

        Statistics actual = statisticsRepository.findOne(FILE_ID);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

}
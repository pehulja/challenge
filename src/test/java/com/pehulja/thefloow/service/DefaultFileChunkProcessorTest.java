package com.pehulja.thefloow.service;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pehulja.thefloow.AbstractTestWithMongo;
import com.pehulja.thefloow.storage.documents.FileChunk;
import com.pehulja.thefloow.storage.documents.FileWordsStatistics;
import com.pehulja.thefloow.storage.documents.QueueItem;
import com.pehulja.thefloow.storage.repository.FileWordsStatisticsRepository;
import com.pehulja.thefloow.storage.repository.QueueItemRepository;

/**
 * Created by eyevpek on 2017-09-12.
 */
@RunWith (SpringRunner.class)
@SpringBootTest (properties = {"queue.listener.enabled=true"})
public class DefaultFileChunkProcessorTest extends AbstractTestWithMongo
{
    @Autowired
    private QueueItemRepository queueItemRepository;

    @Autowired
    private FileWordsStatisticsRepository fileWordsStatisticsRepository;

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
        FileWordsStatistics expected = FileWordsStatistics.builder()
                .fileId(FILE_ID)
                .fileName(FILE_NAME)
                .wordStatistic("hello", 6l)
                .wordStatistic("hi", 8l)
                .wordStatistic("hello_hi", 2l)
                .build();

        Thread.sleep(10 * 1000);

        FileWordsStatistics actual = fileWordsStatisticsRepository.findOne(FILE_ID);

        Assertions.assertThat(actual).isEqualTo(expected);
    }

}
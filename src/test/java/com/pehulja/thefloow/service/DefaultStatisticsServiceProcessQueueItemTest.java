package com.pehulja.thefloow.service;

import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pehulja.thefloow.UnableUpdateDocumentException;
import com.pehulja.thefloow.filereader.FileChunk;
import com.pehulja.thefloow.queue.QueueItem;
import com.pehulja.thefloow.repository.StatisticsRepository;
import com.pehulja.thefloow.statistics.Statistics;

/**
 * Created by baske on 12.09.2017.
 */
@RunWith (SpringRunner.class)
@SpringBootTest
public class DefaultStatisticsServiceProcessQueueItemTest
{
    @Mock
    private StatisticsRepository statisticsRepository;

    @InjectMocks
    @Spy
    @Autowired
    private DefaultStatisticsService defaultStatisticsService;

    @Captor
    private ArgumentCaptor<Statistics> argumentCaptor;

    @Before
    public void setupMocks()
    {
    }

    @Test
    public void processQueueItem() throws UnableUpdateDocumentException
    {
        String textToParse = "hello>hi. hello\nhi hello-hi hello_hi hi\n";
        String fileId = "fileId";
        String fileName = "fileName";

        Statistics expected = Statistics.builder()
                .fileId(fileId)
                .fileName(fileName)
                .statistic("hello", 3l)
                .statistic("hi", 4l)
                .statistic("hello_hi", 1l)
                .build();

        QueueItem queueItem = QueueItem.builder()
                .id(UUID.randomUUID().toString())
                .fileChunk(
                        FileChunk.builder()
                                .fileName(fileName)
                                .fileId(fileId)
                                .chunkId(1l)
                                .content(textToParse)
                                .build())
                .build();

        Mockito.doAnswer(i -> i.getArguments()[0]).when(defaultStatisticsService).optimisticUpdate(argumentCaptor.capture(), Mockito.any());
        Statistics actual = defaultStatisticsService.processQueueItem(queueItem);

        Assertions.assertThat(actual).isEqualTo(expected);
        Assertions.assertThat(argumentCaptor.getValue()).isEqualTo(expected);
    }
}
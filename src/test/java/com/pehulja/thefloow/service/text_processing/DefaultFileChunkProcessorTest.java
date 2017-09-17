package com.pehulja.thefloow.service.text_processing;

import com.pehulja.thefloow.service.queue.statistics.QueueStatisticsService;
import com.pehulja.thefloow.storage.documents.FileChunk;
import com.pehulja.thefloow.storage.documents.QueueItem;
import com.pehulja.thefloow.storage.documents.Word;
import com.pehulja.thefloow.storage.repository.CustomWordRepository;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by baske on 18.09.2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"chunk.max-size=5"})
public class DefaultFileChunkProcessorTest {
    @MockBean
    private QueueStatisticsService queueStatisticsService;

    @MockBean
    private CustomWordRepository customWordRepository;

    @Captor
    private ArgumentCaptor<List<Word>> argumentCaptor;

    @Autowired
    private DefaultFileChunkProcessor defaultFileChunkProcessor;

    @Test
    public void accept() throws Exception {
        Mockito.doNothing().when(customWordRepository).merge(argumentCaptor.capture());

        QueueItem input = QueueItem.builder().fileChunk(FileChunk.builder().chunkId(0l).fileName("a.txt").content("aaa bbbbb").build()).build();
        List<Word> expectedWords = new ArrayList<>();
        expectedWords.add(Word.builder().word("aaa").counter(1l).build());
        expectedWords.add(Word.builder().word("bbbbb").counter(1l).build());

        defaultFileChunkProcessor.accept(input);

        Mockito.verify(queueStatisticsService).incrementSuccessfullyProcessed();
        Mockito.verify(queueStatisticsService, Mockito.never()).incrementFailedToProcess();
        Assertions.assertThat(argumentCaptor.getValue()).isEqualTo(expectedWords);
    }

    @Test
    public void acceptThrowsException() throws Exception {
        Mockito.doThrow(Exception.class).when(customWordRepository).merge(argumentCaptor.capture());

        QueueItem input = QueueItem.builder().fileChunk(FileChunk.builder().chunkId(0l).fileName("a.txt").content("aaa bbbbb").build()).build();

        defaultFileChunkProcessor.accept(input);

        Mockito.verify(queueStatisticsService, Mockito.never()).incrementSuccessfullyProcessed();
        Mockito.verify(queueStatisticsService).incrementFailedToProcess();
    }

}
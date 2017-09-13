package com.pehulja.thefloow.cli;

import java.io.FileNotFoundException;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.pehulja.thefloow.exception.UnableProcessFileException;
import com.pehulja.thefloow.metric.WordsMetricHolder;
import com.pehulja.thefloow.service.metric.MetricsService;
import com.pehulja.thefloow.service.queue.statistics.QueueStatisticsService;
import com.pehulja.thefloow.service.text_processing.FileProcessor;
import com.pehulja.thefloow.storage.documents.QueueStatistics;

/**
 * Created by eyevpek on 2017-09-13.
 */
@RunWith (MockitoJUnitRunner.class)
public class CliCommandsProcessorTest
{
    private QueueStatistics expectedQueueStatistics;
    private Optional<WordsMetricHolder> expectedFileMetrics;
    private String expectedMetricsWhenEmpty = "Nothing to show";
    private static final String FILE_NAME = "randomFileName";
    private static final String ANOTHER_FILE_NAME = "anotherFileName";

    @Mock
    private QueueStatisticsService queueStatisticsService;

    @Mock
    private MetricsService metricService;

    @Mock
    private FileProcessor fileProcessor;

    @InjectMocks
    private CliCommandsProcessor cliCommandsProcessor;

    @Before
    public void setup() throws FileNotFoundException, UnableProcessFileException
    {
        expectedQueueStatistics = QueueStatistics.builder().pushedToQueue(1).failedToProcess(1).possibleLostOrInProgress(0).successfullyProcessed(0).build();
        expectedFileMetrics = Optional.of(WordsMetricHolder
                .builder()
                .metric("metric1", WordsMetricHolder.WordsMetric.builder().word("a").usageCounter(5l).build())
                .metric("metric2", WordsMetricHolder.WordsMetric.builder().word("b").usageCounter(7l).build())
                .build());

        Mockito.when(queueStatisticsService.getQueueStatistics()).thenReturn(expectedQueueStatistics);
        Mockito.when(metricService.byFileName(Mockito.eq(FILE_NAME))).thenReturn(expectedFileMetrics);
        Mockito.when(metricService.byFileName(Mockito.eq(ANOTHER_FILE_NAME))).thenReturn(Optional.empty());
        Mockito.doThrow(new UnableProcessFileException("SomeException", new Exception())).when(fileProcessor).processFile(ANOTHER_FILE_NAME);
        Mockito.doNothing().when(fileProcessor).processFile(FILE_NAME);
    }

    @Test
    public void getQueueStatistics() throws Exception
    {
        Assertions.assertThat(cliCommandsProcessor.getQueueStatistics()).isEqualTo(expectedQueueStatistics.toString());
    }

    @Test
    public void getFileWordsStatistics() throws Exception
    {
        Assertions.assertThat(cliCommandsProcessor.getFileWordsStatistics(FILE_NAME)).isEqualTo(expectedFileMetrics.get().toString());
    }

    @Test
    public void getFileWordsStatisticsEmpty() throws Exception
    {
        Assertions.assertThat(cliCommandsProcessor.getFileWordsStatistics(ANOTHER_FILE_NAME)).isEqualTo(expectedMetricsWhenEmpty);
    }

    @Test
    public void getOverallStatistics() throws Exception
    {
        Mockito.when(metricService.overall()).thenReturn(expectedFileMetrics);
        Assertions.assertThat(cliCommandsProcessor.getOverallStatistics()).isEqualTo(expectedFileMetrics.get().toString());
    }

    @Test
    public void getOverallStatisticsEmpty() throws Exception
    {
        Mockito.when(metricService.overall()).thenReturn(Optional.empty());
        Assertions.assertThat(cliCommandsProcessor.getOverallStatistics()).isEqualTo(expectedMetricsWhenEmpty);
    }

    @Test
    public void importLocalFileSuccess() throws Exception
    {
        String expected = String.format("Success: file %s has been imported and chunks pushed to the Mongo queue, \n" +
                "execute 'print-queue-wordStatistics' to see Mongo queue status", FILE_NAME);
        String actual = cliCommandsProcessor.importLocalFile(FILE_NAME);
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void importLocalFileFail() throws Exception
    {
        String expected = String.format("Fail: unable to import the file %s, reason: %s", ANOTHER_FILE_NAME, "SomeException");
        String actual = cliCommandsProcessor.importLocalFile(ANOTHER_FILE_NAME);
        Assertions.assertThat(actual).isEqualTo(expected);
    }
}
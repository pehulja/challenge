package com.pehulja.thefloow.cli;

import com.pehulja.thefloow.exception.UnableProcessChunkException;
import com.pehulja.thefloow.metric.MetricType;
import com.pehulja.thefloow.metric.WordsMetric;
import com.pehulja.thefloow.service.metric.MetricsService;
import com.pehulja.thefloow.service.queue.statistics.QueueStatisticsService;
import com.pehulja.thefloow.service.text_processing.FileProcessor;
import com.pehulja.thefloow.storage.documents.QueueStatistics;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.io.FileNotFoundException;
import java.util.Optional;

/**
 * Created by eyevpek on 2017-09-13.
 */
@RunWith (MockitoJUnitRunner.class)
public class CliCommandsProcessorTest
{
    private QueueStatistics expectedQueueStatistics;
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

    @Test
    public void getQueueStatistics() throws Exception
    {
        expectedQueueStatistics = QueueStatistics.builder().pushedToQueue(1).failedToProcess(1).possibleLostOrInProgress(0).successfullyProcessed(0).build();

        Mockito.when(queueStatisticsService.getQueueStatistics()).thenReturn(expectedQueueStatistics);

        Assertions.assertThat(cliCommandsProcessor.getQueueStatistics()).isEqualTo(expectedQueueStatistics.toString());
    }

    @Test
    public void getOverallStatistics() throws Exception
    {
        Map<MetricType, Optional<WordsMetric>> expectedMetrics = new HashMap<>();
        expectedMetrics.put(MetricType.LEAST_FREQUENTLY_USED, Optional.of(WordsMetric.builder().word("a").usageCounter(5l).build()));
        expectedMetrics.put(MetricType.MOST_FREQUENTLY_USED, Optional.empty());
        Mockito.when(metricService.get()).thenReturn(expectedMetrics);

        Assertions.assertThat(cliCommandsProcessor.getOverallStatistics()).contains(String.format("Metric 'LEAST_FREQUENTLY_USED': words: [%s], value %d", Arrays.asList("a"), 5l));
        Assertions.assertThat(cliCommandsProcessor.getOverallStatistics()).contains("Metric 'MOST_FREQUENTLY_USED' not applicable or can't be calculated");

    }

    @Test
    public void importLocalFileSuccess() throws Exception
    {
        String expected = String.format("Success: processing of file %s has been started in background and chunks will be pushed to the Mongo queue, \n" +
                "execute 'print-queue-statistics' to see Mongo queue status", FILE_NAME);
        String actual = cliCommandsProcessor.importLocalFile(FILE_NAME);
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void importLocalFileFail() throws Exception
    {
        Mockito.doThrow(new UnableProcessChunkException("SomeException", new Exception())).when(fileProcessor).processFile(ANOTHER_FILE_NAME);
        Mockito.doNothing().when(fileProcessor).processFile(FILE_NAME);
        String expected = String.format("Fail: unable to import the file %s, reason: %s", ANOTHER_FILE_NAME, "SomeException");
        String actual = cliCommandsProcessor.importLocalFile(ANOTHER_FILE_NAME);
        Assertions.assertThat(actual).isEqualTo(expected);
    }
}
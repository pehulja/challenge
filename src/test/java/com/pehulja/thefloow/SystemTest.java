package com.pehulja.thefloow;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pehulja.thefloow.cli.CliCommandsProcessor;
import com.pehulja.thefloow.metric.MetricType;
import com.pehulja.thefloow.metric.WordsMetric;
import com.pehulja.thefloow.service.metric.MetricsService;
import com.pehulja.thefloow.service.queue.statistics.QueueStatisticsService;
import com.pehulja.thefloow.storage.documents.QueueStatistics;

/**
 * Created by baske on 18.09.2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"chunk.max-size=200", "queue.listener.enabled=true"})
public class SystemTest extends AbstractTestWithMongo {
    @Autowired
    private CliCommandsProcessor cliCommandsProcessor;

    @Autowired
    private QueueStatisticsService queueStatisticsService;

    @Autowired
    private MetricsService metricsService;

    @Test
    public void execute() throws URISyntaxException, InterruptedException {
        Path inputFile = Paths.get(this.getClass().getClassLoader().getResource("systemTestFile.txt").toURI());
        String fileLocation = inputFile.toFile().getAbsolutePath();

        String importResponse = cliCommandsProcessor.importLocalFile(fileLocation);
        Assertions.assertThat(importResponse).contains("Success");
        Thread.sleep(15000);

        QueueStatistics queueStatistics;
        do {
            Thread.sleep(5000);
            queueStatistics = queueStatisticsService.getQueueStatistics();
        }
        while (queueStatistics.getFailedToProcess() + queueStatistics.getPushedToQueue() != queueStatistics.getPushedToQueue());
        Map<MetricType, Optional<WordsMetric>> actual = metricsService.get();
        Assertions.assertThat(actual.get(MetricType.LEAST_FREQUENTLY_USED).get().getWords()).contains("Welcome", "Salvation");
    }
}

package com.pehulja.thefloow;

import com.pehulja.thefloow.cli.CliCommandsProcessor;
import com.pehulja.thefloow.metric.MetricType;
import com.pehulja.thefloow.metric.WordsMetric;
import com.pehulja.thefloow.service.metric.MetricsService;
import com.pehulja.thefloow.service.queue.statistics.QueueStatisticsService;
import com.pehulja.thefloow.storage.documents.QueueStatistics;
import com.pehulja.thefloow.storage.documents.Word;
import com.pehulja.thefloow.storage.repository.WordRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

        QueueStatistics queueStatistics;
        do {
            Thread.sleep(30000);
            queueStatistics = queueStatisticsService.getQueueStatistics();
        }while (queueStatistics.getFailedToProcess() + queueStatistics.getPushedToQueue() != queueStatistics.getPushedToQueue());
        Assertions.assertThat(queueStatistics.getPushedToQueue()).isEqualTo(101l);
        Assertions.assertThat(queueStatistics.getSuccessfullyProcessed()).isEqualTo(queueStatistics.getPushedToQueue());

        Map<MetricType, Optional<WordsMetric>> actual = metricsService.get();
        Assertions.assertThat(actual.get(MetricType.MOST_FREQUENTLY_USED).get().getUsageCounter()).isEqualTo(100l);
        Assertions.assertThat(actual.get(MetricType.LEAST_FREQUENTLY_USED).get().getUsageCounter()).isEqualTo(1l);
        Assertions.assertThat(actual.get(MetricType.MOST_FREQUENTLY_USED).get().getWords()).contains("2512419225", "2512781688", "2512576589", "2512884832");
        Assertions.assertThat(actual.get(MetricType.LEAST_FREQUENTLY_USED).get().getWords()).contains("Welcome", "Salvation");
    }
}

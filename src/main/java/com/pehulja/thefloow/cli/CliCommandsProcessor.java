package com.pehulja.thefloow.cli;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import com.pehulja.thefloow.metric.MetricType;
import com.pehulja.thefloow.metric.WordsMetric;
import com.pehulja.thefloow.service.metric.MetricsService;
import com.pehulja.thefloow.service.queue.statistics.QueueStatisticsService;
import com.pehulja.thefloow.service.text_processing.FileProcessor;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by eyevpek on 2017-09-12.
 */
@Component
@Slf4j
public class CliCommandsProcessor implements CommandMarker {
    private static final String METRIC_FORMAT_OUTPUT = "Metric '%s': words: [%s], value %d";
    private static final String METRIC_CANT_BE_CALCULATED = "Metric '%s' not applicable or can't be calculated";
    private static final int LIMIT_WORDS_TO_SHOW = 200;

    @Autowired
    private QueueStatisticsService queueStatisticsService;

    @Autowired
    private MetricsService metricService;

    @Autowired
    private FileProcessor fileProcessor;

    @CliCommand(value = {"print-queue-statistics"})
    public String getQueueStatistics() {
        return queueStatisticsService.getQueueStatistics().toString();
    }

    @CliCommand(value = {"print-word-statistics"})
    public String getOverallStatistics() {
        StringBuilder result = new StringBuilder();
        Map<MetricType, Optional<WordsMetric>> metrics = metricService.get();

        for (Map.Entry<MetricType, Optional<WordsMetric>> metric : metrics.entrySet()) {
            result.append(metric.getValue()
                    .map(words -> String.format(METRIC_FORMAT_OUTPUT, metric.getKey().name(),
                            words.getWords().size() > LIMIT_WORDS_TO_SHOW ? new ArrayList<>(words.getWords()).subList(0, LIMIT_WORDS_TO_SHOW - 1).toString() + "..." : words.getWords().toString(),
                            words.getUsageCounter()))
                    .orElse(String.format(METRIC_CANT_BE_CALCULATED, metric.getKey())));
            result.append('\n');
        }
        return result.toString();
    }

    @CliCommand(value = {"import"}, help = "use --file [path to local file] to process specific file")
    public String importLocalFile(@CliOption(key = {"file"}, mandatory = true) String file) {
        try {
            fileProcessor.processFile(file);
        } catch (Exception ex) {
            String errorMessage = String.format("Fail: unable to import the file %s, reason: %s", file, ex.getMessage());
            log.error(errorMessage, ex);
            return errorMessage;
        }

        return String.format("Success: processing of file %s has been started in background and chunks will be pushed to the Mongo queue, \n" +
                "execute 'print-queue-statistics' to see Mongo queue status", file);
    }
}

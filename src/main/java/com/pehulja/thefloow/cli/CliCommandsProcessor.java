package com.pehulja.thefloow.cli;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import com.pehulja.thefloow.metric.WordsMetricHolder;
import com.pehulja.thefloow.service.file_processing.FileProcessor;
import com.pehulja.thefloow.service.metric.MetricService;
import com.pehulja.thefloow.service.queue.statistics.QueueStatisticsService;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by eyevpek on 2017-09-12.
 */
@Component
@Slf4j
public class CliCommandsProcessor implements CommandMarker
{
    @Autowired
    private QueueStatisticsService queueStatisticsService;

    @Autowired
    private MetricService metricService;

    @Autowired
    private FileProcessor fileProcessor;

    @CliCommand (value = {"print-queue-wordStatistics"})
    public String getQueueStatistics()
    {
        return queueStatisticsService.getQueueStatistics().toString();
    }

    @CliCommand (value = {"print-file-wordStatistics"}, help = "use --file-name [file-name] to get wordStatistics per specific file name imported")
    public String getFileWordsStatistics(@CliOption (key = {"file-name"}, mandatory = true) String fileName)
    {
        return metricService.byFileName(fileName).map(WordsMetricHolder::toString).orElse("Nothing to show");
    }

    @CliCommand (value = {"print-overall-wordStatistics"})
    public String getOverallStatistics()
    {
        return metricService.overall().map(WordsMetricHolder::toString).orElse("Nothing to show");
    }

    @CliCommand (value = {"import"}, help = "use --file [path to local file] to process specific file")
    public String importLocalFile(@CliOption (key = {"file"}, mandatory = true) String file)
    {
        try
        {
            fileProcessor.processFile(file);
        }
        catch (Exception ex)
        {
            String errorMessage = String.format("Unable to import the file %s, reason: %s", file, ex.getMessage());
            log.error(errorMessage, ex);
            return errorMessage;
        }

        return String.format("File %s has been imported and chunks pushed to the Mongo queue, \n" +
                "execute 'print-queue-wordStatistics' to see Mongo queue status");
    }
}

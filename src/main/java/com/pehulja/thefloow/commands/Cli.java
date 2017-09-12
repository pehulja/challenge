package com.pehulja.thefloow.commands;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

import com.pehulja.thefloow.service.FileProcessor;
import com.pehulja.thefloow.service.MetricsService;
import com.pehulja.thefloow.service.QueueStatisticsService;
import com.pehulja.thefloow.statistics.Metrics;

/**
 * Created by eyevpek on 2017-09-12.
 */
@Component
public class Cli implements CommandMarker
{
    @Autowired
    private QueueStatisticsService queueStatisticsService;

    @Autowired
    private MetricsService metricsService;

    @Autowired
    private FileProcessor fileProcessor;

    @CliCommand (value = {"queue-statistics"})
    public String getQueueStatistics()
    {
        return queueStatisticsService.getQueueStatistics().toString();
    }

    @CliCommand (value = {"print-file-statistics"}, help = "use --file-name [file-name] to get statistics per specific file name imported")
    public String getFileWordsStatistics(@CliOption (key = {"file-name"}, mandatory = true) String fileName)
    {
        return metricsService.byFileName(fileName).map(Metrics::toString).orElse("Nothing to show");
    }

    @CliCommand (value = {"print-overall-statistics"})
    public String getOverallStatistics()
    {
        return metricsService.overall().map(Metrics::toString).orElse("Nothing to show");
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
            return ex.getMessage();
        }

        return "Imported";
    }
}

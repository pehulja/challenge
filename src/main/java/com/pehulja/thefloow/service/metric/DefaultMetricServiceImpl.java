package com.pehulja.thefloow.service.metric;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pehulja.thefloow.metric.Metric;
import com.pehulja.thefloow.metric.WordsMetricHolder;
import com.pehulja.thefloow.storage.documents.FileWordsStatistics;
import com.pehulja.thefloow.storage.repository.FileWordsStatisticsRepository;

import lombok.Setter;

/**
 * Created by eyevpek on 2017-09-12.
 */
@Service
@Setter
public class DefaultMetricServiceImpl implements MetricService
{
    @Autowired
    private Set<Metric> metrics;

    @Autowired
    private FileWordsStatisticsRepository fileWordsStatisticsRepository;

    @Override
    public WordsMetricHolder collectMetrics(List<FileWordsStatistics> statistics)
    {
        WordsMetricHolder.WordsMetricHolderBuilder builder = WordsMetricHolder.builder();
        for (Metric metric : metrics)
        {
            builder.metric(metric.getName(), metric.apply(statistics));
        }

        return builder.build();
    }

    @Override
    public Optional<WordsMetricHolder> byFileName(String fileName)
    {
        List<FileWordsStatistics> statistics = fileWordsStatisticsRepository.findByFileName(fileName);
        if (statistics == null || statistics.isEmpty())
        {
            return Optional.empty();
        }
        else
        {
            return Optional.of(this.collectMetrics(statistics));
        }
    }

    @Override
    public Optional<WordsMetricHolder> overall()
    {
        List<FileWordsStatistics> statistics = fileWordsStatisticsRepository.findAll();
        if (statistics == null || statistics.isEmpty())
        {
            return Optional.empty();
        }
        else
        {
            return Optional.of(this.collectMetrics(statistics));
        }
    }
}

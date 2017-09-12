package com.pehulja.thefloow.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pehulja.thefloow.repository.StatisticsRepository;
import com.pehulja.thefloow.statistics.Metrics;
import com.pehulja.thefloow.statistics.Statistics;

import lombok.Setter;

/**
 * Created by eyevpek on 2017-09-12.
 */
@Service
@Setter
public class DefaultMetricsService implements MetricsService
{
    @Autowired
    private Set<Metric> metrics;

    @Autowired
    private StatisticsRepository statisticsRepository;

    @Override
    public Metrics collectMetrics(List<Statistics> statistics)
    {
        Metrics.MetricsBuilder builder = Metrics.builder();
        for (Metric metric : metrics)
        {
            builder.metric(metric.getName(), metric.apply(statistics));
        }

        return builder.build();
    }

    @Override
    public Optional<Metrics> byFileName(String fileName)
    {
        List<Statistics> statistics = statisticsRepository.findByFileName(fileName);
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
    public Optional<Metrics> overall()
    {
        List<Statistics> statistics = statisticsRepository.findAll();
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

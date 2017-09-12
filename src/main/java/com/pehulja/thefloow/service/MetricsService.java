package com.pehulja.thefloow.service;

import java.util.List;
import java.util.Optional;

import com.pehulja.thefloow.statistics.Metrics;
import com.pehulja.thefloow.statistics.Statistics;

/**
 * Created by eyevpek on 2017-09-12.
 */
public interface MetricsService
{
    Metrics collectMetrics(List<Statistics> statistics);

    Optional<Metrics> byFileName(String fileName);

    Optional<Metrics> overall();
}

package com.pehulja.thefloow.service.metric;

import com.pehulja.thefloow.metric.MetricProcessor;
import com.pehulja.thefloow.metric.MetricType;
import com.pehulja.thefloow.metric.WordsMetric;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by eyevpek on 2017-09-12.
 */
@Service
@Setter
public class DefaultMetricsServiceImpl implements MetricsService {
    @Autowired
    private Set<MetricProcessor> metricProcessors;

    @Override
    public Map<MetricType, Optional<WordsMetric>> get() {
        return metricProcessors
                .parallelStream()
                .collect(Collectors.toMap(MetricProcessor::getMetricType, metric -> metric.get()));
    }
}


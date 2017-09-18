package com.pehulja.thefloow.service.metric;

import com.pehulja.thefloow.metric.MetricType;
import com.pehulja.thefloow.metric.WordsMetric;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Created by eyevpek on 2017-09-12.
 */
public interface MetricsService extends Supplier<Map<MetricType, Optional<WordsMetric>>> {
}

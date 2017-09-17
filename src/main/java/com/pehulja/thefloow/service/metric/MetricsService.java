package com.pehulja.thefloow.service.metric;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.Map;

import com.pehulja.thefloow.metric.MetricType;
import com.pehulja.thefloow.metric.WordsMetric;

/**
 * Created by eyevpek on 2017-09-12.
 */
public interface MetricsService extends Supplier<Map<MetricType, Optional<WordsMetric>>>
{
}

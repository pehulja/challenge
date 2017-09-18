package com.pehulja.thefloow.metric;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Created by eyevpek on 2017-09-12.
 */
public interface MetricProcessor extends Supplier<Optional<WordsMetric>> {
    MetricType getMetricType();
}

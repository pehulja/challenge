package com.pehulja.thefloow.metric;

import com.pehulja.thefloow.storage.repository.CustomWordRepository;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.function.Function;

/**
 * Created by eyevpek on 2017-09-12.
 */
@Setter
public abstract class AbstractMetricProcessor implements MetricProcessor, Function<Long, WordsMetric> {
    @Autowired
    private CustomWordRepository customWordRepository;

    @Override
    public WordsMetric apply(Long value) {
        return WordsMetric.builder()
                .usageCounter(value)
                .words(customWordRepository.findWordsByCount(value))
                .build();
    }
}

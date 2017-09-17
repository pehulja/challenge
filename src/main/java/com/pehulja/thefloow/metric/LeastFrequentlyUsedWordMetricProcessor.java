package com.pehulja.thefloow.metric;

import com.pehulja.thefloow.storage.repository.CustomWordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by eyevpek on 2017-09-12.
 */
@Service
public class LeastFrequentlyUsedWordMetricProcessor extends AbstractMetricProcessor implements MetricProcessor
{
    @Autowired
    private CustomWordRepository customWordRepository;

    @Override
    public MetricType getMetricType()
    {
        return MetricType.LEAST_FREQUENTLY_USED;
    }

    @Override
    public Optional<WordsMetric> get() {
        return customWordRepository.findMin().map(this);
    }
}

package com.pehulja.thefloow.metric;

import com.pehulja.thefloow.storage.repository.CustomWordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by eyevpek on 2017-09-12.
 */
@Service
public class MostFrequentlyUsedWordMetricProcessor extends AbstractMetricProcessor implements MetricProcessor
{
    @Autowired
    private CustomWordRepository customWordRepository;

    @Override
    public Optional<WordsMetric> get() {
        return customWordRepository.findMax().map(this);
    }

    @Override
    public MetricType getMetricType()
    {
        return MetricType.MOST_FREQUENTLY_USED;
    }
}

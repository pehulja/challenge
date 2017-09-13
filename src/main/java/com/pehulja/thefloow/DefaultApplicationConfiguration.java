package com.pehulja.thefloow;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

import com.pehulja.thefloow.metric.LeastFrequentlyUsedWordMetric;
import com.pehulja.thefloow.metric.Metric;
import com.pehulja.thefloow.metric.MostFrequentlyUsedWordMetric;

/**
 * Created by eyevpek on 2017-09-11.
 */
@Configuration
@EnableAsync
@EnableMongoAuditing
public class DefaultApplicationConfiguration
{
    @Bean
    public Set<Metric> getActiveMetrics()
    {
        return new HashSet<>(Arrays.asList(
                new MostFrequentlyUsedWordMetric(),
                new LeastFrequentlyUsedWordMetric()));
    }
}

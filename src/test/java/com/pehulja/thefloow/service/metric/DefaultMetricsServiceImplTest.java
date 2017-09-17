package com.pehulja.thefloow.service.metric;

import com.pehulja.thefloow.metric.*;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by baske on 18.09.2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DefaultMetricsServiceImplTest {

    @SpyBean
    private LeastFrequentlyUsedWordMetricProcessor leastFrequentlyUsedWordMetricProcessor;

    @SpyBean
    private MostFrequentlyUsedWordMetricProcessor mostFrequentlyUsedWordMetricProcessor;

    @Autowired
    private DefaultMetricsServiceImpl defaultMetricsService;

    @Before
    public void setup(){
        Mockito.when(mostFrequentlyUsedWordMetricProcessor.get()).thenReturn(Optional.empty());
        Mockito.when(leastFrequentlyUsedWordMetricProcessor.get()).thenReturn(Optional.of(WordsMetric.builder().build()));

    }
    @Test
    public void get() throws Exception {
        Map<MetricType, Optional<WordsMetric>> expected = new HashMap<>();
        expected.put(MetricType.LEAST_FREQUENTLY_USED, Optional.of(WordsMetric.builder().build()));
        expected.put(MetricType.MOST_FREQUENTLY_USED, Optional.empty());

        Assertions.assertThat(defaultMetricsService.get()).isEqualTo(expected);
    }

}
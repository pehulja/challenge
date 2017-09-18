package com.pehulja.thefloow.metric;

import com.pehulja.thefloow.storage.repository.CustomWordRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Created by baske on 18.09.2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class LeastFrequentlyUsedWordMetricProcessorTest {
    @MockBean
    private CustomWordRepository customWordRepository;

    @InjectMocks
    @Autowired
    private LeastFrequentlyUsedWordMetricProcessor leastFrequentlyUsedWordMetricProcessor;

    @Test
    public void getMetricType() throws Exception {
        Assertions.assertThat(leastFrequentlyUsedWordMetricProcessor.getMetricType()).isEqualTo(MetricType.LEAST_FREQUENTLY_USED);
    }

    @Test
    public void get() throws Exception {
        Long minValue = 1l;
        Set<String> words = new HashSet<>(Arrays.asList("b", "c"));

        Mockito.when(customWordRepository.findMin()).thenReturn(Optional.of(minValue));
        Mockito.when(customWordRepository.findWordsByCount(Mockito.eq(minValue))).thenReturn(words);

        Optional<WordsMetric> actual = leastFrequentlyUsedWordMetricProcessor.get();
        Optional<WordsMetric> expected = Optional.of(WordsMetric.builder().usageCounter(minValue).words(words).build());
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void getNotPresentMetric() throws Exception {
        Mockito.when(customWordRepository.findMin()).thenReturn(Optional.empty());

        Optional<WordsMetric> actual = leastFrequentlyUsedWordMetricProcessor.get();
        Optional<WordsMetric> expected = Optional.empty();
        Assertions.assertThat(actual).isEqualTo(expected);
        Mockito.verify(customWordRepository, Mockito.never()).findWordsByCount(Mockito.any());
    }

}
package com.pehulja.thefloow.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import com.pehulja.thefloow.metric.LeastFrequentlyUsedWordMetric;
import com.pehulja.thefloow.metric.Metric;
import com.pehulja.thefloow.metric.MostFrequentlyUsedWordMetric;
import com.pehulja.thefloow.metric.WordsMetricHolder;
import com.pehulja.thefloow.service.metric.DefaultMetricServiceImpl;
import com.pehulja.thefloow.storage.documents.FileWordsStatistics;
import com.pehulja.thefloow.storage.repository.FileWordsStatisticsRepository;

/**
 * Created by eyevpek on 2017-09-12.
 */
public class DefaultMetricServiceImplTest
{
    public static final String FILE_NAME = "fileName";
    public static final String DIFFERENT_FILE_NAME = "differentFileName";
    @InjectMocks
    @Spy
    private DefaultMetricServiceImpl defaultMetricsServiceSpy;

    private Set<Metric> metricsMock;

    @Spy
    private MostFrequentlyUsedWordMetric mostFrequentlyUsedWordMetric;

    @Spy
    private LeastFrequentlyUsedWordMetric leastFrequentlyUsedWordMetric;

    @Mock
    private WordsMetricHolder.WordsMetric mostFrequentlyUsedWordMetricTuple;

    @Mock
    private WordsMetricHolder.WordsMetric leastFrequentlyUsedWordMetricTuple;

    @Mock
    private WordsMetricHolder wordsMetricsHolder;

    @Mock
    private FileWordsStatisticsRepository fileWordsStatisticsRepositoryMock;

    @Mock
    private FileWordsStatistics a;

    @Mock
    private FileWordsStatistics b;

    @Mock
    private FileWordsStatistics c;

    @Before
    public void setupMocks()
    {
        Mockito.when(leastFrequentlyUsedWordMetric.apply(Mockito.anyList())).thenReturn(leastFrequentlyUsedWordMetricTuple);
        Mockito.when(mostFrequentlyUsedWordMetric.apply(Mockito.anyList())).thenReturn(mostFrequentlyUsedWordMetricTuple);
        metricsMock = new HashSet<>(Arrays.asList(mostFrequentlyUsedWordMetric, leastFrequentlyUsedWordMetric));
        Mockito.when(fileWordsStatisticsRepositoryMock.findByFileName(FILE_NAME)).thenReturn(Arrays.asList(a, b));
        Mockito.when(fileWordsStatisticsRepositoryMock.findByFileName(DIFFERENT_FILE_NAME)).thenReturn(Arrays.asList(c));
        Mockito.when(fileWordsStatisticsRepositoryMock.findAll()).thenReturn(Arrays.asList(a, b, c));

        defaultMetricsServiceSpy.setMetrics(metricsMock);
    }

    @Test
    public void collectMetrics() throws Exception
    {
        WordsMetricHolder expected = WordsMetricHolder.builder()
                .metric(LeastFrequentlyUsedWordMetric.NAME, leastFrequentlyUsedWordMetricTuple)
                .metric(MostFrequentlyUsedWordMetric.NAME, mostFrequentlyUsedWordMetricTuple)
                .build();

        Assertions.assertThat(defaultMetricsServiceSpy.collectMetrics(Collections.emptyList())).isEqualTo(expected);
    }

    @Test
    public void byFileName() throws Exception
    {
        Mockito.doReturn(wordsMetricsHolder).when(defaultMetricsServiceSpy).collectMetrics(Mockito.eq(Arrays.asList(a, b)));
        Optional<WordsMetricHolder> actual = defaultMetricsServiceSpy.byFileName(FILE_NAME);
        Assertions.assertThat(actual).isEqualTo(Optional.of(wordsMetricsHolder));
    }

    @Test
    public void byFileNameNoMatched() throws Exception
    {
        Mockito.doReturn(wordsMetricsHolder).when(defaultMetricsServiceSpy).collectMetrics(Mockito.eq(Arrays.asList(a, b)));
        Optional<WordsMetricHolder> actual = defaultMetricsServiceSpy.byFileName("someOtherFileName");
        Assertions.assertThat(actual).isEqualTo(Optional.empty());
    }

    @Test
    public void overall() throws Exception
    {
        Mockito.doReturn(wordsMetricsHolder).when(defaultMetricsServiceSpy).collectMetrics(Mockito.eq(Arrays.asList(a, b, c)));
        Optional<WordsMetricHolder> actual = defaultMetricsServiceSpy.overall();
        Assertions.assertThat(actual).isEqualTo(Optional.of(wordsMetricsHolder));
    }

}
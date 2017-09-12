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

import com.pehulja.thefloow.repository.StatisticsRepository;
import com.pehulja.thefloow.statistics.Metrics;
import com.pehulja.thefloow.statistics.Statistics;
import com.pehulja.thefloow.statistics.Tuple;

/**
 * Created by eyevpek on 2017-09-12.
 */
public class DefaultMetricsServiceTest
{
    public static final String FILE_NAME = "fileName";
    public static final String DIFFERENT_FILE_NAME = "differentFileName";
    @InjectMocks
    @Spy
    private DefaultMetricsService defaultMetricsServiceSpy;

    private Set<Metric> metricsMock;

    @Spy
    private MostFrequentlyUsedWordMetric mostFrequentlyUsedWordMetric;

    @Spy
    private LeastFrequentlyUsedWordMetric leastFrequentlyUsedWordMetric;

    @Mock
    private Tuple mostFrequentlyUsedWordMetricTuple;

    @Mock
    private Tuple leastFrequentlyUsedWordMetricTuple;

    @Mock
    private Metrics metrics;

    @Mock
    private StatisticsRepository statisticsRepositoryMock;

    @Mock
    private Statistics a;

    @Mock
    private Statistics b;

    @Mock
    private Statistics c;

    @Before
    public void setupMocks()
    {
        Mockito.when(leastFrequentlyUsedWordMetric.apply(Mockito.anyList())).thenReturn(leastFrequentlyUsedWordMetricTuple);
        Mockito.when(mostFrequentlyUsedWordMetric.apply(Mockito.anyList())).thenReturn(mostFrequentlyUsedWordMetricTuple);
        metricsMock = new HashSet<>(Arrays.asList(mostFrequentlyUsedWordMetric, leastFrequentlyUsedWordMetric));
        Mockito.when(statisticsRepositoryMock.findByFileName(FILE_NAME)).thenReturn(Arrays.asList(a, b));
        Mockito.when(statisticsRepositoryMock.findByFileName(DIFFERENT_FILE_NAME)).thenReturn(Arrays.asList(c));
        Mockito.when(statisticsRepositoryMock.findAll()).thenReturn(Arrays.asList(a, b, c));

        defaultMetricsServiceSpy.setMetrics(metricsMock);
    }

    @Test
    public void collectMetrics() throws Exception
    {
        Metrics expected = Metrics.builder()
                .metric(LeastFrequentlyUsedWordMetric.NAME, leastFrequentlyUsedWordMetricTuple)
                .metric(MostFrequentlyUsedWordMetric.NAME, mostFrequentlyUsedWordMetricTuple)
                .build();

        Assertions.assertThat(defaultMetricsServiceSpy.collectMetrics(Collections.emptyList())).isEqualTo(expected);
    }

    @Test
    public void byFileName() throws Exception
    {
        Mockito.doReturn(metrics).when(defaultMetricsServiceSpy).collectMetrics(Mockito.eq(Arrays.asList(a, b)));
        Optional<Metrics> actual = defaultMetricsServiceSpy.byFileName(FILE_NAME);
        Assertions.assertThat(actual).isEqualTo(Optional.of(metrics));
    }

    @Test
    public void byFileNameNoMatched() throws Exception
    {
        Mockito.doReturn(metrics).when(defaultMetricsServiceSpy).collectMetrics(Mockito.eq(Arrays.asList(a, b)));
        Optional<Metrics> actual = defaultMetricsServiceSpy.byFileName("someOtherFileName");
        Assertions.assertThat(actual).isEqualTo(Optional.empty());
    }

    @Test
    public void overall() throws Exception
    {
        Mockito.doReturn(metrics).when(defaultMetricsServiceSpy).collectMetrics(Mockito.eq(Arrays.asList(a, b, c)));
        Optional<Metrics> actual = defaultMetricsServiceSpy.overall();
        Assertions.assertThat(actual).isEqualTo(Optional.of(metrics));
    }

}
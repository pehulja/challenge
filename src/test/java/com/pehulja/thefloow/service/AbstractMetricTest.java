package com.pehulja.thefloow.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.pehulja.thefloow.statistics.Statistics;

/**
 * Created by eyevpek on 2017-09-12.
 */

public class AbstractMetricTest
{
    private AbstractMetric abstractMetricMock;

    @Before
    public void setupMocks()
    {
        abstractMetricMock = Mockito.mock(AbstractMetric.class, Mockito.CALLS_REAL_METHODS);
        ;
    }

    @Test
    public void join() throws Exception
    {
        List<Statistics> statistics = new ArrayList<>();
        statistics.add(Statistics.builder()
                .statistic("a", 1l)
                .statistic("b", 2l)
                .build());
        statistics.add(Statistics.builder()
                .statistic("a", 3l)
                .statistic("c", 4l)
                .build());

        Map<String, Long> expected = new HashMap<>();
        expected.put("a", 4l);
        expected.put("b", 2l);
        expected.put("c", 4l);

        Assertions.assertThat(abstractMetricMock.join(statistics)).isEqualTo(expected);
    }

    @Test
    public void filterByFrequency() throws Exception
    {
        Map<String, Long> input = new HashMap<>();
        input.put("a", 3l);
        input.put("b", 1l);
        input.put("c", 3l);

        Set<String> expected = new HashSet<>(Arrays.asList("a", "c"));
        Assertions.assertThat(abstractMetricMock.filterByFrequency(input, 3l)).isEqualTo(expected);
    }

}
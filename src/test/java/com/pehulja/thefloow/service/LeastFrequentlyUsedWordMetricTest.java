package com.pehulja.thefloow.service;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import com.pehulja.thefloow.statistics.Statistics;
import com.pehulja.thefloow.statistics.Tuple;

/**
 * Created by eyevpek on 2017-09-12.
 */
public class LeastFrequentlyUsedWordMetricTest
{
    private LeastFrequentlyUsedWordMetric leastFrequentlyUsedWordMetric;

    @Before
    public void setup()
    {
        leastFrequentlyUsedWordMetric = new LeastFrequentlyUsedWordMetric();
    }

    @Test
    public void apply() throws Exception
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

        Tuple expected = Tuple.builder().word("b").count(2l).build();
        Assertions.assertThat(leastFrequentlyUsedWordMetric.apply(statistics)).isEqualTo(expected);
    }

}
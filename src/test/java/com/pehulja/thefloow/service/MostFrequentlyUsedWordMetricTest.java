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
public class MostFrequentlyUsedWordMetricTest
{
    private MostFrequentlyUsedWordMetric mostFrequentlyUsedWordMetric;

    @Before
    public void setup()
    {
        mostFrequentlyUsedWordMetric = new MostFrequentlyUsedWordMetric();
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

        Tuple expected = Tuple.builder().word("a").word("c").count(4l).build();
        Assertions.assertThat(mostFrequentlyUsedWordMetric.apply(statistics)).isEqualTo(expected);
    }

}
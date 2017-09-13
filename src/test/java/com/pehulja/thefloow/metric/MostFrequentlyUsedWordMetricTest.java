package com.pehulja.thefloow.metric;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import com.pehulja.thefloow.storage.documents.FileWordsStatistics;

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
        List<FileWordsStatistics> statistics = new ArrayList<>();
        statistics.add(FileWordsStatistics.builder()
                .wordStatistic("a", 1l)
                .wordStatistic("b", 2l)
                .build());
        statistics.add(FileWordsStatistics.builder()
                .wordStatistic("a", 3l)
                .wordStatistic("c", 4l)
                .build());

        WordsMetricHolder.WordsMetric expected = WordsMetricHolder.WordsMetric.builder().word("a").word("c").usageCounter(4l).build();
        Assertions.assertThat(mostFrequentlyUsedWordMetric.apply(statistics)).isEqualTo(expected);
    }

}
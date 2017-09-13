package com.pehulja.thefloow.metric;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.pehulja.thefloow.storage.documents.FileWordsStatistics;

/**
 * Created by eyevpek on 2017-09-12.
 */
public class MostFrequentlyUsedWordMetric extends AbstractMetric implements Metric
{
    public final static String NAME = "MostFrequentlyUsed";

    /**
     * Applies this function to the given argument.
     *
     * @param statistics
     *         the function argument
     * @return the function result
     */
    @Override
    public WordsMetricHolder.WordsMetric apply(List<FileWordsStatistics> statistics)
    {
        Map<String, Long> combined = this.join(statistics);

        if (combined.isEmpty())
        {
            return new WordsMetricHolder.WordsMetric();
        }

        Optional<Long> maxOptional = combined.values().stream().max(Comparator.naturalOrder());

        return maxOptional.map(max ->
                new WordsMetricHolder.WordsMetric(
                        filterByFrequency(combined, max),
                        max)
        ).orElseGet(WordsMetricHolder.WordsMetric::new);
    }

    @Override
    public String getName()
    {
        return NAME;
    }
}

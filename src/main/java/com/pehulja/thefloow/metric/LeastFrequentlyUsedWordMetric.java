package com.pehulja.thefloow.metric;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.pehulja.thefloow.storage.documents.FileWordsStatistics;

/**
 * Created by eyevpek on 2017-09-12.
 */
public class LeastFrequentlyUsedWordMetric extends AbstractMetric implements Metric
{
    public final static String NAME = "LeastFrequentlyUsed";

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

        Optional<Long> minOptional = combined.values().stream().min(Comparator.naturalOrder());

        return minOptional.map(min ->
                new WordsMetricHolder.WordsMetric(
                        filterByFrequency(combined, min),
                        min)
        ).orElseGet(WordsMetricHolder.WordsMetric::new);
    }

    @Override
    public String getName()
    {
        return NAME;
    }
}

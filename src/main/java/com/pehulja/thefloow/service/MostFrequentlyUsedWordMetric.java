package com.pehulja.thefloow.service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.pehulja.thefloow.statistics.Statistics;
import com.pehulja.thefloow.statistics.Tuple;

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
    public Tuple apply(List<Statistics> statistics)
    {
        Map<String, Long> combined = this.join(statistics);

        if (combined.isEmpty())
        {
            return new Tuple();
        }

        Optional<Long> maxOptional = combined.values().stream().max(Comparator.naturalOrder());

        return maxOptional.map(max ->
                new Tuple(
                        filterByFrequency(combined, max),
                        max)
        ).orElseGet(Tuple::new);
    }

    @Override
    public String getName()
    {
        return NAME;
    }
}

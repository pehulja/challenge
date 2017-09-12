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
    public Tuple apply(List<Statistics> statistics)
    {
        Map<String, Long> combined = this.join(statistics);

        if (combined.isEmpty())
        {
            return new Tuple();
        }

        Optional<Long> minOptional = combined.values().stream().min(Comparator.naturalOrder());

        return minOptional.map(min ->
                new Tuple(
                        filterByFrequency(combined, min),
                        min)
        ).orElseGet(Tuple::new);
    }

    @Override
    public String getName()
    {
        return NAME;
    }
}

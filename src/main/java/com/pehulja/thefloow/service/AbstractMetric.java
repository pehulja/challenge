package com.pehulja.thefloow.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.pehulja.thefloow.statistics.Statistics;

/**
 * Created by eyevpek on 2017-09-12.
 */
public abstract class AbstractMetric implements Metric
{
    public Map<String, Long> join(List<Statistics> statistics)
    {
        return statistics.stream()
                .map(Statistics::getStatistics).map(Map::entrySet)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Long::sum));
    }

    public Set<String> filterByFrequency(Map<String, Long> input, Long value)
    {
        return input.entrySet().stream().filter(entry -> entry.getValue().equals(value)).map(Map.Entry::getKey).collect(Collectors.toSet());
    }
}

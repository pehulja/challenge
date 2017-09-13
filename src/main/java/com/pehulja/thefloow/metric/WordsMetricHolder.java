package com.pehulja.thefloow.metric;

import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

/**
 * Created by eyevpek on 2017-09-12.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WordsMetricHolder
{
    @Singular
    private Map<String, WordsMetric> metrics;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class WordsMetric
    {
        @Singular
        private Set<String> words;
        private Long usageCounter;
    }
}

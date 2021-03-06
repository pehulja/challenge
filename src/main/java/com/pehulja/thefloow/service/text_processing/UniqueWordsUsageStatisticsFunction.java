package com.pehulja.thefloow.service.text_processing;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by baske on 11.09.2017.
 */
public class UniqueWordsUsageStatisticsFunction implements Function<String, Map<String, Long>> {
    public static final String ALL_EXCEPT_WORD_CHRACTER_PATTERN = "[\\W\\']+";

    @Override
    public Map<String, Long> apply(String chunkContent) {
        Map<String, Long> chunkStatistics = Arrays.stream(chunkContent
                .split(ALL_EXCEPT_WORD_CHRACTER_PATTERN))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return chunkStatistics;
    }

}

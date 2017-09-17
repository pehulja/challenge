package com.pehulja.thefloow.metric;

import lombok.*;

import java.util.Set;

/**
 * Created by baske on 17.09.2017.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WordsMetric {
    @Singular
    private Set<String> words;
    private Long usageCounter;
}

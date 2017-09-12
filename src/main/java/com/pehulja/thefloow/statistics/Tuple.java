package com.pehulja.thefloow.statistics;

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
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Tuple
{
    @Singular
    private Set<String> words;
    private Long count;
}

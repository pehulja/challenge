package com.pehulja.thefloow.statistics;

import java.util.Map;

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
public class Metrics
{
    @Singular
    private Map<String, Tuple> metrics;

}

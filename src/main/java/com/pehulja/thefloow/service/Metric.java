package com.pehulja.thefloow.service;

import java.util.List;
import java.util.function.Function;

import com.pehulja.thefloow.statistics.Statistics;
import com.pehulja.thefloow.statistics.Tuple;

/**
 * Created by eyevpek on 2017-09-12.
 */
public interface Metric extends Function<List<Statistics>, Tuple>
{
    String getName();
}

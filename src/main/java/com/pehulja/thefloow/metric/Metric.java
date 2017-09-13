package com.pehulja.thefloow.metric;

import java.util.List;
import java.util.function.Function;

import com.pehulja.thefloow.storage.documents.FileWordsStatistics;

/**
 * Created by eyevpek on 2017-09-12.
 */
public interface Metric extends Function<List<FileWordsStatistics>, WordsMetricHolder.WordsMetric>
{
    String getName();
}

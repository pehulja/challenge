package com.pehulja.thefloow.service.metric;

import java.util.List;
import java.util.Optional;

import com.pehulja.thefloow.metric.WordsMetricHolder;
import com.pehulja.thefloow.storage.documents.FileWordsStatistics;

/**
 * Created by eyevpek on 2017-09-12.
 */
public interface MetricsService
{
    WordsMetricHolder collectMetrics(List<FileWordsStatistics> statistics);

    Optional<WordsMetricHolder> byFileName(String fileName);

    Optional<WordsMetricHolder> overall();
}

package com.pehulja.thefloow.service;

import com.pehulja.thefloow.UnableUpdateStatisticsException;
import com.pehulja.thefloow.queue.QueueItem;
import com.pehulja.thefloow.statistics.Statistics;

import java.util.function.BinaryOperator;
import java.util.function.Consumer;

/**
 * Created by baske on 11.09.2017.
 */
public interface StatisticsService extends Consumer<QueueItem>{
    Statistics optimisticUpdate(Statistics statistics, BinaryOperator<Statistics> retryOperation) throws UnableUpdateStatisticsException;
}

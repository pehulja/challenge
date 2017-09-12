package com.pehulja.thefloow.service;

import java.util.function.BinaryOperator;

import com.pehulja.thefloow.UnableUpdateDocumentException;
import com.pehulja.thefloow.queue.QueueItem;
import com.pehulja.thefloow.statistics.Statistics;

/**
 * Created by baske on 11.09.2017.
 */
public interface StatisticsService
{
    Statistics optimisticUpdate(Statistics statistics, BinaryOperator<Statistics> retryOperation) throws UnableUpdateDocumentException;

    Statistics processQueueItem(QueueItem queueItem) throws UnableUpdateDocumentException;
}

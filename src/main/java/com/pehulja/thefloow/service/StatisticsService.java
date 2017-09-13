package com.pehulja.thefloow.service;

import com.pehulja.thefloow.exception.UnableUpdateDocumentException;
import com.pehulja.thefloow.storage.documents.FileWordsStatistics;
import com.pehulja.thefloow.storage.documents.QueueItem;

/**
 * Created by baske on 11.09.2017.
 */
public interface StatisticsService
{
    FileWordsStatistics processQueueItem(QueueItem queueItem) throws UnableUpdateDocumentException;
}

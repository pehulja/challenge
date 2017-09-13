package com.pehulja.thefloow.storage.repository;

import java.util.function.BinaryOperator;

import com.pehulja.thefloow.exception.UnableUpdateDocumentException;
import com.pehulja.thefloow.storage.documents.FileWordsStatistics;

/**
 * Created by eyevpek on 2017-09-13.
 */
public interface CustomFileWordsStatisticsRepository
{
    FileWordsStatistics optimisticMerge(FileWordsStatistics fileWordsStatistics, BinaryOperator<FileWordsStatistics> mergeOperation) throws UnableUpdateDocumentException;
}

package com.pehulja.thefloow.storage.repository;

import com.pehulja.thefloow.storage.documents.FileWordsStatistics;

/**
 * Created by eyevpek on 2017-09-15.
 */
public interface CustomBucketRepository
{
    void process(FileWordsStatistics fileWordsStatistics);
}

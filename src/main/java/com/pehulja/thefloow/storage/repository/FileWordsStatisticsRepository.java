package com.pehulja.thefloow.storage.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.pehulja.thefloow.storage.documents.FileWordsStatistics;

/**
 * Created by baske on 11.09.2017.
 */

public interface FileWordsStatisticsRepository extends MongoRepository<FileWordsStatistics, String>
{
    List<FileWordsStatistics> findByFileName(String fileName);
}

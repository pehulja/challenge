package com.pehulja.thefloow.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.pehulja.thefloow.statistics.Statistics;

/**
 * Created by baske on 11.09.2017.
 */

public interface StatisticsRepository extends MongoRepository<Statistics, String> {
    List<Statistics> findByFileName(String fileName);
}

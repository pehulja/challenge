package com.pehulja.thefloow.storage.repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import com.pehulja.thefloow.storage.documents.FileWordsStatistics;

/**
 * Created by eyevpek on 2017-09-15.
 */
@Repository
public class CustomBucketRepositoryImpl implements CustomBucketRepository
{
    private long BUCKET_COUNT = 500;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void process(FileWordsStatistics fileWordsStatistics)
    {
        Map<Long, Map<String, Long>> groupedByBucket = fileWordsStatistics
                .getWordStatistics()
                .entrySet()
                .stream()
                .collect(Collectors.groupingBy(entry -> entry.getKey().hashCode() % BUCKET_COUNT, Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        List<Pair<Query, Update>> wordsUpdate = groupedByBucket.entrySet().parallelStream()
                .map(entry ->
                {
                    Query query = new Query();
                    query.addCriteria(Criteria.where("id").is(entry.getKey()));

                    Update update = new Update();
                    for (Map.Entry<String, Long> word : entry.getValue().entrySet())
                    {
                        update.inc("holder." + word.getKey(), word.getValue());
                    }

                    return Pair.of(query, update);
                }).collect(Collectors.toList());
        BulkOperations ops = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, FileWordsStatistics.class);
        ops.upsert(wordsUpdate).execute();
    }
}

package com.pehulja.thefloow.storage.repository;

import java.util.List;
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
import com.pehulja.thefloow.storage.documents.Word;

/**
 * Created by eyevpek on 2017-09-14.
 */
@Repository
public class CustomWordRepositoryImpl implements CustomWordRepository
{
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void apply(FileWordsStatistics fileWordsStatistics)
    {
        List<Pair<Query, Update>> wordsUpdate = fileWordsStatistics.getWordStatistics().entrySet().parallelStream()
                .map(word ->
                {
                    Query query = new Query();
                    query.addCriteria(Criteria.where("word").is(word.getKey()));
                    query.addCriteria(Criteria.where("fileName").is(fileWordsStatistics.getFileName()));
                    query.addCriteria(Criteria.where("fileId").is(fileWordsStatistics.getFileId()));

                    Update update = new Update();
                    update.inc("counter", word.getValue());

                    return Pair.of(query, update);
                }).collect(Collectors.toList());

        BulkOperations ops = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Word.class);
        ops.upsert(wordsUpdate).execute();
    }
}

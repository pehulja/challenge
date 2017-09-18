package com.pehulja.thefloow.storage.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.BulkOperationException;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Lists;
import com.pehulja.thefloow.storage.documents.Word;

/**
 * Created by eyevpek on 2017-09-14.
 */
@Repository
public class CustomWordRepositoryImpl implements CustomWordRepository {
    public static final String WORD_FIELD = "word";
    public static final String ID_FIELD = "_id";

    public static final String COUNTER_FIELD = "counter";
    public static final int BATCH_SIZE = 500;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Value ("${mongo.retry-count}")
    private Integer maxRetryAttempts;

    @Override
    public void merge(List<Word> wordList) {
        this.merge(wordList, 0);
    }

    public void merge(List<Word> wordList, int attempt)
    {
        try
        {
            List<Pair<Query, Update>> batchPayload = wordList.parallelStream()
                    .map(word ->
                    {
                        Query query = new Query();
                        query.addCriteria(Criteria.where(WORD_FIELD).is(word.getWord()));

                        Update update = new Update();
                        update.inc(COUNTER_FIELD, word.getCounter());

                        return Pair.of(query, update);
                    }).collect(Collectors.toList());

            List<List<Pair<Query, Update>>> partitions = Lists.partition(batchPayload, BATCH_SIZE);
            partitions.forEach(partition -> mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, Word.class).upsert(partition).execute());
        }
        catch (BulkOperationException bulkOperationException)
        {
            if (attempt <= maxRetryAttempts)
            {
                //Attempt to push this again due to well known issue https://jira.mongodb.org/browse/SERVER-14322
                merge(wordList, ++attempt);
            }
            else
            {
                throw bulkOperationException;
            }
        }
    }

    @Override
    public Optional<Long> findMax() {
        Query query = new Query();
        query.limit(1);
        query.fields().exclude(ID_FIELD).include(COUNTER_FIELD);
        query.with(new Sort(new Sort.Order(Sort.Direction.DESC, COUNTER_FIELD)));

        return Optional.ofNullable(mongoTemplate.findOne(query, Word.class)).map(Word::getCounter);
    }

    @Override
    public Optional<Long> findMin() {
        Query query = new Query();
        query.limit(1);
        query.fields().exclude(ID_FIELD).include(COUNTER_FIELD);
        query.with(new Sort(new Sort.Order(Sort.Direction.ASC, COUNTER_FIELD)));

        return Optional.ofNullable(mongoTemplate.findOne(query, Word.class)).map(Word::getCounter);
    }

    @Override
    public Set<String> findWordsByCount(Long count) {
        Query query = new Query();
        query.addCriteria(Criteria.where(COUNTER_FIELD).is(count));
        query.fields().exclude(COUNTER_FIELD);

        return mongoTemplate.find(query, Word.class).stream().map(Word::getWord).collect(Collectors.toSet());
    }
}

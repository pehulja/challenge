package com.pehulja.thefloow.service;

import com.pehulja.thefloow.UnableUpdateStatisticsException;
import com.pehulja.thefloow.queue.QueueItem;
import com.pehulja.thefloow.repository.StatisticsRepository;
import com.pehulja.thefloow.statistics.Statistics;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.CriteriaDefinition;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by baske on 11.09.2017.
 */
@Service
public class DefaultStatisticsService implements StatisticsService{
    @Autowired
    private MongoTemplate mongoTemplate;

    @Value("${mongo.retry-count}")
    private Integer retryCount;

    private Character [] WORDS_SEPARATOR = {' ', '\n'};

    @Autowired
    private StatisticsRepository statisticsRepository;

    @Override
    public void accept(QueueItem queueItem) {
        Map<String, Long> chunkStatistics = Arrays.stream(queueItem
                .getFileChunk()
                .getContent()
                .split("\\W+"))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        String fileId = queueItem.getFileChunk().getFileInfo().getFileId();

    }

    @Override
    public Statistics optimisticUpdate(Statistics statistics, BinaryOperator<Statistics> mergeOperation) throws UnableUpdateStatisticsException {
        return this.optimisticUpdate(statistics, mergeOperation, 0);
    }

    public Statistics optimisticUpdate(Statistics statistics, BinaryOperator<Statistics> mergeOperation, int attempt) throws UnableUpdateStatisticsException {
        Statistics existing = statisticsRepository.findOne(statistics.getFileId());

        Query query = new Query();
        query.addCriteria(Criteria.where("fileId").is(statistics.getFileId()));
        Optional.ofNullable(existing).ifPresent(existingStatistics -> {
            query.addCriteria(Criteria.where("version").is(existingStatistics.getVersion()));
        });

        query.limit(1);

        Update update = new Update();
        update.set("statistics", Optional.ofNullable(existing)
                .map(existingStatistics -> mergeOperation.apply(existingStatistics, statistics))
                .orElse(statistics)
                .getStatistics());

        try {
            return mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().upsert(true).returnNew(true), Statistics.class);
        } catch (DuplicateKeyException ex){
            if(attempt < retryCount){
                return this.optimisticUpdate(statistics, mergeOperation, ++attempt);
            }else {
                throw new UnableUpdateStatisticsException(String.format("Unable to update statistics: %s", statistics), ex);
            }
        }
    }

    public static class MergePolicy implements BinaryOperator<Statistics>{

        @Override
        public Statistics apply(Statistics a, Statistics b) {
            Map<String, Long> mergedStatistics = Stream.of(a.getStatistics(), b.getStatistics())
                    .map(Map::entrySet)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Long::sum));
            return b.toBuilder().statistics(mergedStatistics).build();
        }
    }

}

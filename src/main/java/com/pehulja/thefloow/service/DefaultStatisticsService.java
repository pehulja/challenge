package com.pehulja.thefloow.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.pehulja.thefloow.UnableUpdateDocumentException;
import com.pehulja.thefloow.queue.QueueItem;
import com.pehulja.thefloow.repository.StatisticsRepository;
import com.pehulja.thefloow.statistics.Statistics;

/**
 * Created by baske on 11.09.2017.
 */
@Service
public class DefaultStatisticsService implements StatisticsService
{
    @Autowired
    private MongoTemplate mongoTemplate;

    @Value("${mongo.retry-count}")
    private Integer retryCount;

    private Character [] WORDS_SEPARATOR = {' ', '\n'};

    @Autowired
    private StatisticsRepository statisticsRepository;

    @Override
    public Statistics processQueueItem(QueueItem queueItem) throws UnableUpdateDocumentException
    {
        Map<String, Long> chunkStatistics = Arrays.stream(queueItem
                .getFileChunk()
                .getContent()
                .split("\\W+"))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Statistics statistics = Statistics.builder()
                .fileId(queueItem.getFileChunk().getFileId())
                .fileName(queueItem.getFileChunk().getFileName())
                .statistics(chunkStatistics)
                .build();

        return this.optimisticUpdate(statistics, new MergeStatisticsFunction());
    }

    @Override
    public Statistics optimisticUpdate(Statistics statistics, BinaryOperator<Statistics> mergeOperation) throws UnableUpdateDocumentException
    {
        return this.optimisticUpdate(statistics, mergeOperation, 0);
    }

    private Statistics optimisticUpdate(Statistics statistics, BinaryOperator<Statistics> mergeOperation, int attempt) throws UnableUpdateDocumentException
    {
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
        update.set("fileName", statistics.getFileName());

        try {
            return mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().upsert(true).returnNew(true), Statistics.class);
        } catch (DuplicateKeyException ex){
            if(attempt < retryCount){
                return this.optimisticUpdate(statistics, mergeOperation, ++attempt);
            }else {
                throw new UnableUpdateDocumentException(String.format("Unable to update document: %s", statistics), ex);
            }
        }
    }

    public static class MergeStatisticsFunction implements BinaryOperator<Statistics>
    {

        @Override
        public Statistics apply(Statistics source, Statistics target)
        {
            Map<String, Long> mergedStatistics = Stream.of(source.getStatistics(), target.getStatistics())
                    .map(Map::entrySet)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Long::sum));
            return target.toBuilder().statistics(mergedStatistics).build();
        }
    }

}

package com.pehulja.thefloow.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.pehulja.thefloow.exception.UnableUpdateDocumentException;
import com.pehulja.thefloow.storage.documents.FileWordsStatistics;
import com.pehulja.thefloow.storage.documents.QueueItem;
import com.pehulja.thefloow.storage.repository.CustomFileWordsStatisticsRepository;

/**
 * Created by baske on 11.09.2017.
 */
@Service
public class DefaultStatisticsService implements StatisticsService
{
    @Autowired
    private MongoTemplate mongoTemplate;

    private Character [] WORDS_SEPARATOR = {' ', '\n'};

    @Autowired
    private CustomFileWordsStatisticsRepository customFileWordsStatisticsRepository;

    @Override
    public FileWordsStatistics processQueueItem(QueueItem queueItem) throws UnableUpdateDocumentException
    {
        Map<String, Long> chunkStatistics = Arrays.stream(queueItem
                .getFileChunk()
                .getContent()
                .split("\\W+"))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        FileWordsStatistics fileWordsStatistics = FileWordsStatistics.builder()
                .fileId(queueItem.getFileChunk().getFileId())
                .fileName(queueItem.getFileChunk().getFileName())
                .wordStatistics(chunkStatistics)
                .build();

        return customFileWordsStatisticsRepository.optimisticMerge(fileWordsStatistics, new MergeStatisticsFunction());
    }

    public static class MergeStatisticsFunction implements BinaryOperator<FileWordsStatistics>
    {

        @Override
        public FileWordsStatistics apply(FileWordsStatistics source, FileWordsStatistics target)
        {
            Map<String, Long> mergedStatistics = Stream.of(source.getWordStatistics(), target.getWordStatistics())
                    .map(Map::entrySet)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Long::sum));
            return target.toBuilder().wordStatistics(mergedStatistics).build();
        }
    }

}

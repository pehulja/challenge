package com.pehulja.thefloow.service.text_processing;

import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Function;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pehulja.thefloow.service.queue.QueueManagementService;
import com.pehulja.thefloow.service.queue.statistics.QueueStatisticsService;
import com.pehulja.thefloow.storage.documents.FileWordsStatistics;
import com.pehulja.thefloow.storage.documents.QueueItem;
import com.pehulja.thefloow.storage.repository.CustomBucketRepository;
import com.pehulja.thefloow.storage.repository.CustomFileWordsStatisticsRepository;
import com.pehulja.thefloow.storage.repository.CustomWordRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by eyevpek on 2017-09-12.
 */
@Component
@Slf4j
public class DefaultFileChunkProcessor implements FileChunkProcessor, InitializingBean
{
    private Function<String, Map<String, Long>> uniqueWordsUsageStatisticsFunction = new UniqueWordsUsageStatisticsFunction();
    private BinaryOperator<FileWordsStatistics> mergeStatisticsFunction = new MergeStatisticsFunction();

    @Autowired
    private QueueStatisticsService queueStatisticsService;

    @Autowired
    private CustomFileWordsStatisticsRepository customFileWordsStatisticsRepository;

    @Autowired
    private QueueManagementService queueManagementService;

    @Autowired
    private CustomWordRepository customWordRepository;

    @Autowired
    private CustomBucketRepository customBucketRepository;
    /**
     * Performs this operation on the given argument.
     *
     * @param queueItem
     *         the input argument
     */
    @Override
    public void accept(QueueItem queueItem)
    {
        FileWordsStatistics fileWordsStatistics = FileWordsStatistics.builder()
                .fileId(queueItem.getFileChunk().getFileId())
                .fileName(queueItem.getFileChunk().getFileName())
                .wordStatistics(uniqueWordsUsageStatisticsFunction.apply(queueItem.getFileChunk().getContent()))
                .build();

        customWordRepository.apply(fileWordsStatistics);
        //customBucketRepository.process(fileWordsStatistics);

        queueStatisticsService.incrementSuccessfullyProcessed();
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        queueManagementService.registerSubscriber(this);
    }
}

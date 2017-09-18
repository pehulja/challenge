package com.pehulja.thefloow.service.text_processing;

import com.pehulja.thefloow.service.queue.QueueManagementService;
import com.pehulja.thefloow.service.queue.statistics.QueueStatisticsService;
import com.pehulja.thefloow.storage.documents.QueueItem;
import com.pehulja.thefloow.storage.documents.Word;
import com.pehulja.thefloow.storage.repository.CustomWordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by eyevpek on 2017-09-12.
 */
@Component
@Slf4j
public class DefaultFileChunkProcessor implements FileChunkProcessor, InitializingBean {
    private Function<String, Map<String, Long>> uniqueWordsUsageStatisticsFunction = new UniqueWordsUsageStatisticsFunction();

    @Autowired
    private QueueStatisticsService queueStatisticsService;

    @Autowired
    private CustomWordRepository customWordRepository;

    @Autowired
    private QueueManagementService queueManagementService;

    /**
     * Performs this operation on the given argument.
     *
     * @param queueItem the input argument
     */
    @Override
    public void accept(QueueItem queueItem) {
        List<Word> words = uniqueWordsUsageStatisticsFunction
                .apply(queueItem.getFileChunk().getContent())
                .entrySet()
                .stream()
                .map(entry -> Word.builder().word(entry.getKey()).counter(entry.getValue()).build())
                .collect(Collectors.toList());
        try {
            customWordRepository.merge(words);
            queueStatisticsService.incrementSuccessfullyProcessed();
        } catch (Exception ex) {
            log.error("Failed to process chunk", ex);
            queueStatisticsService.incrementFailedToProcess();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        queueManagementService.registerSubscriber(this);
    }
}

package com.pehulja.thefloow.service.file_processing;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pehulja.thefloow.exception.UnableUpdateDocumentException;
import com.pehulja.thefloow.service.StatisticsService;
import com.pehulja.thefloow.service.queue.QueueManagementService;
import com.pehulja.thefloow.service.queue.statistics.QueueStatisticsService;
import com.pehulja.thefloow.storage.documents.FileWordsStatistics;
import com.pehulja.thefloow.storage.documents.QueueItem;

/**
 * Created by eyevpek on 2017-09-12.
 */
@Component
public class DefaultFileChunkProcessor implements FileChunkProcessor, InitializingBean, DisposableBean
{
    @Autowired
    private QueueManagementService queueManagementService;

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private QueueStatisticsService queueStatisticsService;

    @Value ("${queue.listener.delay}")
    private Integer fixedDelay;

    @Value ("${queue.listener.threads}")
    private Integer queueListenerThreads;

    @Value ("${queue.listener.enabled}")
    private boolean isListenerPoolingEnabled;

    private ScheduledExecutorService executorService;

    /**
     * Gets a result.
     *
     * @return a result
     */
    @Override
    public Callable<Optional<FileWordsStatistics>> get()
    {
        return new FileChunkProcessingTask();
    }

    public class FileChunkProcessingTask implements Callable<Optional<FileWordsStatistics>>
    {

        /**
         * Computes a result, or throws an exception if unable to do so.
         *
         * @return computed result
         * @throws Exception
         *         if unable to compute a result
         */
        @Override
        public Optional<FileWordsStatistics> call()
        {
            Optional<QueueItem> optionalQueueItem = queueManagementService.poll();

            return optionalQueueItem.map(queueItem ->
            {
                try
                {
                    FileWordsStatistics fileWordsStatistics = statisticsService.processQueueItem(queueItem);

                    queueStatisticsService.incrementSuccessfullyProcessed();

                    return fileWordsStatistics;
                }
                catch (UnableUpdateDocumentException ex)
                {
                    queueStatisticsService.incrementFailedToProcess();

                    throw new RuntimeException(String.format("Unable to process queue item: %s", queueItem), ex);
                }
            });
        }
    }

    @Override
    public void destroy() throws Exception
    {
        if (isListenerPoolingEnabled && executorService != null && !executorService.isShutdown())
        {
            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.SECONDS);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        if (isListenerPoolingEnabled)
        {
            executorService = Executors.newScheduledThreadPool(queueListenerThreads);
            Stream.generate(this).limit(queueListenerThreads).forEach(queueListenerTask -> executorService.schedule(queueListenerTask, fixedDelay, TimeUnit.MILLISECONDS));
        }
    }
}

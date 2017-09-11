package com.pehulja.thefloow.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.pehulja.thefloow.filereader.FileInfo;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pehulja.thefloow.filereader.FileChunk;
import com.pehulja.thefloow.queue.QueueItem;
import com.pehulja.thefloow.repository.QueueItemRepository;

/**
 * Created by eyevpek on 2017-09-11.
 */
@RunWith (SpringRunner.class)
@SpringBootTest
public class DefaultQueueServiceTest implements Supplier<QueueItem>
{
    private Random randomizer = new Random();

    @Autowired
    private DefaultQueueService defaultQueueService;

    @Autowired
    private QueueItemRepository queueItemRepository;

    @Before
    public void cleanUpStorage(){
        queueItemRepository.deleteAll();
    }

    @Test
    public void pushSingle() throws Exception
    {
        QueueItem queueItem = this.get();

        Future<QueueItem> callback = defaultQueueService.push(queueItem);
        QueueItem actual = callback.get();
        Assertions.assertThat(actual).isNotNull();
        Assertions.assertThat(actual.getId()).isNotBlank();
        Assertions.assertThat(actual.getFileChunk()).isEqualTo(queueItem.getFileChunk());

        List<QueueItem> actualAllDocuments = queueItemRepository.findAll();
        Assertions.assertThat(actualAllDocuments).hasSize(1);
        Assertions.assertThat(actualAllDocuments).containsExactly(actual);
    }

    @Test
    public void pushMany() throws Exception
    {
        Set<QueueItem> list = Stream.generate(this::get).limit(200).collect(Collectors.toSet());
        list.forEach(defaultQueueService::push);

        Thread.sleep(1000 * 30);

        List<QueueItem> actualAllDocuments = queueItemRepository.findAll();
        Assertions.assertThat(actualAllDocuments).hasSize(list.size());
        Assertions.assertThat(actualAllDocuments.stream().map(QueueItem::getFileChunk).collect(Collectors.toSet()))
                .containsAll(list.stream().map(QueueItem::getFileChunk).collect(Collectors.toSet()));
    }

    @Test
    public void pollSingleThread() throws InterruptedException
    {
        Set<QueueItem> list = Stream.generate(this::get).limit(200).collect(Collectors.toSet());
        list.forEach(defaultQueueService::push);

        Thread.sleep(1000 * 30);

        Optional<QueueItem> optional;
        List<QueueItem> actual = new ArrayList<>();

        for(optional = defaultQueueService.poll(); optional.isPresent(); optional = defaultQueueService.poll()){
            actual.add(optional.orElseThrow(() -> new IllegalArgumentException("Shouldn't be empty")));
        }

        Assertions.assertThat(actual).hasSize(list.size());
        Assertions.assertThat(actual.stream().map(QueueItem::getFileChunk).collect(Collectors.toList()))
                .containsAll(list.stream().map(QueueItem::getFileChunk).collect(Collectors.toSet()));

        Assertions.assertThat(queueItemRepository.findAll()).isEmpty();
    }

    @Test
    public void pollMultiThread() throws InterruptedException, ExecutionException
    {
        Set<QueueItem> list = Stream.generate(this::get).limit(200).collect(Collectors.toSet());
        list.forEach(defaultQueueService::push);

        Thread.sleep(1000 * 30);

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        CompletionService<List<QueueItem>> pollingExecutorService = new ExecutorCompletionService(executorService);
        List<QueueItem> joinedActualList = new ArrayList<>();

        for(int i = 0; i < 5; i++){
            pollingExecutorService.submit(() ->
            {
                Optional<QueueItem> optional;
                List<QueueItem> actual = new ArrayList<>();
                for (optional = defaultQueueService.poll(); optional.isPresent(); optional = defaultQueueService.poll())
                {
                    actual.add(optional.orElseThrow(() -> new IllegalArgumentException("Shouldn't be empty")));
                }
                return actual;
            });
        }

        for(int i = 0; i < 5; i++){
            joinedActualList.addAll(pollingExecutorService.take().get());
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
        Assertions.assertThat(queueItemRepository.findAll()).isEmpty();

        Assertions.assertThat(joinedActualList).hasSize(list.size());
        Assertions.assertThat(joinedActualList.stream().map(QueueItem::getFileChunk).collect(Collectors.toSet()))
                .containsAll(list.stream().map(QueueItem::getFileChunk).collect(Collectors.toSet()));
    }

    /**
     * Gets a result.
     *
     * @return a result
     */
    @Override
    public QueueItem get()
    {
        return QueueItem.builder()
                .fileChunk(FileChunk.builder()
                        .content("content")
                        .chunkId(randomizer.nextLong())
                        .fileInfo(
                            FileInfo.builder()
                                .fileId(UUID.randomUUID().toString())
                                .fileName(UUID.randomUUID().toString())
                                .build())
                        .build())
                .build();
    }
}
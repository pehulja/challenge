package com.pehulja.thefloow.storage.repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pehulja.thefloow.service.text_processing.MergeStatisticsFunction;
import com.pehulja.thefloow.storage.documents.FileWordsStatistics;

/**
 * Created by eyevpek on 2017-09-13.
 */
@RunWith (SpringRunner.class)
@SpringBootTest
public class CustomFileWordsStatisticsRepositoryImplTest implements Supplier<FileWordsStatistics>
{
    @Autowired
    private CustomFileWordsStatisticsRepositoryImpl customFileWordsStatisticsRepository;

    @Autowired
    private FileWordsStatisticsRepository fileWordsStatisticsRepository;

    @Test
    public void optimisticMerge() throws Exception
    {
        FileWordsStatistics fileWordsStatistics = fileWordsStatisticsRepository.save(this.get());

        fileWordsStatistics = fileWordsStatistics.toBuilder()
                .wordStatistic("a", 1l)
                .wordStatistic("b", 1l)
                .build();

        Map<String, Long> expectedStatistics = new HashMap<>();
        expectedStatistics.put("a", 2l);
        expectedStatistics.put("b", 2l);

        FileWordsStatistics updated = customFileWordsStatisticsRepository.optimisticMerge(fileWordsStatistics, new MergeStatisticsFunction());
        Assertions.assertThat(updated.getVersion()).isEqualTo(1l);
        Assertions.assertThat(updated.getWordStatistics()).isEqualTo(expectedStatistics);

        fileWordsStatistics = fileWordsStatistics.toBuilder()
                .wordStatistic("a", 3l)
                .wordStatistic("b", 3l)
                .build();

        expectedStatistics = new HashMap<>();
        expectedStatistics.put("a", 5l);
        expectedStatistics.put("b", 5l);

        updated = customFileWordsStatisticsRepository.optimisticMerge(fileWordsStatistics, new MergeStatisticsFunction());

        Assertions.assertThat(updated.getVersion()).isEqualTo(2l);
        Assertions.assertThat(updated.getWordStatistics()).isEqualTo(expectedStatistics);
    }

    @Test
    public void optimisticMergeExisting() throws Exception
    {
        FileWordsStatistics fileWordsStatistics = fileWordsStatisticsRepository.save(this.get());

        fileWordsStatistics = fileWordsStatistics.toBuilder()
                .wordStatistic("a", 1l)
                .wordStatistic("b", 1l)
                .build();

        customFileWordsStatisticsRepository.optimisticMerge(fileWordsStatistics);

        FileWordsStatistics expected = fileWordsStatistics.toBuilder()
                .wordStatistic("a", 2l)
                .wordStatistic("b", 2l)
                .build();

        Assertions.assertThat(fileWordsStatisticsRepository.findAll()).contains(expected);
    }


    @Test
    public void optimisticMergeNew() throws Exception
    {
        FileWordsStatistics fileWordsStatistics = this.get();

        customFileWordsStatisticsRepository.optimisticMerge(fileWordsStatistics);

        FileWordsStatistics expected = fileWordsStatistics.toBuilder()
                .wordStatistic("a", 1l)
                .wordStatistic("b", 1l)
                .build();

        Assertions.assertThat(fileWordsStatisticsRepository.findAll()).contains(expected);
    }


    @Test
    public void optimisticMergeBulk() throws Exception
    {
        String fileName = "a";
        String fileId = "b";

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for(int i = 0; i < 700; i++){
            executorService.submit(() -> {

                FileWordsStatistics.FileWordsStatisticsBuilder builder = FileWordsStatistics.builder();
                builder.fileId(fileId).fileName(fileName);
                for(int j = 0; j < 10000; j++){
                    builder.wordStatistic(String.valueOf(j), 1l);
                }
                customFileWordsStatisticsRepository.optimisticMerge(builder.build());
                System.err.println("INTHEMETROD");

            });
        }

        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.MINUTES);

        FileWordsStatistics.FileWordsStatisticsBuilder builder = FileWordsStatistics.builder();
        builder.fileId(fileId).fileName(fileName);
        for(int j = 0; j < 10000; j++){
            builder.wordStatistic(String.valueOf(j), 10l);
        }

        FileWordsStatistics expected = builder.build();

        Assertions.assertThat(fileWordsStatisticsRepository.findAll()).contains(expected);
    }

    @Override
    public FileWordsStatistics get()
    {
        return FileWordsStatistics.builder()
                .fileId(UUID.randomUUID().toString())
                .fileName(UUID.randomUUID().toString())
                .wordStatistic("a", 1l)
                .wordStatistic("b", 1l)
                .build();
    }
}
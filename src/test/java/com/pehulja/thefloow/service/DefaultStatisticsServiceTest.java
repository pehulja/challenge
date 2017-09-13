package com.pehulja.thefloow.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pehulja.thefloow.storage.documents.FileWordsStatistics;
import com.pehulja.thefloow.storage.repository.FileWordsStatisticsRepository;

/**
 * Created by baske on 12.09.2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DefaultStatisticsServiceTest implements Supplier<FileWordsStatistics>
{
    private Random random = new Random();

    @Autowired
    private FileWordsStatisticsRepository fileWordsStatisticsRepository;

    @Autowired
    private DefaultStatisticsService defaultStatisticsService;

    @Test
    public void optimisticUpdate() throws Exception {
        FileWordsStatistics fileWordsStatistics = fileWordsStatisticsRepository.save(this.get());

        fileWordsStatistics = fileWordsStatistics.toBuilder()
                .statistic("a", 1l)
                .statistic("b", 1l)
        .build();

        Map<String, Long> expectedStatistics = new HashMap<>();
        expectedStatistics.put("a", 2l);
        expectedStatistics.put("b", 2l);

        FileWordsStatistics updated = defaultStatisticsService.optimisticUpdate(fileWordsStatistics, new DefaultStatisticsService.MergeStatisticsFunction());
        Assertions.assertThat(updated.getVersion()).isEqualTo(1l);
        Assertions.assertThat(updated.getWordStatistics()).isEqualTo(expectedStatistics);

        fileWordsStatistics = fileWordsStatistics.toBuilder()
                .statistic("a", 3l)
                .statistic("b", 3l)
                .build();

        expectedStatistics = new HashMap<>();
        expectedStatistics.put("a", 5l);
        expectedStatistics.put("b", 5l);

        updated = defaultStatisticsService.optimisticUpdate(fileWordsStatistics, new DefaultStatisticsService.MergeStatisticsFunction());

        Assertions.assertThat(updated.getVersion()).isEqualTo(2l);
        Assertions.assertThat(updated.getWordStatistics()).isEqualTo(expectedStatistics);
    }

    @Override
    public FileWordsStatistics get()
    {
        return FileWordsStatistics.builder()
                .fileId(UUID.randomUUID().toString())
                .fileName(UUID.randomUUID().toString())
                .statistic("a", 1l)
                .statistic("b", 1l)
                .build();
    }
}
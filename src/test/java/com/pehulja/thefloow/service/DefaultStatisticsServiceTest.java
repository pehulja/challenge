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

import com.pehulja.thefloow.repository.StatisticsRepository;
import com.pehulja.thefloow.statistics.Statistics;

/**
 * Created by baske on 12.09.2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class DefaultStatisticsServiceTest implements Supplier<Statistics> {
    private Random random = new Random();

    @Autowired
    private StatisticsRepository statisticsRepository;

    @Autowired
    private DefaultStatisticsService defaultStatisticsService;

    @Test
    public void optimisticUpdate() throws Exception {
        Statistics statistics = statisticsRepository.save(this.get());

        statistics = statistics.toBuilder()
                .statistic("a", 1l)
                .statistic("b", 1l)
        .build();

        Map<String, Long> expectedStatistics = new HashMap<>();
        expectedStatistics.put("a", 2l);
        expectedStatistics.put("b", 2l);

        Statistics updated = defaultStatisticsService.optimisticUpdate(statistics, new DefaultStatisticsService.MergeStatisticsFunction());
        Assertions.assertThat(updated.getVersion()).isEqualTo(1l);
        Assertions.assertThat(updated.getStatistics()).isEqualTo(expectedStatistics);

        statistics = statistics.toBuilder()
                .statistic("a", 3l)
                .statistic("b", 3l)
                .build();

        expectedStatistics = new HashMap<>();
        expectedStatistics.put("a", 5l);
        expectedStatistics.put("b", 5l);

        updated = defaultStatisticsService.optimisticUpdate(statistics, new DefaultStatisticsService.MergeStatisticsFunction());

        Assertions.assertThat(updated.getVersion()).isEqualTo(2l);
        Assertions.assertThat(updated.getStatistics()).isEqualTo(expectedStatistics);
    }

    @Override
    public Statistics get() {
        return Statistics.builder()
                .fileId(UUID.randomUUID().toString())
                .fileName(UUID.randomUUID().toString())
                .statistic("a", 1l)
                .statistic("b", 1l)
                .build();
    }
}
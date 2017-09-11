package com.pehulja.thefloow.repository;

import com.pehulja.thefloow.filereader.FileInfo;
import com.pehulja.thefloow.statistics.Statistics;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/**
 * Created by baske on 11.09.2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class StatisticsRepositoryTest implements Supplier<Statistics> {
    private Random random = new Random();

    @Autowired
    private StatisticsRepository statisticsRepository;

    @Before
    public void cleanUpStorage(){
        statisticsRepository.deleteAll();
    }

    @Test
    public void findByFileId() throws Exception {
        List<Statistics> expectedAll = Stream.generate(this).limit(5).map(statisticsRepository::insert).collect(Collectors.toList());
        List<Statistics> actualAll = statisticsRepository.findAll();

        Assertions.assertThat(actualAll).hasSameSizeAs(expectedAll);
        Assertions.assertThat(actualAll).containsAll(expectedAll);

        Statistics expected = expectedAll.get(0);
        Statistics actual = statisticsRepository.findOne(expected.getFileId());
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void insertVersionIncrement(){
        Statistics statistics = statisticsRepository.save(this.get());
        Assertions.assertThat(statistics.getVersion()).isEqualTo(0l);

        statistics.setFileName("updatedFileName");
        statistics = statisticsRepository.save(statistics);
        Assertions.assertThat(statistics.getVersion()).isEqualTo(1l);
    }

    @Override
    public Statistics get() {
        return Statistics.builder()
                .fileId(UUID.randomUUID().toString())
                .fileName(UUID.randomUUID().toString())
                .statistic(UUID.randomUUID().toString(), random.nextLong())
                .statistic(UUID.randomUUID().toString(), random.nextLong())
                .build();
    }
}
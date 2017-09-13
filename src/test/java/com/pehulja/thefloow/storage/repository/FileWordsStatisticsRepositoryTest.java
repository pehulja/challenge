package com.pehulja.thefloow.storage.repository;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pehulja.thefloow.AbstractTestWithMongo;
import com.pehulja.thefloow.storage.documents.FileWordsStatistics;

/**
 * Created by baske on 11.09.2017.
 */
@RunWith (SpringRunner.class)
@SpringBootTest
public class FileWordsStatisticsRepositoryTest extends AbstractTestWithMongo implements Supplier<FileWordsStatistics>
{
    private Random random = new Random();

    @Autowired
    private FileWordsStatisticsRepository fileWordsStatisticsRepository;

    @Test
    public void findByFileId() throws Exception
    {
        List<FileWordsStatistics> expectedAll = Stream.generate(this).limit(5).map(fileWordsStatisticsRepository::insert).collect(Collectors.toList());
        List<FileWordsStatistics> actualAll = fileWordsStatisticsRepository.findAll();

        Assertions.assertThat(actualAll).hasSameSizeAs(expectedAll);
        Assertions.assertThat(actualAll).containsAll(expectedAll);

        FileWordsStatistics expected = expectedAll.get(0);
        FileWordsStatistics actual = fileWordsStatisticsRepository.findOne(expected.getFileId());
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void insertVersionIncrement()
    {
        FileWordsStatistics fileWordsStatistics = fileWordsStatisticsRepository.save(this.get());
        Assertions.assertThat(fileWordsStatistics.getVersion()).isEqualTo(0l);

        fileWordsStatistics.setFileName("updatedFileName");
        fileWordsStatistics = fileWordsStatisticsRepository.save(fileWordsStatistics);
        Assertions.assertThat(fileWordsStatistics.getVersion()).isEqualTo(1l);
    }

    @Test
    public void findByFileNamePresent()
    {
        FileWordsStatistics fileWordsStatistics = fileWordsStatisticsRepository.save(this.get());
        List<FileWordsStatistics> actual = fileWordsStatisticsRepository.findByFileName(fileWordsStatistics.getFileName());
        Assertions.assertThat(actual).containsOnly(fileWordsStatistics);
    }

    @Test
    public void findByFileNameAbsent()
    {
        fileWordsStatisticsRepository.save(this.get());
        List<FileWordsStatistics> actual = fileWordsStatisticsRepository.findByFileName("anyFileName");
        Assertions.assertThat(actual).isEmpty();
    }

    @Override
    public FileWordsStatistics get()
    {
        return FileWordsStatistics.builder()
                .fileId(UUID.randomUUID().toString())
                .fileName(UUID.randomUUID().toString())
                .wordStatistic(UUID.randomUUID().toString(), random.nextLong())
                .wordStatistic(UUID.randomUUID().toString(), random.nextLong())
                .build();
    }
}
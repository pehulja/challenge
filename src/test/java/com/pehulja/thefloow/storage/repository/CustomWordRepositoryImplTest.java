package com.pehulja.thefloow.storage.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pehulja.thefloow.AbstractTestWithMongo;
import com.pehulja.thefloow.storage.documents.FileWordsStatistics;
import com.pehulja.thefloow.storage.documents.Word;

/**
 * Created by eyevpek on 2017-09-14.
 */
@RunWith (SpringRunner.class)
@SpringBootTest
public class CustomWordRepositoryImplTest extends AbstractTestWithMongo
{
    public static final String FILE_NAME = "fileName";
    public static final String FILE_ID = "fileId";

    @Autowired
    public CustomWordRepositoryImpl customWordRepository;

    @Autowired
    public WordRepository wordRepository;

    @Test
    public void applyUpdateExisting() throws Exception
    {
        Map<String, Long> input = new HashMap<>();
        List<Word> expected = new ArrayList<>();
        List<Word> existing = new ArrayList<>();

        for (long i = 0; i < 1000; i++)
        {
            input.put(String.valueOf(i), i);
            expected.add(Word.builder().fileId(FILE_ID).fileName(FILE_NAME).word(String.valueOf(i)).counter(i * 2).build());
            existing.add(Word.builder().fileId(FILE_ID).fileName(FILE_NAME).word(String.valueOf(i)).counter(i).build());
        }

        FileWordsStatistics inputFileWordsStatistics = FileWordsStatistics.builder()
                .wordStatistics(input)
                .fileId(FILE_ID)
                .fileName(FILE_NAME).build();

        wordRepository.save(existing);

        customWordRepository.apply(inputFileWordsStatistics);

        List<Word> actual = wordRepository.findAll();
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void applyInsertOnEmpty() throws Exception
    {
        Map<String, Long> input = new HashMap<>();
        Set<Word> expected = new HashSet<>();

        for (long i = 0; i < 1000; i++)
        {
            input.put(String.valueOf(i), i);
            expected.add(Word.builder().fileId(FILE_ID).fileName(FILE_NAME).word(String.valueOf(i)).counter(i).build());
        }

        FileWordsStatistics inputFileWordsStatistics = FileWordsStatistics.builder()
                .wordStatistics(input)
                .fileId(FILE_ID)
                .fileName(FILE_NAME).build();

        customWordRepository.apply(inputFileWordsStatistics);

        Set<Word> actual = new HashSet<>(wordRepository.findAll());
        Assertions.assertThat(actual).containsAll(expected);
    }
}
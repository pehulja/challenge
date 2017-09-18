package com.pehulja.thefloow.storage.repository;

import com.pehulja.thefloow.AbstractTestWithMongo;
import com.pehulja.thefloow.storage.documents.Word;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by baske on 17.09.2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")

public class CustomWordRepositoryImplTest extends AbstractTestWithMongo {
    @Autowired
    private WordRepository wordRepository;

    @Autowired
    private CustomWordRepository customWordRepository;

    @Before
    public void backloadData() {
        List<Word> words = new ArrayList<>();
        words.add(Word.builder().word("a").counter(1l).build());
        words.add(Word.builder().word("n").counter(1l).build());
        words.add(Word.builder().word("b").counter(2l).build());
        words.add(Word.builder().word("c").counter(3l).build());
        wordRepository.save(words);
    }

    @Test
    public void merge() throws Exception {
        List<Word> input = new ArrayList<>();
        input.add(Word.builder().word("a").counter(1l).build());
        input.add(Word.builder().word("b").counter(2l).build());
        input.add(Word.builder().word("c").counter(3l).build());
        input.add(Word.builder().word("d").counter(4l).build());

        List<Word> expected = new ArrayList<>();
        expected.add(Word.builder().word("a").counter(2l).build());
        expected.add(Word.builder().word("b").counter(4l).build());
        expected.add(Word.builder().word("c").counter(6l).build());
        expected.add(Word.builder().word("d").counter(4l).build());
        expected.add(Word.builder().word("n").counter(1l).build());


        customWordRepository.merge(input);
        List<Word> actual = wordRepository.findAll();
        Assertions.assertThat(actual).containsOnlyElementsOf(expected);
    }

    @Test
    public void mergeMultithread() throws Exception {
        ExecutorService scheduledExecutorService = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 10; i++) {
            scheduledExecutorService.submit(() -> {
                List<Word> input = new ArrayList<>();
                input.add(Word.builder().word("a").counter(1l).build());
                input.add(Word.builder().word("e").counter(2l).build());
                input.add(Word.builder().word("f").counter(3l).build());
                input.add(Word.builder().word("g").counter(1l).build());

                customWordRepository.merge(input);
            });
        }

        scheduledExecutorService.shutdown();
        scheduledExecutorService.awaitTermination(1, TimeUnit.MINUTES);

        List<Word> expected = new ArrayList<>();
        expected.add(Word.builder().word("a").counter(11l).build());
        expected.add(Word.builder().word("b").counter(2l).build());
        expected.add(Word.builder().word("c").counter(3l).build());
        expected.add(Word.builder().word("e").counter(20l).build());
        expected.add(Word.builder().word("f").counter(30l).build());
        expected.add(Word.builder().word("g").counter(10l).build());
        expected.add(Word.builder().word("n").counter(1l).build());


        List<Word> actual = wordRepository.findAll();
        Assertions.assertThat(actual).containsOnlyElementsOf(expected);
    }

    @Test
    public void findMax() throws Exception {
        Optional<Long> expected = Optional.of(3l);
        Optional<Long> actual = customWordRepository.findMax();
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void findEmpty() throws Exception {
        wordRepository.deleteAll();

        Optional<Long> expected = Optional.empty();
        Optional<Long> actual = customWordRepository.findMin();
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void findMin() throws Exception {
        Optional<Long> expected = Optional.of(1l);
        Optional<Long> actual = customWordRepository.findMin();
        Assertions.assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void findWordsByCountSingle() throws Exception {
        Set<String> expected = new HashSet<>();
        expected.add("c");
        Set<String> actual = customWordRepository.findWordsByCount(3l);
        Assertions.assertThat(actual).containsOnlyElementsOf(expected);
    }

    @Test
    public void findWordsByCountMultiple() throws Exception {
        Set<String> expected = new HashSet<>();
        expected.add("a");
        expected.add("n");
        Set<String> actual = customWordRepository.findWordsByCount(1l);
        Assertions.assertThat(actual).containsOnlyElementsOf(expected);
    }
}
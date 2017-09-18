package com.pehulja.thefloow.storage.repository;

import com.pehulja.thefloow.storage.documents.Word;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by eyevpek on 2017-09-14.
 */
public interface CustomWordRepository {
    void merge(List<Word> wordList);

    Optional<Long> findMax();

    Optional<Long> findMin();

    Set<String> findWordsByCount(Long counter);
}

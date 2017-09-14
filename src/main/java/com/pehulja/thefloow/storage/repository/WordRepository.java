package com.pehulja.thefloow.storage.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.pehulja.thefloow.storage.documents.Word;

/**
 * Created by eyevpek on 2017-09-14.
 */
public interface WordRepository extends MongoRepository<Word, String>
{
}

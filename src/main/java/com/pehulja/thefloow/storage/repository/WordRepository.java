package com.pehulja.thefloow.storage.repository;

import com.pehulja.thefloow.storage.documents.Word;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by eyevpek on 2017-09-14.
 */
public interface WordRepository extends MongoRepository<Word, String> {
}

package com.pehulja.thefloow;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Created by eyevpek on 2017-09-12.
 */
public abstract class AbstractTestWithMongo
{
    @Autowired
    private MongoTemplate mongoTemplate;

    @Before
    public void cleanupInmemoryMongoDatabase()
    {
        mongoTemplate.getDb().dropDatabase();
    }
}

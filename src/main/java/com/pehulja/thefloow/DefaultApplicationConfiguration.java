package com.pehulja.thefloow;

import com.mongodb.Mongo;
import com.mongodb.MongoClientURI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.MongoClientFactoryBean;
import org.springframework.scheduling.annotation.EnableAsync;

import java.net.UnknownHostException;

/**
 * Created by eyevpek on 2017-09-11.
 */
@Configuration
@EnableAsync
public class DefaultApplicationConfiguration
{
}

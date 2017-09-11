package com.pehulja.thefloow;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Created by eyevpek on 2017-09-11.
 */
@Configuration
@EnableAsync
@EnableMongoAuditing
public class ApplicationConfiguration
{
}

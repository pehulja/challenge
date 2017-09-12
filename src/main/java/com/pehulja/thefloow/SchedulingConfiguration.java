package com.pehulja.thefloow;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Created by eyevpek on 2017-09-12.
 */
@Configuration
@ConditionalOnProperty (value = "queue.listener.enabled", havingValue = "true", matchIfMissing = true)
@EnableScheduling
public class SchedulingConfiguration
{
}

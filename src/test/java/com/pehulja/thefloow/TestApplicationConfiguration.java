package com.pehulja.thefloow;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Created by baske on 17.09.2017.
 */
@Configuration
@EnableAsync
@Profile("test")
public class TestApplicationConfiguration {
}

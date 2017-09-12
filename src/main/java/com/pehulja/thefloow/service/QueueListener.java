package com.pehulja.thefloow.service;

import java.util.Optional;

import com.pehulja.thefloow.statistics.Statistics;

/**
 * Created by eyevpek on 2017-09-12.
 */
public interface QueueListener
{
    Optional<Statistics> processQueueItem();
}

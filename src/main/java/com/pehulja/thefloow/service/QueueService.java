package com.pehulja.thefloow.service;

import java.util.Optional;
import java.util.concurrent.Future;

import com.pehulja.thefloow.queue.QueueItem;

/**
 * Created by eyevpek on 2017-09-11.
 */
public interface QueueService
{
    Future<QueueItem> push(QueueItem queueItem);

    Optional<QueueItem> poll();
}

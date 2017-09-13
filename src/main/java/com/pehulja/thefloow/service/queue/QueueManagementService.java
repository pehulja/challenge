package com.pehulja.thefloow.service.queue;

import java.util.concurrent.Future;
import java.util.function.Consumer;

import com.pehulja.thefloow.storage.documents.QueueItem;

/**
 * Created by eyevpek on 2017-09-11.
 */
public interface QueueManagementService
{
    Future<QueueItem> push(QueueItem queueItem);

    void registerSubscriber(Consumer<QueueItem> subscriber);
}

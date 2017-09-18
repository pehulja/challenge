package com.pehulja.thefloow.service.queue;

import com.pehulja.thefloow.storage.documents.QueueItem;

import java.util.concurrent.Future;
import java.util.function.Consumer;

/**
 * Created by eyevpek on 2017-09-11.
 */
public interface QueueManagementService {
    Future<QueueItem> push(QueueItem queueItem);

    void registerSubscriber(Consumer<QueueItem> subscriber);
}

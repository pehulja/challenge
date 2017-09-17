package com.pehulja.thefloow.storage.repository;

import com.pehulja.thefloow.storage.documents.QueueItem;

import java.util.Optional;

/**
 * Created by baske on 17.09.2017.
 */
public interface CustomQueueItemRepository {
    Optional<QueueItem> poll();
}

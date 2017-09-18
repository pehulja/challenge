package com.pehulja.thefloow.service.queue;

import com.pehulja.thefloow.storage.documents.QueueItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

/**
 * Jut another one subscriber that may log polled queue items
 */
@Service
@Slf4j
public class QueueItemLogger implements Consumer<QueueItem>, InitializingBean {
    private static final String MESSAGE_TEMPLATE = "Queue item polled: %s";

    @Autowired
    private QueueManagementService queueManagementService;

    /**
     * Performs this operation on the given argument.
     *
     * @param queueItem the input argument
     */
    @Override
    public void accept(QueueItem queueItem) {
        log.debug(String.format(MESSAGE_TEMPLATE, queueItem.toString()));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        queueManagementService.registerSubscriber(this);
    }
}

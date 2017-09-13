package com.pehulja.thefloow.service.queue;

import java.util.function.Consumer;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pehulja.thefloow.storage.documents.QueueItem;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by eyevpek on 2017-09-13.
 */
@Service
@Slf4j
public class QueueItemLogger implements Consumer<QueueItem>, InitializingBean
{
    private static final String MESSAGE_TEMPLATE = "Queue item polled: %s";

    @Autowired
    private QueueManagementService queueManagementService;

    /**
     * Performs this operation on the given argument.
     *
     * @param queueItem
     *         the input argument
     */
    @Override
    public void accept(QueueItem queueItem)
    {
        log.info(String.format(MESSAGE_TEMPLATE, queueItem.toString()));
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        queueManagementService.registerSubscriber(this);
    }
}

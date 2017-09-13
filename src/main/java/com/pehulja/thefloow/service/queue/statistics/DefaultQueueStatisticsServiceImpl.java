package com.pehulja.thefloow.service.queue.statistics;

import static com.pehulja.thefloow.storage.documents.QueueStatistics.FAILED;
import static com.pehulja.thefloow.storage.documents.QueueStatistics.PUSHED;
import static com.pehulja.thefloow.storage.documents.QueueStatistics.SUCCESS;

import java.util.Optional;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.pehulja.thefloow.storage.documents.QueueStatistics;
import com.pehulja.thefloow.storage.repository.QueueStatisticsRepository;

/**
 * Created by eyevpek on 2017-09-12.
 */
@Service
public class DefaultQueueStatisticsServiceImpl implements QueueStatisticsService, InitializingBean
{
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private QueueStatisticsRepository queueStatisticsRepository;

    public static final String QUEUE_STATISTICS_DOC_ID = "0d0d11e4-97a6-11e7-abc4-cec278b6b50a";

    private static final QueueStatistics DEFAULT_STATISTICS = QueueStatistics.builder()
            .id(QUEUE_STATISTICS_DOC_ID)
            .failedToProcess(0l)
            .pushedToQueue(0l)
            .successfullyProcessed(0l)
            .possibleLostOrInProgress(0l)
            .build();

    @Override
    public QueueStatistics incrementQueue()
    {
        return incrementField(PUSHED);
    }

    @Override
    public QueueStatistics incrementSuccessfullyProcessed()
    {
        return incrementField(SUCCESS);
    }

    @Override
    public QueueStatistics incrementFailedToProcess()
    {
        return incrementField(FAILED);
    }

    @Override
    public QueueStatistics getQueueStatistics()
    {
        return Optional.ofNullable(queueStatisticsRepository.findAll().get(0)).map(queueStatistics ->
        {
            queueStatistics.setPossibleLostOrInProgress(queueStatistics.getPushedToQueue() - queueStatistics.getSuccessfullyProcessed() - queueStatistics.getFailedToProcess());
            return queueStatistics;
        }).orElse(DEFAULT_STATISTICS);
    }

    private QueueStatistics incrementField(String fieldName)
    {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(QUEUE_STATISTICS_DOC_ID));

        Update update = new Update();
        update.inc(fieldName, 1);

        return mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().upsert(true).returnNew(true), QueueStatistics.class);
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        try
        {
            queueStatisticsRepository.insert(DEFAULT_STATISTICS);
        }
        catch (DuplicateKeyException | com.mongodb.DuplicateKeyException ex)
        {
            //ignore
            // TODO: Fix it
        }
    }
}

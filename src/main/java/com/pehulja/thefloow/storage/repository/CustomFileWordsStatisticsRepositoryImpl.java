package com.pehulja.thefloow.storage.repository;

import java.util.Optional;
import java.util.function.BinaryOperator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.pehulja.thefloow.exception.UnableUpdateDocumentException;
import com.pehulja.thefloow.storage.documents.FileWordsStatistics;

/**
 * Created by eyevpek on 2017-09-13.
 */
@Repository
public class CustomFileWordsStatisticsRepositoryImpl implements CustomFileWordsStatisticsRepository
{
    @Autowired
    private FileWordsStatisticsRepository fileWordsStatisticsRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Value ("${mongo.retry-usageCounter}")
    private Integer retryCount;

    @Override
    public FileWordsStatistics optimisticMerge(FileWordsStatistics fileWordsStatistics, BinaryOperator<FileWordsStatistics> mergeOperation) throws UnableUpdateDocumentException
    {
        return this.optimisticMerge(fileWordsStatistics, mergeOperation, 0);
    }

    private FileWordsStatistics optimisticMerge(FileWordsStatistics fileWordsStatistics, BinaryOperator<FileWordsStatistics> mergeOperation, int attempt) throws UnableUpdateDocumentException
    {
        FileWordsStatistics existing = fileWordsStatisticsRepository.findOne(fileWordsStatistics.getFileId());
        Query query = new Query();
        query.addCriteria(Criteria.where("fileId").is(fileWordsStatistics.getFileId()));
        query.addCriteria(Criteria.where("version").is(Optional.ofNullable(existing).map(FileWordsStatistics::getVersion).orElse(1l)));
        query.limit(1);

        Update update = new Update();
        update.set("wordStatistics", Optional.ofNullable(existing)
                .map(existingStatistics -> mergeOperation.apply(existingStatistics, fileWordsStatistics))
                .orElse(fileWordsStatistics)
                .getWordStatistics());
        update.set("fileName", fileWordsStatistics.getFileName());

        try
        {
            FileWordsStatistics result = mongoTemplate.findAndModify(query, update, FindAndModifyOptions.options().upsert(true).returnNew(true), FileWordsStatistics.class);
            return result;
        }
        catch (DuplicateKeyException ex)
        {
            if (attempt < retryCount)
            {
                return this.optimisticMerge(fileWordsStatistics, mergeOperation, ++attempt);
            }
            else
            {
                throw new UnableUpdateDocumentException(String.format("Unable to update document: %s", fileWordsStatistics.getFileName()), ex);
            }
        }
    }

}

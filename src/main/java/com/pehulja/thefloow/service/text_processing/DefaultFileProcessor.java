package com.pehulja.thefloow.service.text_processing;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pehulja.thefloow.exception.UnableProcessChunkException;
import com.pehulja.thefloow.filereader.InputFileReader;
import com.pehulja.thefloow.service.queue.QueueManagementService;
import com.pehulja.thefloow.service.queue.statistics.QueueStatisticsService;
import com.pehulja.thefloow.storage.documents.QueueItem;

/**
 * Created by eyevpek on 2017-09-12.
 */
@Service
public class DefaultFileProcessor implements FileProcessor
{
    @Autowired
    private InputFileReader inputFileReader;

    @Autowired
    private QueueManagementService queueManagementService;

    @Autowired
    private QueueStatisticsService queueStatisticsService;

    @Override
    public void processFile(String filePath) throws FileNotFoundException, UnableProcessChunkException
    {
        Path path = Paths.get(filePath);
        if (Files.notExists(path))
        {
            throw new FileNotFoundException(String.format("File %s doesn't exists or can't be read by application", filePath));
        }

        try
        {
            inputFileReader.chunksProcessor(path, fileChunk ->
            {
                queueManagementService.push(QueueItem.builder().fileChunk(fileChunk).build());
                queueStatisticsService.incrementQueue();
            });
        }
        catch (Exception ex)
        {
            throw new UnableProcessChunkException(String.format("Unable to read file %s: cause %s", filePath, ex.getMessage()), ex);
        }
    }
}

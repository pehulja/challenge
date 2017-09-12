package com.pehulja.thefloow.service;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pehulja.thefloow.UnableProcessFileException;
import com.pehulja.thefloow.filereader.InputFileReader;
import com.pehulja.thefloow.queue.QueueItem;

/**
 * Created by eyevpek on 2017-09-12.
 */
@Service
public class DefaultFileProcessor implements FileProcessor
{
    @Autowired
    private InputFileReader inputFileReader;

    @Autowired
    private QueueService queueService;

    @Autowired
    private QueueStatisticsService queueStatisticsService;

    @Override
    public void processFile(String filePath) throws FileNotFoundException, UnableProcessFileException
    {
        Path path = Paths.get(filePath);
        if (Files.notExists(path))
        {
            throw new FileNotFoundException(String.format("File %s doesn't exists or can't be read by application"));
        }

        try
        {
            inputFileReader.chunksProcessor(path, fileChunk ->
            {
                queueService.push(QueueItem.builder().fileChunk(fileChunk).build());
                queueStatisticsService.incrementQueue();
            });
        }
        catch (Exception ex)
        {
            throw new UnableProcessFileException(String.format("Unable to read file %s: cause %s", filePath, ex.getMessage()), ex);
        }
    }
}

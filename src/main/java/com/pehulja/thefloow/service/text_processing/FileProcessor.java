package com.pehulja.thefloow.service.text_processing;

import java.io.FileNotFoundException;

import com.pehulja.thefloow.exception.UnableProcessFileException;

/**
 * Created by eyevpek on 2017-09-12.
 */
public interface FileProcessor
{
    void processFile(String filePath) throws FileNotFoundException, UnableProcessFileException;
}

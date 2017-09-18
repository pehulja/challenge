package com.pehulja.thefloow.service.text_processing;

import com.pehulja.thefloow.exception.UnableProcessChunkException;

import java.io.FileNotFoundException;

/**
 * Created by eyevpek on 2017-09-12.
 */
public interface FileProcessor {
    void processFile(String filePath) throws FileNotFoundException, UnableProcessChunkException;
}

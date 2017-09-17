package com.pehulja.thefloow.filereader;

import java.nio.file.Path;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import com.pehulja.thefloow.storage.documents.FileChunk;

/**
 * Created by eyevpek on 2017-09-11.
 */
public interface InputFileReader
{
    Future<?> chunksProcessor(Path file, Consumer<FileChunk> fileChunkConsumer) throws Exception;
}

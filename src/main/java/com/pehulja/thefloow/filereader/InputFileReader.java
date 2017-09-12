package com.pehulja.thefloow.filereader;

import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * Created by eyevpek on 2017-09-11.
 */
public interface InputFileReader
{
    void chunksProcessor(Path file, Consumer<FileChunk> fileChunkConsumer) throws Exception;
}

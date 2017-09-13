package com.pehulja.thefloow.service.file_processing;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Supplier;

import com.pehulja.thefloow.storage.documents.FileWordsStatistics;

/**
 * Created by eyevpek on 2017-09-12.
 */
public interface FileChunkProcessor extends Supplier<Callable<Optional<FileWordsStatistics>>>
{
}

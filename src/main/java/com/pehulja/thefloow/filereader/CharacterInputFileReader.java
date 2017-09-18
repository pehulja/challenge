package com.pehulja.thefloow.filereader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.pehulja.thefloow.storage.documents.FileChunk;

/**
 * Created by eyevpek on 2017-09-11.
 */
@Slf4j
@Service
public class CharacterInputFileReader implements InputFileReader, InitializingBean, DisposableBean
{
    public static final int CHARACTER_NOT_FOUND_MARKER = -1;
    @Autowired
    private ChunkSplitPolicy chunkSplitPolicy;

    @Value ("${chunk.max-size}")
    private Long maxChunkSize;

    private static final char UNDESCORE = '_';
    private static final char SINGLE_QUOTE = '\'';

    private ExecutorService executorService;

    @Override
    public Future<?> chunksProcessor(Path path, Consumer<FileChunk> fileChunkConsumer) throws IOException
    {
        final String FILE_NAME = path.getFileName().toString();
        final File file = path.toFile();

        if(!file.exists() || !file.canRead()){
            throw new IOException(String.format("File '%s' doesn't exsit or cannot be read", file.getAbsolutePath()));
        }

        return executorService.submit(() -> {
            try (FileReader reader = new FileReader(path.toFile())) {
                StringBuilder charAccumulator = new StringBuilder();
                long chunkId = 0;
                int tempChar;

                while ((tempChar = reader.read()) != -1 && !Thread.currentThread().isInterrupted()) {
                    charAccumulator.append((char) tempChar);

                    if (chunkSplitPolicy.test(charAccumulator)) {
                        String fileChunk = charAccumulator.toString();
                        int splitIndex = this.lastIndexOfNonLetterOrDigit(fileChunk);
                        if(splitIndex == CHARACTER_NOT_FOUND_MARKER){
                            throw new IllegalArgumentException(String.format("Unable to find any word separator among in the string '%s'", fileChunk));
                        }
                        processChunk(FILE_NAME, chunkId++, fileChunk.substring(0, splitIndex), fileChunkConsumer);
                        charAccumulator = new StringBuilder(fileChunk.substring(splitIndex));
                    }
                }

                processChunk(FILE_NAME, chunkId, charAccumulator.toString(), fileChunkConsumer);
            } catch (IOException ex) {
                log.error(String.format("Unable to process file '%s'", file.getAbsolutePath()), ex);
            }
        });
    }

    private void processChunk(String fileName, Long chunkId, String chunkContent, Consumer<FileChunk> fileChunkConsumer){
        fileChunkConsumer.accept(FileChunk.builder()
                .chunkId(chunkId)
                .content(chunkContent)
                .fileName(fileName)
                .build());
    }

    @Override
    public void destroy() throws Exception {
        executorService.shutdown();

        if(!executorService.awaitTermination(1, TimeUnit.MINUTES)){
            executorService.shutdownNow();
        };
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        executorService = Executors.newSingleThreadExecutor();
    }

    public int lastIndexOfNonLetterOrDigit(String str)
    {
        for (int i = str.length() - 1; i >= 0; i--){
            char testChar = str.charAt(i);
            if(Character.isLetterOrDigit(testChar) == false && UNDESCORE != testChar && SINGLE_QUOTE != testChar){
                return i;
            }
        }

        return -1;
    }
}

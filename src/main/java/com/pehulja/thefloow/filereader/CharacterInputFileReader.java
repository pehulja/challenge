package com.pehulja.thefloow.filereader;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by eyevpek on 2017-09-11.
 */
@Service
public class CharacterInputFileReader implements InputFileReader
{
    @Autowired
    private ChunkSplitPolicy chunkSplitPolicy;

    private Character [] WORDS_SEPARATOR = {' ', '\n'};

    @Override
    public void chunksProcessor(Path file, long maxChunkSize, Consumer<FileChunk> fileChunkConsumer) throws IOException
    {
        final String FILE_NAME = file.getFileName().toString();
        final String FILE_ID = UUID.randomUUID().toString();

        try(FileReader reader = new FileReader(file.toFile()))
        {
            StringBuilder charAccumulator = new StringBuilder();
            long chunkId = 0;

            int tempChar;
            while((tempChar=reader.read())!= -1){
                charAccumulator.append((char)tempChar);

                if(chunkSplitPolicy.test(charAccumulator)){
                    String fileChunk = charAccumulator.toString();
                    int splitIndex = Arrays.stream(WORDS_SEPARATOR)
                            .mapToInt(fileChunk::lastIndexOf).filter(index -> index >= 0)
                            .max()
                            .orElseThrow(() -> new IllegalArgumentException(String.format("Unable to find any word separator among %s in the string '%s'", WORDS_SEPARATOR.toString(), fileChunk)));

                    processChunk(FILE_NAME, FILE_ID, chunkId++, fileChunk.substring(0, splitIndex), fileChunkConsumer);
                    charAccumulator = new StringBuilder(fileChunk.substring(splitIndex + 1));
                }
            }

            processChunk(FILE_NAME, FILE_ID, chunkId, charAccumulator.toString(), fileChunkConsumer);
        }
    }

    private void processChunk(String fileName, String fileId, Long chunkId, String chunkContent, Consumer<FileChunk> fileChunkConsumer){
        fileChunkConsumer.accept(FileChunk.builder()
                .chunkId(chunkId)
                .content(chunkContent)
                .fileInfo(
                    FileInfo.builder()
                        .fileName(fileName)
                        .fileId(fileId)
                            .build())
                .build());
    }
}

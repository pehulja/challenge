package com.pehulja.thefloow.filereader;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.pehulja.thefloow.storage.documents.FileChunk;

/**
 * Created by eyevpek on 2017-09-11.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"chunk.max-size=7"})
public class CharacterInputFileReaderTest {
    @Autowired
    private CharacterInputFileReader reader;

    @Test
    public void chunksProcessor() throws Exception {
        List<FileChunk> expected = new ArrayList<>();
        expected.add(FileChunk.builder().chunkId(0l).fileName("testFile.txt").content("aa bb").build());
        expected.add(FileChunk.builder().chunkId(1l).fileName("testFile.txt").content(" ccc").build());
        expected.add(FileChunk.builder().chunkId(2l).fileName("testFile.txt").content(" dddd").build());

        Path inputFile = Paths.get(this.getClass().getClassLoader().getResource("testFile.txt").toURI());
        List<FileChunk> actual = new CopyOnWriteArrayList<>();

        Future<?> task = reader.chunksProcessor(inputFile, actual::add);
        task.get();

        Assertions.assertThat(actual).containsAll(expected);
    }

    @Test
    public void chunksProcessorException() throws Exception {
        List<FileChunk> expected = new ArrayList<>();
        expected.add(FileChunk.builder().chunkId(0l).fileName("testFile.txt").content("aa bb").build());
        expected.add(FileChunk.builder().chunkId(1l).fileName("testFile.txt").content(" ccc").build());
        expected.add(FileChunk.builder().chunkId(2l).fileName("testFile.txt").content(" dddd").build());

        Path inputFile = Paths.get(this.getClass().getClassLoader().getResource("testFile.txt").toURI());
        List<FileChunk> actual = new ArrayList<>();

        reader.chunksProcessor(inputFile, (fileChunk) -> {
            throw new RuntimeException("Expected exception");
        });

        Assertions.assertThat(actual).isEmpty();
    }

}
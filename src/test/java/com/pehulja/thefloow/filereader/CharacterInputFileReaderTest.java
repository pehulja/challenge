package com.pehulja.thefloow.filereader;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by eyevpek on 2017-09-11.
 */
@RunWith (SpringRunner.class)
@SpringBootTest
public class CharacterInputFileReaderTest
{
    @Autowired
    private CharacterInputFileReader reader;

    @Test
    @Ignore
    public void chunksProcessor() throws Exception
    {
        Path inputFile = Paths.get(this.getClass().getClassLoader().getResource("enwiki-20170701-pages-articles-multistream-index.txt").toURI());
        reader.chunksProcessor(inputFile, chunk ->
        {
        });
    }

}
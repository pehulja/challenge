package com.pehulja.thefloow.filereader;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * Created by baske on 18.09.2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(properties = {"chunk.max-size=7"})
public class BySizeChunkSplitPolicyTest {

    @Autowired
    private BySizeChunkSplitPolicy bySizeChunkSplitPolicy;

    @Test
    public void testTrue() throws Exception {
        StringBuilder holder = new StringBuilder();
        holder.append("1234");

        Assertions.assertThat(bySizeChunkSplitPolicy.test(holder)).isFalse();
    }

    @Test
    public void testFalse() throws Exception {
        StringBuilder holder = new StringBuilder();
        holder.append("12345678");

        Assertions.assertThat(bySizeChunkSplitPolicy.test(holder)).isTrue();
    }

}
package com.pehulja.thefloow.filereader;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Created by eyevpek on 2017-09-11.
 */
@Service
public class BySizeChunkSplitPolicy implements ChunkSplitPolicy
{
    @Value("${chunk.max-size}")
    private Long chunkMaxSize;

    /**
     * Evaluates this predicate on the given argument.
     *
     * @param charAccumulator
     *         the input argument
     * @return {@code true} if the input argument matches the predicate, otherwise {@code false}
     */
    @Override
    public boolean test(StringBuilder charAccumulator)
    {
        return charAccumulator.length() >= chunkMaxSize;
    }
}

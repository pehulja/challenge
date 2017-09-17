package com.pehulja.thefloow.metric;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.pehulja.thefloow.storage.repository.CustomWordRepository;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Created by eyevpek on 2017-09-12.
 */
@RunWith(MockitoJUnitRunner.class)
public class AbstractMetricProcessorTest
{
    private AbstractMetricProcessor abstractMetricMock;

    @Mock
    private CustomWordRepository customWordRepository;

    @Before
    public void setupMocks()
    {
        abstractMetricMock = Mockito.mock(AbstractMetricProcessor.class, Mockito.CALLS_REAL_METHODS);
        abstractMetricMock.setCustomWordRepository(customWordRepository);
    }

    @Test
    public void applyTest() throws Exception
    {
        WordsMetric expected = WordsMetric.builder().usageCounter(1l).word("a").word("b").build();
        Mockito.when(customWordRepository.findWordsByCount(Matchers.eq(1l))).thenReturn(new HashSet<>(Arrays.asList("a", "b")));
        Assertions.assertThat(abstractMetricMock.apply(1l)).isEqualTo(expected);
    }
}
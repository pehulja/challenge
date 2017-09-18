package com.pehulja.thefloow.service.text_processing;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by baske on 12.09.2017.
 */
public class UniqueWordsUsageStatisticsFunctionTest {
    private UniqueWordsUsageStatisticsFunction function;

    @Captor
    private ArgumentCaptor<Map<String, Long>> argumentCaptor;

    @Before
    public void setup() {
        function = new UniqueWordsUsageStatisticsFunction();
    }

    @Test
    public void calculateWordsStatistics() {
        String textToParse = "hello>hi. hello\nhi hello-hi hello_hi hi\n";

        Map<String, Long> expected = new HashMap<>();
        expected.put("hello", 3l);
        expected.put("hi", 4l);
        expected.put("hello_hi", 1l);

        Assertions.assertThat(function.apply(textToParse)).isEqualTo(expected);
    }

    @Test
    public void calculateWordsStatisticsNoSpaces() {
        String textToParse = "2512884832:2394719:San-Juan-Tecuaco";

        Map<String, Long> expected = new HashMap<>();
        expected.put("2512884832", 1l);
        expected.put("2394719", 1l);
        expected.put("San", 1l);
        expected.put("Juan", 1l);
        expected.put("Tecuaco", 1l);

        Assertions.assertThat(function.apply(textToParse)).isEqualTo(expected);
    }
}
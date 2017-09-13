package com.pehulja.thefloow.service.text_processing;

import java.util.HashMap;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import com.pehulja.thefloow.storage.documents.FileWordsStatistics;

/**
 * Created by eyevpek on 2017-09-12.
 */
public class MergeStatisticsFunctionTest
{
    private MergeStatisticsFunction mergeStatisticsFunction;

    @Before
    public void setup()
    {
        mergeStatisticsFunction = new MergeStatisticsFunction();
    }

    @Test
    public void apply() throws Exception
    {
        Map<String, Long> map1 = new HashMap<>();
        map1.put("a", 0l);
        map1.put("b", 5l);
        map1.put("c", 7l);

        Map<String, Long> map2 = new HashMap<>();
        map2.put("a", 1l);
        map2.put("b", 5l);
        map2.put("d", 4l);

        Map<String, Long> expectedMap = new HashMap<>();
        expectedMap.put("a", 1l);
        expectedMap.put("b", 10l);
        expectedMap.put("c", 7l);
        expectedMap.put("d", 4l);

        FileWordsStatistics expected = FileWordsStatistics.builder().wordStatistics(expectedMap).fileId("id2").fileName("filename2").version(3l).build();

        FileWordsStatistics source = FileWordsStatistics.builder().wordStatistics(new HashMap<>(map1)).fileId("id1").fileName("filename1").version(1l).build();
        FileWordsStatistics target = FileWordsStatistics.builder().wordStatistics(new HashMap<>(map2)).fileId("id2").fileName("filename2").version(3l).build();

        FileWordsStatistics actual = mergeStatisticsFunction.apply(source, target);

        // Verify actual merged wordStatistics equals expected
        Assertions.assertThat(actual).isEqualTo(expected);

        //Verify that merge operation doesnt affect input arguments to function
        Assertions.assertThat(source.getWordStatistics()).isEqualTo(map1);
        Assertions.assertThat(target.getWordStatistics()).isEqualTo(map2);
    }

}
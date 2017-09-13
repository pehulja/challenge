package com.pehulja.thefloow.service.text_processing;

import java.util.Collection;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.pehulja.thefloow.storage.documents.FileWordsStatistics;

/**
 * Created by eyevpek on 2017-09-13.
 */
public class MergeStatisticsFunction implements BinaryOperator<FileWordsStatistics>
{

    @Override
    public FileWordsStatistics apply(FileWordsStatistics source, FileWordsStatistics target)
    {
        Map<String, Long> mergedStatistics = Stream.of(source.getWordStatistics(), target.getWordStatistics())
                .map(Map::entrySet)
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Long::sum));
        return target.toBuilder().wordStatistics(mergedStatistics).build();
    }
}

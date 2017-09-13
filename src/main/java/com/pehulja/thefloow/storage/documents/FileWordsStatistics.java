package com.pehulja.thefloow.storage.documents;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Singular;

/**
 * Created by baske on 11.09.2017.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@EqualsAndHashCode
public class FileWordsStatistics
{
    @Id
    private String fileId;

    @Indexed
    private String fileName;

    @Indexed
    @Version
    private Long version;

    @Singular
    private Map<String, Long> wordStatistics;
}

package com.pehulja.thefloow.statistics;

import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Singular;

/**
 * Created by baske on 11.09.2017.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Statistics {
    @Id
    private String fileId;

    @Indexed
    private String fileName;

    @Version
    private Long version;

    @Singular
    private Map<String, Long> statistics;
}

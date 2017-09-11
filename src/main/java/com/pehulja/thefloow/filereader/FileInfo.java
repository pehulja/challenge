package com.pehulja.thefloow.filereader;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;

/**
 * Created by baske on 11.09.2017.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileInfo {
    @Indexed(unique = true)
    private String fileId;

    @Indexed
    private String fileName;
}

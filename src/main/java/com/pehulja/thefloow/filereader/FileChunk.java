package com.pehulja.thefloow.filereader;

import org.springframework.data.mongodb.core.index.Indexed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by eyevpek on 2017-09-11.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileChunk
{
    @Indexed (unique = true)
    private String fileId;

    @Indexed
    private String fileName;

    private Long chunkId;
    private String content;
}

package com.pehulja.thefloow.filereader;

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
    private String fileId;
    private String fileName;
    private Long chunkId;
    private String content;
}

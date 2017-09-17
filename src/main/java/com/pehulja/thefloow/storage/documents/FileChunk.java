package com.pehulja.thefloow.storage.documents;

import java.util.Optional;

import org.springframework.data.mongodb.core.index.Indexed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Created by eyevpek on 2017-09-11.
 */
@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileChunk
{
    @Indexed
    private String fileName;

    private Long chunkId;

    private String content;

    /*@Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("FileChunk{");
        sb.append(", fileName='").append(fileName).append('\'');
        sb.append(", chunkId=").append(chunkId);
        sb.append(", content size='").append(Optional.ofNullable(content).map(String::length).orElse(0)).append('\'');
        sb.append('}');
        return sb.toString();
    }*/
}

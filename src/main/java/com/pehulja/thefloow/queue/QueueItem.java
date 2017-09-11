package com.pehulja.thefloow.queue;

import org.springframework.data.annotation.Id;

import com.pehulja.thefloow.filereader.FileChunk;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by eyevpek on 2017-09-11.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueueItem
{
    @Id
    public String id;
    private FileChunk fileChunk;
}

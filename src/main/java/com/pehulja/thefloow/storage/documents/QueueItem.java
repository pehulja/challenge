package com.pehulja.thefloow.storage.documents;

import org.springframework.data.annotation.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by eyevpek on 2017-09-11.
 */
@Data
@ToString (callSuper = true, exclude = "id")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueueItem
{
    @Id
    public String id;
    private FileChunk fileChunk;
}

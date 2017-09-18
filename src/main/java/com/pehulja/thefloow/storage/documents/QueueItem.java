package com.pehulja.thefloow.storage.documents;

import lombok.*;
import org.springframework.data.annotation.Id;

/**
 * Created by eyevpek on 2017-09-11.
 */
@Data
@ToString(callSuper = true, exclude = "id")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QueueItem {
    @Id
    public String id;
    private FileChunk fileChunk;
}

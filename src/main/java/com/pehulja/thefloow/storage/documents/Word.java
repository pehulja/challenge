package com.pehulja.thefloow.storage.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Created by eyevpek on 2017-09-14.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode (exclude = "id")
public class Word
{
    @Id
    private String word;

    private Long counter;

    @Indexed
    private String fileName;

    @Indexed
    private String fileId;
}

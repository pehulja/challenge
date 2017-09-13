package com.pehulja.thefloow.storage.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by eyevpek on 2017-09-12.
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@EqualsAndHashCode (exclude = "id")
@ToString (exclude = "id")
public class QueueStatistics
{
    @Id
    @Indexed
    private String id;

    @Transient
    public static final String PUSHED = "pushed";

    @Transient
    public static final String SUCCESS = "success";

    @Transient
    public static final String FAILED = "failed";

    @Field (PUSHED)
    private long pushedToQueue;

    @Field (SUCCESS)
    private long successfullyProcessed;

    @Field (FAILED)
    private long failedToProcess;

    @Transient
    private long possibleLostOrInProgress;
}

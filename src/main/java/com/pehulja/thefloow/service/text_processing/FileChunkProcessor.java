package com.pehulja.thefloow.service.text_processing;

import java.util.function.Consumer;

import com.pehulja.thefloow.storage.documents.QueueItem;

/**
 * Created by eyevpek on 2017-09-12.
 */
public interface FileChunkProcessor extends Consumer<QueueItem>
{
}

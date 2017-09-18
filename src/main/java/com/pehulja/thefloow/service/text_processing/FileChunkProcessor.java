package com.pehulja.thefloow.service.text_processing;

import com.pehulja.thefloow.storage.documents.QueueItem;

import java.util.function.Consumer;

/**
 * Created by eyevpek on 2017-09-12.
 */
public interface FileChunkProcessor extends Consumer<QueueItem> {
}

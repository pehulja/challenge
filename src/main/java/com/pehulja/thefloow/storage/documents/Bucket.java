package com.pehulja.thefloow.storage.documents;

import java.util.Map;

import org.springframework.data.annotation.Id;

/**
 * Created by eyevpek on 2017-09-15.
 */
public class Bucket
{
    @Id
    private Long id;

    private Map<String, Long> holder;
}

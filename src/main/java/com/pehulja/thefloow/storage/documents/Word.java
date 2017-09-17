package com.pehulja.thefloow.storage.documents;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.function.BinaryOperator;

/**
 * Created by eyevpek on 2017-09-14.
 */
@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class Word
{
    @Id
    private String word;

    @Indexed
    private Long counter;

    public static class Reduce implements BinaryOperator<Word>{

        @Override
        public Word apply(Word a, Word b) {
            return Word.builder().word(a.word).counter(a.counter + b.counter).build();
        }
    }
}

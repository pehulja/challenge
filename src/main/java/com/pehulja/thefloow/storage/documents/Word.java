package com.pehulja.thefloow.storage.documents;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
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
public class Word {
    @Id
    private String word;

    @Indexed
    private Long counter;

    public static class Reduce implements BinaryOperator<Word> {

        @Override
        public Word apply(Word a, Word b) {
            return Word.builder().word(a.word).counter(a.counter + b.counter).build();
        }
    }
}

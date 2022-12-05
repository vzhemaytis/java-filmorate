package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.Positive;


@Data
//@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
//    @NonNull
    private Long reviewId;
    @NonNull
    private Long userId;
    @NonNull
    private Long filmId;
    @NonNull
    private String content;
    @NonNull
    private Boolean isPositive;
    private Long useful; // в базе отсутствует, считается динамически
}

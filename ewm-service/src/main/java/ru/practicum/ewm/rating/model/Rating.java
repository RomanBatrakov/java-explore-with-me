package ru.practicum.ewm.rating.model;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Rating {
    @Builder.Default
    private Long likes = 0L;
    @Builder.Default
    private Long dislikes = 0L;
    @Builder.Default
    private Double rating = 0.00;


    public Long getLikes() {
        return likes;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
    }

    public Long getDislikes() {
        return dislikes;
    }

    public void setDislikes(Long dislikes) {
        this.dislikes = dislikes;
    }

    public Double getRating() {
        return rating;
    }
}
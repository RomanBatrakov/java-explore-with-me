package ru.practicum.ewm.rating.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Rating {
    @Builder.Default
    private Long likes = 0L;
    @Builder.Default
    private Long dislikes = 0L;
    @Builder.Default
    private Double rating = 0.0;


    public Long getLikes() {
        return likes;
    }

    public void setLikes(Long likes) {
        this.likes = likes;
        calculateRating();
    }

    public Long getDislikes() {
        return dislikes;
    }

    public void setDislikes(Long dislikes) {
        this.dislikes = dislikes;
        calculateRating();
    }

    public Double getRating() {
        return rating;
    }

    private void calculateRating() {
        long totalVotes = this.likes + this.dislikes;
        if (totalVotes == 0) {
            this.rating = 0.0;
        } else {
            this.rating = (double) (this.likes / totalVotes) * 10;
        }
    }
}

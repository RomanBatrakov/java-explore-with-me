package ru.practicum.ewm.reaction.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reactions")
public class Reaction {
    @EmbeddedId
    private ReactionId id;
    @Column(name = "reaction")
    @Enumerated(EnumType.STRING)
    private ReactionType reaction;
}
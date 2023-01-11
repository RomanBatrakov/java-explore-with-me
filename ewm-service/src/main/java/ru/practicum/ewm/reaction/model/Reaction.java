package ru.practicum.ewm.reaction.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

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
    private ReactionType reaction;
}
package ru.practicum.ewm.reaction.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.rating.model.Rating;
import ru.practicum.ewm.reaction.dao.ReactionRepository;
import ru.practicum.ewm.reaction.dto.ReactionDto;
import ru.practicum.ewm.reaction.mapper.ReactionMapper;
import ru.practicum.ewm.reaction.model.Reaction;
import ru.practicum.ewm.reaction.model.ReactionId;
import ru.practicum.ewm.reaction.model.ReactionType;
import ru.practicum.ewm.user.model.User;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReactionServiceImpl implements ReactionService {
    private final ReactionRepository reactionRepository;
    private final ReactionMapper reactionMapper;

    @Override
    public ReactionDto createReaction(User user, Event event, ReactionType reactionType) {
        ReactionId reactionId = ReactionId.builder().event(event).user(user).build();
        Reaction userReaction = Reaction.builder().id(reactionId).reaction(reactionType).build();
        return reactionMapper.reactionDto(reactionRepository.save(userReaction));
    }

    @Override
    public void deleteById(User user, Event event) {
        try {
            ReactionId reactionId = ReactionId.builder().event(event).user(user).build();
            reactionRepository.deleteById(reactionId);
        } catch (EmptyResultDataAccessException e) {
            throw new NoSuchElementException("Reaction is not found");
        }
    }

    @Override
    public void setRating(List<Event> events) {
        Map<ReactionId, ReactionType> reactionMap = reactionRepository.findAllById_EventIn(events)
                .stream()
                .collect(Collectors.toMap(Reaction::getId, Reaction::getReaction));
        events.forEach(event -> {
                    List<ReactionType> eventReactions = reactionMap.entrySet().stream()
                            .filter(entry -> entry.getKey().getEvent().equals(event))
                            .map(Map.Entry::getValue)
                            .collect(Collectors.toList());
                    Rating rating = Rating.builder().build();
                    if (!eventReactions.isEmpty()) {
                        long likes = eventReactions.stream().filter(r -> r.equals(ReactionType.LIKE)).count();
                        rating.setLikes(likes);
                        rating.setDislikes(eventReactions.size() - likes);
                    }
                    event.setRating(rating);

                }
        );
    }
}
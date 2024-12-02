package com.io.iotask.service.impl;

import com.io.iotask.repository.ClientReactionRepository;
import com.io.iotask.repository.RecordRepository;
import com.io.iotask.repository.entity.ClientReaction;
import com.io.iotask.service.LikeCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LikeCalculationServiceImpl implements LikeCalculationService {
    private final DeferredWriteCache likeCache;
    private final ClientReactionRepository clientReactionRepository;
    private final RecordRepository recordRepository;

    public void processLike(UUID clientId, UUID id, int increment) {
        recordRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found"));

        clientReactionRepository.findByClientIdAndRecordId(clientId, id).ifPresentOrElse((reaction) -> {
            if (reaction.getReaction() == increment) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client already reacted to this record");
            } else {
                int prevReaction = reaction.getReaction();
                reaction.setReaction(increment);
                clientReactionRepository.save(reaction);
                likeCache.increment(id, increment - prevReaction);
            }
        }, () -> {
            clientReactionRepository.save(new ClientReaction(clientId, id, increment));
            likeCache.increment(id, increment);
        });
    }
}

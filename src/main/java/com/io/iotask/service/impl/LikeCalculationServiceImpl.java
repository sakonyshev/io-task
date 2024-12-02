package com.io.iotask.service.impl;

import com.io.iotask.repository.ClientReactionRepository;
import com.io.iotask.repository.RecordRepository;
import com.io.iotask.repository.entity.ClientReaction;
import com.io.iotask.service.LikeCalculationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
//Service for processing likes
//-1 - dislike
//0 - neutral
//1 - like
public class LikeCalculationServiceImpl implements LikeCalculationService {
    private final DeferredRecordWriteCacheImpl likeCache;
    private final ClientReactionRepository clientReactionRepository;
    private final RecordRepository recordRepository;

    public void processLike(UUID clientId, UUID id, int increment) {
        log.trace("Processing like for clientId={}, recordId={}, increment={}", clientId, id, increment);
        recordRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found"));
        log.trace("Record found, processing like");
        clientReactionRepository.findByClientIdAndRecordId(clientId, id).ifPresentOrElse(reaction -> {
            if (reaction.getReaction() == increment) {
                log.trace("Client already reacted to this record. recordId={}, clientId={}", id, clientId);
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Client already reacted to this record");
            } else {
                log.trace("Client reaction found, updating reaction. recordId={}, clientId={}", id, clientId);
                int prevReaction = reaction.getReaction();
                reaction.setReaction(increment);
                clientReactionRepository.save(reaction);
                likeCache.increment(id, increment - prevReaction);
            }
        }, () -> {
            log.trace("Client reaction not found, creating new reaction. recordId={}, clientId={}", id, clientId);
            clientReactionRepository.save(new ClientReaction(clientId, id, increment));
            likeCache.increment(id, increment);
        });
        log.trace("Like processed for clientId={}, recordId={}, increment={}", clientId, id, increment);
    }
}

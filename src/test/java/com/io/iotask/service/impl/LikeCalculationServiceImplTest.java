package com.io.iotask.service.impl;

import com.io.iotask.repository.ClientReactionRepository;
import com.io.iotask.repository.RecordRepository;
import com.io.iotask.repository.entity.ClientReaction;
import com.io.iotask.repository.entity.Record;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

@ExtendWith({MockitoExtension.class})
class LikeCalculationServiceImplTest {
    @InjectMocks
    private LikeCalculationServiceImpl testedObject;
    @Mock
    private DeferredRecordWriteCacheImpl likeCache;
    @Mock
    private ClientReactionRepository clientReactionRepository;
    @Mock
    private RecordRepository recordRepository;
    private UUID clientId;
    private UUID recordId;

    LikeCalculationServiceImplTest() {
    }

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();
        recordId = UUID.randomUUID();
        Mockito.when(recordRepository.findById(recordId)).thenReturn(Optional.of(new Record()));
    }

    @Test
    void processLike_fromNegativeToPositive() {
        ClientReaction clientReaction = new ClientReaction(clientId, recordId, -1);
        Mockito.when(clientReactionRepository.findByClientIdAndRecordId(clientId, recordId)).thenReturn(Optional.of(clientReaction));

        testedObject.processLike(clientId, recordId, 1);

        ArgumentCaptor<ClientReaction> reactionCaptor = ArgumentCaptor.forClass(ClientReaction.class);

        Mockito.verify(clientReactionRepository).save(reactionCaptor.capture());
        Mockito.verify(likeCache, Mockito.times(1)).increment(recordId, 2);

        Assertions.assertEquals(1, reactionCaptor.getValue().getReaction());
    }

    @Test
    void processLike_fromPositiveToNegative() {
        ClientReaction clientReaction = new ClientReaction(clientId, recordId, 1);

        Mockito.when(clientReactionRepository.findByClientIdAndRecordId(clientId, recordId))
                .thenReturn(Optional.of(clientReaction));

        testedObject.processLike(clientId, recordId, -1);

        ArgumentCaptor<ClientReaction> reactionCaptor = ArgumentCaptor.forClass(ClientReaction.class);

        Mockito.verify(clientReactionRepository).save(reactionCaptor.capture());
        Mockito.verify(likeCache, Mockito.times(1)).increment(recordId, -2);

        Assertions.assertEquals(-1, reactionCaptor.getValue().getReaction());
    }

    @Test
    void processLike_removeLike() {
        ClientReaction clientReaction = new ClientReaction(clientId, recordId, 1);

        Mockito.when(clientReactionRepository.findByClientIdAndRecordId(clientId, recordId))
                .thenReturn(Optional.of(clientReaction));

        testedObject.processLike(clientId, recordId, 0);

        ArgumentCaptor<ClientReaction> reactionCaptor = ArgumentCaptor.forClass(ClientReaction.class);

        Mockito.verify(clientReactionRepository).save(reactionCaptor.capture());
        Mockito.verify(likeCache, Mockito.times(1)).increment(recordId, -1);
        Assertions.assertEquals(0, reactionCaptor.getValue().getReaction());
    }

    @Test
    void processLike_sameReaction() {
        ClientReaction clientReaction = new ClientReaction(clientId, recordId, 1);

        Mockito.when(clientReactionRepository.findByClientIdAndRecordId(clientId, recordId))
                .thenReturn(Optional.of(clientReaction));

        Assertions.assertThrows(ResponseStatusException.class,
                () -> testedObject.processLike(clientId, recordId, 1));
    }
}
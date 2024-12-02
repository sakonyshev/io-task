package com.io.iotask.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LikeCalculationService {

    private final DeferredWriteCache likeCache;

    @Override
    public void processLike(UUID id, boolean increment) {
        if (increment) {
            likeCache.increment(id);
        } else {
            likeCache.decrement(id);
        }
    }
}

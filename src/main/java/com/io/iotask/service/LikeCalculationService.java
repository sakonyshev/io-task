package com.io.iotask.service;

import java.util.UUID;

public interface LikeCalculationService {
    void processLike(UUID clientId, UUID id, int increment);
}

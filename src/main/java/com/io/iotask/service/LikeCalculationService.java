package com.io.iotask.service.impl;

import java.util.UUID;

public interface LikeCalculationService {
    void processLike(UUID id, boolean increment);
}

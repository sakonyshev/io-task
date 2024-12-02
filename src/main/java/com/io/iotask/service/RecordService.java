package com.io.iotask.service;

import java.util.UUID;

public interface RecordLikeCalculator {
    void like(UUID id, boolean modifier);
}

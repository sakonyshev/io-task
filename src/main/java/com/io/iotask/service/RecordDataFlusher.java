package com.io.iotask.service;

import jakarta.transaction.Transactional;

import java.math.BigInteger;
import java.util.UUID;

public interface RecordDataFlusher {
    @Transactional
    void save(UUID id, BigInteger likes);
}

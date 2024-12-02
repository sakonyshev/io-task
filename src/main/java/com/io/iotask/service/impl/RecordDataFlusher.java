package com.io.iotask.service.impl;

import com.io.iotask.repository.RecordRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RecordDataFlusher {
    private final RecordRepository recordRepository;

    @Transactional
    public void save(UUID id, BigInteger likes) {
        recordRepository.processStoredLikes(id, likes);
    }
}
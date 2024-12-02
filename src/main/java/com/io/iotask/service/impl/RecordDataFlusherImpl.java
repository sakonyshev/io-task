package com.io.iotask.service.impl;

import com.io.iotask.repository.RecordRepository;
import com.io.iotask.service.RecordDataFlusher;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecordDataFlusherImpl implements RecordDataFlusher {
    private final RecordRepository recordRepository;

    @Override
    @Transactional
    public void save(UUID id, BigInteger likes) {
        log.trace("Saving record with id={} and likes={}", id, likes);
        recordRepository.processStoredLikes(id, likes);
    }
}
package com.io.iotask.service.impl;

import com.io.iotask.repository.Record;
import com.io.iotask.repository.RecordRepository;
import com.io.iotask.service.RecordLikeCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ConcurrentLruCache;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecordLikeCalculatorImpl implements RecordLikeCalculator {
    private final ConcurrentLruCache<UUID, Record> recordLikeCache = new ConcurrentLruCache<>(100,
            this::fetch);

    private final RecordRepository recordRepository;

    @Override
    public void like(UUID id, boolean modifier) {
        log.trace("Going to update record reputation with following parameters:" +
                " id={}, modifier={}", id, modifier);
        Record meta = recordLikeCache.get(id);
        meta.setLikes(meta.getLikes());

        log.trace("Reputation updated, id={}", id);
    }

    private Record fetch(UUID id) {
        return recordRepository.findById(id).orElse(null);
    }
}

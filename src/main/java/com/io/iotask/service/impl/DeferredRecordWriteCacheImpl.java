package com.io.iotask.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.io.iotask.service.DeferredRecordWriteCache;
import com.io.iotask.service.RecordDataFlusher;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
@RequiredArgsConstructor
//Cache implementation for deferred write of records
//Flushes dirty keys every 20 seconds to reduce the number of writes to the database
public class DeferredRecordWriteCacheImpl implements DisposableBean, DeferredRecordWriteCache {
    private final Cache<UUID, BigInteger> cache = Caffeine.newBuilder().build();
    private final Set<UUID> dirtyKeys = ConcurrentHashMap.newKeySet();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final RecordDataFlusher recordDataFlusher;

    @PostConstruct
    public void init() {
        log.trace("Starting deferred write cache flusher");
        scheduler.scheduleAtFixedRate(this::flushDirtyKeys, 20L, 20L, TimeUnit.SECONDS);
        log.trace("Deferred write cache flusher started");
    }

    @Override
    public void increment(UUID key, int value) {
        log.trace("Incrementing key={}, value={}", key, value);
        cache.asMap().merge(key, BigInteger.valueOf(value), BigInteger::add);
        dirtyKeys.add(key);
    }

    @Override
    public void put(UUID key, BigInteger value) {
        log.trace("Putting key={}, value={}", key, value);
        cache.put(key, value);
        dirtyKeys.add(key);
    }

    @Override
    public BigInteger get(UUID key) {
        log.trace("Getting key={}", key);
        return cache.getIfPresent(key);
    }

    public synchronized void flushDirtyKeys() {
        log.trace("Flushing dirty keys");

        if (!dirtyKeys.isEmpty()) {
            log.trace("Dirty keys are not empty, saving records");
            Set<UUID> keysToSave = new HashSet<>(dirtyKeys);
            dirtyKeys.clear();

            keysToSave.forEach(key -> cache.asMap()
                    .computeIfPresent(key, (k, v) -> {
                        try {
                            log.trace("Saving record with id={} and likes={}", k, v);
                            recordDataFlusher.save(k, v);
                            dirtyKeys.remove(k);
                            return null;
                        } catch (Exception var4) {
                            log.error("Error occurred while saving record with id={}, message={}", k, var4.getMessage());
                            dirtyKeys.add(k);
                            return v;
                        }
                    }));
        }
    }

    public void destroy() throws Exception {
        log.info("Stopping deferred write cache flusher");
        scheduler.shutdown();

        try {
            log.trace("Waiting for deferred write cache flusher to stop");
            if (!scheduler.awaitTermination(60L, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
                log.warn("Deferred write cache flusher did not stop gracefully");
            }
        } catch (InterruptedException var2) {
            log.error("Interrupted while waiting for deferred write cache flusher to stop", var2);
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
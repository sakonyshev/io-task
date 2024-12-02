package com.io.iotask.service;

import java.math.BigInteger;
import java.util.UUID;

public interface DeferredRecordWriteCache {
    void increment(UUID key, int value);

    void put(UUID key, BigInteger value);

    BigInteger get(UUID key);
}

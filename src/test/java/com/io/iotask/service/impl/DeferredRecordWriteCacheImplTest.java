package com.io.iotask.service.impl;

import com.io.iotask.service.RecordDataFlusher;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class})
class DeferredRecordWriteCacheImplTest {
    @InjectMocks
    private DeferredRecordWriteCacheImpl testedObject;
    @Mock
    private RecordDataFlusher recordDataFlusher;

    @SneakyThrows
    @Test
    void testIncrement_concurrent() {
        UUID recordId = UUID.randomUUID();
        ExecutorService executor = Executors.newFixedThreadPool(100);

        Runnable positiveIncrement = () -> testedObject.increment(recordId, 1);
        Runnable negativeIncrement = () -> testedObject.increment(recordId, -1);
        Runnable dataFlusher = () -> testedObject.flushDirtyKeys();

        for (int i = 0; i < 100000; i++) {
            executor.submit(positiveIncrement);
            executor.submit(dataFlusher);
            executor.submit(negativeIncrement);
        }

        executor.shutdown();
        Assertions.assertTrue(executor.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS));

        ArgumentCaptor<BigInteger> likesCaptor = ArgumentCaptor.forClass(BigInteger.class);
        verify(recordDataFlusher, atLeast(1)).save(any(), likesCaptor.capture());

        likesCaptor.getAllValues().stream().reduce(BigInteger::add).ifPresent(sum -> {
            Assertions.assertEquals(0, sum.longValue());
        });
    }

}
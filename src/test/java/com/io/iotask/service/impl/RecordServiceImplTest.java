package com.io.iotask.service.impl;

import com.io.iotask.repository.RecordRepository;
import com.io.iotask.repository.entity.Record;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;

import java.nio.file.Path;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class})
class RecordServiceImplTest {
    @InjectMocks
    private RecordServiceImpl testedObject;
    @Mock
    private RecordRepository recordRepository;

    @Test
    @SneakyThrows
    void test_loadCsv() {
        ClassPathResource resource = new ClassPathResource("data.csv");
        Path filePath = resource.getFile().toPath();

        testedObject.loadCSV(UUID.randomUUID(), filePath.toString());
        ArgumentCaptor<Record> recordArgumentCaptor = ArgumentCaptor.forClass(Record.class);

        verify(recordRepository, times(1)).save(recordArgumentCaptor.capture());

        Record recordArgumentCaptorValue = recordArgumentCaptor.getValue();

        Assertions.assertEquals("Title test", recordArgumentCaptorValue.getTitle());
        Assertions.assertEquals("Author", recordArgumentCaptorValue.getAuthor());
        Assertions.assertEquals("2021-12-01T00:00", recordArgumentCaptorValue.getDate().toString());
        Assertions.assertEquals(10000, recordArgumentCaptorValue.getViews().intValue());
        Assertions.assertEquals(20000, recordArgumentCaptorValue.getLikes().intValue());
        Assertions.assertEquals("http://localhost:8080/test", recordArgumentCaptorValue.getLink());
    }
}

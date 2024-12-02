package com.io.iotask.service;

import com.io.iotask.web.model.dto.RecordDto;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface RecordService {
    void loadCSV(UUID taskId, String filePath) throws CsvValidationException, IOException;

    List<RecordDto> findRecords(int page, int size, String author, String title);

    void delete(UUID id);

    RecordDto save(RecordDto recordDto);

    RecordDto update(UUID id, RecordDto recordDto);

    RecordDto getRecord(UUID id);
}


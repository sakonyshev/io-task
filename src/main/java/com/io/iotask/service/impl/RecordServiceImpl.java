package com.io.iotask.service.impl;

import com.io.iotask.repository.RecordRepository;
import com.io.iotask.repository.RecordSpecification;
import com.io.iotask.repository.entity.Record;
import com.io.iotask.service.RecordService;
import com.io.iotask.service.mapper.RecordMapper;
import com.io.iotask.web.model.dto.RecordDto;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.time.Month;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecordServiceImpl implements RecordService {
    private final RecordRepository recordRepository;
    private final EntityManager entityManager;
    private final RecordMapper recordMapper;

    @Async
    public void loadCSV(UUID taskId, String filePath) throws CsvValidationException, IOException {
        long startTime = System.currentTimeMillis();

        log.info("Loading CSV file, taskId={}", taskId);
        long errors = 0L;

        try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
            String[] line;
            reader.readNext(); // skip header
            while ((line = reader.readNext()) != null) {
                try {
                    Record video = new Record();

                    video.setTitle(line[0]);
                    video.setAuthor(line[1]);

                    String[] monthYear = line[2].split(" ");
                    YearMonth yearMonth = YearMonth.of(Integer.parseInt(monthYear[1]), Month.valueOf(monthYear[0].toUpperCase()));
                    video.setDate(yearMonth.atDay(1).atStartOfDay());

                    video.setViews(BigInteger.valueOf(Long.parseLong(line[3])));
                    video.setLikes(BigInteger.valueOf(Long.parseLong(line[4])));

                    video.setLink(line[5]);

                    recordRepository.save(video);
                } catch (Exception e) {
                    ++errors;
                    log.error("Error while processing line '{}' with the following error '{}': , taskId={}",
                            String.join(",", line), e.getMessage(), taskId);
                }
            }
        }

        log.info("CSV file has been loaded, errors={}, processingTime={} seconds, taskId={}", new Object[]{errors, (System.currentTimeMillis() - startTime) / 1000L, taskId});
    }

    public List<RecordDto> findRecords(int page, int size, String author, String title) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Record> criteriaQuery = criteriaBuilder.createQuery(Record.class);
        Root<Record> root = criteriaQuery.from(Record.class);

        Specification<Record> spec = Specification.where(RecordSpecification.likeAuthor(author))
                .and(RecordSpecification.likeTitle(title)
                        .and(RecordSpecification.isNotDeleted()));

        criteriaBuilder.desc(root.get("date"));

        criteriaQuery.where(spec.toPredicate(root, criteriaQuery, criteriaBuilder));
        TypedQuery<Record> query = entityManager.createQuery(criteriaQuery);

        query.setMaxResults(size);

        return query.getResultList().stream().map(recordMapper::toDto).toList();
    }

    public void delete(UUID id) {
        int updatedRecords = recordRepository.softDeleteBy(id);

        if (updatedRecords == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found");
        }
    }

    public RecordDto save(RecordDto recordDto) {
        if (recordDto.getId() != null) {
            throw new IllegalArgumentException("Id should be null");
        } else {
            return recordMapper.toDto(recordRepository.save(recordMapper.toEntity(recordDto)));
        }
    }

    public RecordDto update(UUID id, RecordDto recordDto) {
        if (recordDto.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Id should be null");
        } else {
            Record existingRecord = recordRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found"));
            recordMapper.updateRecordFromDto(recordDto, existingRecord);
            return recordMapper.toDto(recordRepository.save(existingRecord));
        }
    }

    public RecordDto getRecord(UUID id) {
        Record fetchedRecord = recordRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found"));

        return recordMapper.toDto(fetchedRecord);
    }
}

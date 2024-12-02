package com.io.iotask;

import com.io.iotask.service.RecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("io-task/v1")
@RequiredArgsConstructor
public class DataManagementController {

    private final RecordService recordService;

    @GetMapping("/records")
    public void getRecords() {

    }

    @PostMapping("/records")
    public void insertRecord() {

    }

    @DeleteMapping("/records/{id}")
    public void deleteRecord(@PathVariable("id") UUID id) {

    }

    @PatchMapping("/records/{id}")
    public void updateRecord(@PathVariable("id") UUID id) {

    }

    @PostMapping("/records/upload")
    public String uploadCSV(@RequestParam("file") MultipartFile file) {
        UUID taskId = UUID.randomUUID();

        try {
            // Сохранить файл во временную директорию
            File tempFile = File.createTempFile("upload", ".csv");
            file.transferTo(tempFile);

            recordService.loadCSV(taskId, tempFile.getAbsolutePath());
        } catch (Exception e) {
            log.error("Error while uploading file", e);
        }
        return taskId.toString();
    }
}

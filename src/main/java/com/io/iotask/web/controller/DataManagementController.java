package com.io.iotask.web.controller;

import com.io.iotask.service.RecordService;
import com.io.iotask.web.model.dto.RecordDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping({"io-task/v1"})
@RequiredArgsConstructor
public class DataManagementController {
    private final RecordService recordService;

    @GetMapping({"/record"})
    @Operation(summary = "Get filtered records",
            description = "This method returns records filtered by author and title")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Records were found"
    )})
    public List<RecordDto> getRecords(@RequestParam(name = "page", defaultValue = "0") int page,
                                      @RequestParam(name = "size", defaultValue = "10") int size,
                                      @RequestParam(name = "author", required = false) String author,
                                      @RequestParam(name = "title", required = false) String title) {
        return recordService.findRecords(page, size, author, title);
    }

    @GetMapping({"/record/{id}"})
    @Operation(summary = "Get record by id", description = "This method returns record by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record was found"), 
            @ApiResponse(responseCode = "404", description = "Record was not found"
    )})
    public RecordDto getRecord(@PathVariable("id") UUID id) {
        log.trace("Getting record with id={}", id);
        return recordService.getRecord(id);
    }

    @PostMapping({"/record"})
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Save record", description = "This method saves record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Record was saved")
    })
    public RecordDto saveRecord(@RequestBody RecordDto recordDto) {
        log.trace("Saving record with title={} and author={}", recordDto.getTitle(), recordDto.getAuthor());
        return recordService.save(recordDto);
    }

    @DeleteMapping({"/record/{id}"})
    @Operation(summary = "Delete record", description = "This method deletes record by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Record was deleted"),
            @ApiResponse(responseCode = "404", description = "Record was not found")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRecord(@PathVariable("id") UUID id) {
        log.trace("Deleting record with id={}", id);
        recordService.delete(id);
    }

    @PatchMapping({"/record/{id}"})
    @Operation(summary = "Update record", description = "This method updates record by id")
    @ApiResponses(value = {
                    @ApiResponse(responseCode = "200", description = "Record was updated"),
                    @ApiResponse(responseCode = "404", description = "Record was not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid request")
    })
    public RecordDto updateRecord(@PathVariable("id") UUID id, @RequestBody RecordDto recordDto) {
        log.trace("Updating record with id={}", id);
        return recordService.update(id, recordDto);
    }

    @PostMapping(
            value = {"/record/upload"},
            consumes = {"multipart/form-data"}
    )
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Upload CSV file",
            description = "This method uploads CSV file, parses it and saves records" +
            " asynchronously. Returns taskId to track the progress. Corrupted lines will be skipped.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "File was uploaded"),
            @ApiResponse(responseCode = "400", description = "Invalid request"
    )})
    public String uploadCSV(@RequestParam("file") MultipartFile file) {
        log.trace("Uploading file {}", file.getOriginalFilename());
        UUID taskId = UUID.randomUUID();
        log.trace("TaskId={}", taskId);

        try {
            File tempFile = File.createTempFile("upload", ".csv");
            file.transferTo(tempFile);
            recordService.loadCSV(taskId, tempFile.getAbsolutePath());
        } catch (Exception var4) {
            log.error("Error while uploading file", var4);
        }

        log.trace("File uploaded successfully");
        return taskId.toString();
    }
}
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.io.iotask.web.controller;

import com.io.iotask.repository.RecordRepository;
import com.io.iotask.service.LikeCalculationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping({"io-task/v1/metrics"})
public class DataMetricController {
    private final LikeCalculationService likeCalculationService;
    private final RecordRepository recordRepository;

    @GetMapping({"/authors"})
    @Operation(summary = "Get influence authors", description = "This method returns influence authors")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authors were found"
            )})
    public List<String> getInfluenceRecordAuthors(@RequestParam(name = "size", defaultValue = "10") int size) {
        log.trace("Getting influence authors for records with size {}", size);
        return recordRepository.getInfluenceAuthors(size);
    }

    @PostMapping({"/like/{id}"})
    @Operation(summary = "Like record",
            description = "This method is used to save user reaction to record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Record was liked"),
            @ApiResponse(responseCode = "404", description = "Record does not exist"),
            @ApiResponse(responseCode = "400", description = "Client already reacted to this record"
            )})
    public void likeRecord(@CookieValue(name = "clientId", required = false) UUID clientId,
                           @PathVariable("id") UUID recordId,
                           @RequestParam(name = "like", defaultValue = "0") int like,
                           HttpServletResponse response) {
        log.trace("Processing like for record {} with value {}", recordId, like);

        if (clientId == null) {
            clientId = UUID.randomUUID();
            Cookie client = new Cookie("clientId", clientId.toString());
            client.setPath("/");
            client.setMaxAge(60 * 60 * 24 * 365);
            response.addCookie(client);
        }

        likeCalculationService.processLike(clientId, recordId, like);
        log.trace("Like processed successfully for record {} with value {}", recordId, like);
    }
}

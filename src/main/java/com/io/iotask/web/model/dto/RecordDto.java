package com.io.iotask.web.model.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class RecordDto {
    private UUID id;
    @NotNull
    private String title;
    @NotNull
    private String author;
    @NotNull
    private LocalDateTime date;
    @PositiveOrZero
    private BigInteger views;
    @NotNull
    private BigInteger likes;
    @NotEmpty
    private String link;
}

package com.io.iotask.repository.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "client_reaction",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"client_id", "record_id"})}
)
public class ClientReaction {
    @Id
    private UUID clientId;
    private UUID recordId;

    @Min(-1L)
    @Max(1L)
    private int reaction;
}

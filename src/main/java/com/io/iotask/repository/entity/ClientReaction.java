package com.io.iotask.repository;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@Table(name = "client_reaction", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"client_id", "record_id"})
})
public class ClientReaction {
    @Id
    private UUID clientId;
    private UUID recordId;
    private boolean liked;
}

package com.io.iotask.repository;

import com.io.iotask.repository.entity.ClientReaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ClientReactionRepository extends JpaRepository<ClientReaction, UUID> {
    Optional<ClientReaction> findByClientIdAndRecordId(UUID clientId, UUID recordId);
}
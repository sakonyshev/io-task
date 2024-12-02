package com.io.iotask.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.io.iotask.repository.entity.Record;

import java.math.BigInteger;
import java.util.List;
import java.util.UUID;

@Repository
public interface RecordRepository extends JpaRepository<Record, UUID>, JpaSpecificationExecutor<Record> {
    @Modifying
    @Query("update Record r set r.likes=r.likes + :likes " +
            " where r.id=:id and r.deleted = FALSE")
    void processStoredLikes(@Param("id") UUID id, @Param("likes") BigInteger likes);

    @Modifying
    @Transactional
    @Query("UPDATE Record r SET r.deleted = TRUE WHERE r.id=:id and r.deleted = FALSE")
    int softDeleteBy(@Param("id") UUID uuid);

    @Query(
            value = " SELECT r.author " +
                    " FROM records r " +
                    " WHERE r.deleted=FALSE" +
                    " GROUP BY r.author " +
                    " ORDER BY SUM(likes) DESC, SUM(views) DESC " +
                    " LIMIT :size",
            nativeQuery = true
    )
    List<String> getInfluenceAuthors(@Param("size") int size);
}

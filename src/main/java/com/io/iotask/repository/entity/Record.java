package com.io.iotask.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.validator.constraints.URL;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "records",
        indexes = {
                @Index(name = "idx_record_likes", columnList = "likes"),
                @Index(name = "idx_record_created_at", columnList = "created_at"),
                @Index(name = "idx_record_updated_at", columnList = "updated_at"),
        },
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"title", "author", "date", "deleted"}, name = "uk_record_title_author_date"),
                @UniqueConstraint(columnNames = {"link"}, name = "uk_record_link")
        })
public class Record {
    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String author;
    @Column(nullable = false)
    private LocalDateTime date;
    @PositiveOrZero
    private BigInteger views;
    private BigInteger likes;
    @Column(nullable = false)
    @URL
    private String link;
    private Boolean deleted;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Integer version;

    @PrePersist
    public void setDefaults() {
        if (views == null) {
            views = BigInteger.ZERO;
        }
        if (likes == null) {
            likes = BigInteger.ZERO;
        }
        deleted = false;
    }
}

package com.io.iotask.repository;

import com.io.iotask.repository.entity.Record;
import org.springframework.data.jpa.domain.Specification;

import java.util.UUID;

public class RecordSpecification {
    private RecordSpecification() {
    }

    public static Specification<Record> hasId(UUID id) {
        return (root, query, cb) -> id == null ? cb.conjunction() : cb.equal(root.get("id"), id);
    }

    public static Specification<Record> likeAuthor(String author) {
        return (root, query, cb) -> author == null ? cb.conjunction() : cb.like(root.get("author"), author);
    }

    public static Specification<Record> likeTitle(String title) {
        return (root, query, cb) -> title == null ? cb.conjunction() : cb.like(root.get("title"), title);
    }

    public static Specification<Record> isNotDeleted() {
        return (root, query, cb) -> cb.equal(root.get("deleted"), false);
    }
}

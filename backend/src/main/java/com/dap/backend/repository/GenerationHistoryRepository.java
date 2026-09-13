package com.dap.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * MongoDB repository for generation history records.
 */
public interface GenerationHistoryRepository extends MongoRepository<GenerationHistoryDocument, String> {

    /**
     * Returns all history records ordered newest-first.
     * Used by HistoryController to replace the former filesystem scan.
     */
    List<GenerationHistoryDocument> findAllByOrderByGeneratedAtDesc();

    /**
     * Looks up a history record by output filename.
     * Used by HistorySyncService to avoid duplicate imports.
     */
    Optional<GenerationHistoryDocument> findByFilename(String filename);
}

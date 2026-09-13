package com.dap.backend.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * MongoDB document representing a single document generation event.
 * Created whenever a document is generated (single or batch).
 * Maps to existing {@link com.dap.backend.model.HistoryItem} DTO via
 * {@code filename}, {@code size}, and {@code generatedAt.toEpochMilli()}.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "generation_history")
public class GenerationHistoryDocument {

    /** Auto-generated MongoDB ObjectId. */
    @Id
    private String id;

    /** Generated output filename, e.g. "MRM_20260705_153045.docx". */
    private String filename;

    /** Name of the template used for generation, e.g. "MRM". */
    private String templateName;

    /** File size in bytes. */
    private long size;

    /** UTC timestamp of generation. Maps to HistoryItem.lastModified via toEpochMilli(). */
    private Instant generatedAt;
}

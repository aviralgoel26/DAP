package com.dap.backend.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * MongoDB document representing template metadata.
 * <p>
 * The {@code id} field is the filesystem folder name (templateId), making it the
 * stable identity shared between MongoDB and the filesystem. This prevents
 * duplicate records and simplifies future S3 migration.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "templates")
public class TemplateDocument {

    /** MongoDB @Id = templateId = filesystem folder name (e.g. "MRM", "HR Policy"). */
    @Id
    private String id;

    /** Human-readable display name (currently same as id, can diverge later). */
    private String displayName;

    /** File type: "DOCX" or "XLSX". */
    private String fileType;

    /** Original uploaded filename (e.g. "template.docx"). */
    private String originalFilename;

    /** Absolute filesystem path to the template folder (Render-safe: from @Value). */
    private String filePath;

    /** Hex ObjectId string pointing to the actual binary file in MongoDB GridFS. */
    private String gridFsFileId;

    /** UTC timestamp when this template was uploaded or first synced. */
    private Instant uploadedAt;
}

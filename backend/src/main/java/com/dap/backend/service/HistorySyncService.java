package com.dap.backend.service;

import com.dap.backend.repository.GenerationHistoryDocument;
import com.dap.backend.repository.GenerationHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.Instant;

/**
 * One-time migration service that imports existing generated documents from the
 * filesystem into the MongoDB {@code generation_history} collection.
 * <p>
 * Exposed via {@code POST /api/admin/sync-history}. Safe to run multiple times —
 * completely idempotent. Never deletes files or existing MongoDB records.
 * </p>
 *
 * <p>Preserves the original file's {@code lastModified} timestamp so that
 * history ordering in the UI remains chronologically correct after migration.</p>
 */
@Service
public class HistorySyncService {

    private static final Logger logger = LoggerFactory.getLogger(HistorySyncService.class);

    private final GenerationHistoryRepository historyRepository;

    @Value("${generated.storage.path}")
    private String generatedPath;

    public HistorySyncService(GenerationHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    /**
     * Scans the generated documents directory and creates a {@link GenerationHistoryDocument}
     * for every file that does not already have a MongoDB record.
     *
     * @return number of new records created (0 if all already exist)
     */
    public int syncHistory() {
        File genDir = new File(generatedPath);
        File[] files = genDir.listFiles(File::isFile);

        if (files == null) {
            logger.warn("Generated storage path does not exist or is not a directory: {}", generatedPath);
            return 0;
        }

        int created = 0;

        for (File file : files) {
            String filename = file.getName();

            // Idempotency check — skip if already in MongoDB
            if (historyRepository.findByFilename(filename).isPresent()) {
                logger.debug("History entry for '{}' already in MongoDB — skipping", filename);
                continue;
            }

            GenerationHistoryDocument doc = GenerationHistoryDocument.builder()
                    .filename(filename)
                    .templateName(deriveTemplateName(filename))
                    .size(file.length())
                    .generatedAt(Instant.ofEpochMilli(file.lastModified()))
                    .build();

            historyRepository.save(doc);
            logger.info("Synced history record for '{}' into MongoDB", filename);
            created++;
        }

        logger.info("History sync complete: {} new record(s) created out of {} file(s) scanned",
                created, files.length);
        return created;
    }

    /**
     * Derives the template name from a generated output filename.
     * <p>
     * Generated filenames follow the format produced by {@link FileService#generateFileName}:
     * {@code {templateName}_{yyyyMMdd}_{HHmmss}.{ext}}
     * </p>
     * <p>Examples:</p>
     * <ul>
     *   <li>{@code MRM_20260705_153045.docx} → {@code MRM}</li>
     *   <li>{@code HR Policy_20260705_153045.docx} → {@code HR Policy}</li>
     *   <li>{@code generated_1234567890.zip} → best-effort, returns {@code generated}</li>
     * </ul>
     */
    private String deriveTemplateName(String filename) {
        // Strip extension
        String withoutExt = filename.contains(".")
                ? filename.substring(0, filename.lastIndexOf('.'))
                : filename;

        // Strip time segment (_HHmmss)
        int lastUnderscore = withoutExt.lastIndexOf('_');
        if (lastUnderscore < 0) {
            return withoutExt;
        }
        String withoutTime = withoutExt.substring(0, lastUnderscore);

        // Strip date segment (_yyyyMMdd)
        int secondLastUnderscore = withoutTime.lastIndexOf('_');
        if (secondLastUnderscore < 0) {
            return withoutTime;
        }

        return withoutTime.substring(0, secondLastUnderscore);
    }
}

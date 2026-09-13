package com.dap.backend.service;

import com.dap.backend.repository.TemplateDocument;
import com.dap.backend.repository.TemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.Instant;

/**
 * One-time migration service that imports existing filesystem templates into MongoDB.
 * <p>
 * Exposed via {@code POST /api/admin/sync-templates}. Safe to run multiple times —
 * completely idempotent. Never creates duplicate records, never deletes any data.
 * </p>
 *
 * <p>Uses the same file-type detection logic as the original TemplateService
 * (inspect files inside each template folder for .xlsx extension).</p>
 */
@Service
public class TemplateSyncService {

    private static final Logger logger = LoggerFactory.getLogger(TemplateSyncService.class);

    private final TemplateRepository templateRepository;

    @Value("${template.storage.path}")
    private String templatePath;

    public TemplateSyncService(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    /**
     * Scans the filesystem template directory and inserts a {@link TemplateDocument}
     * for every folder that does not already have a MongoDB record.
     *
     * @return number of new records created (0 if all already exist)
     */
    public int syncTemplates() {
        File root = new File(templatePath);
        File[] dirs = root.listFiles(File::isDirectory);

        if (dirs == null) {
            logger.warn("Template storage path does not exist or is not a directory: {}", templatePath);
            return 0;
        }

        int created = 0;

        for (File dir : dirs) {
            String templateId = dir.getName();

            // Idempotency check — skip if already in MongoDB
            if (templateRepository.existsById(templateId)) {
                logger.debug("Template '{}' already in MongoDB — skipping", templateId);
                continue;
            }

            String fileType = resolveFileType(dir);
            String originalFilename = resolveOriginalFilename(dir, fileType);

            TemplateDocument doc = TemplateDocument.builder()
                    .id(templateId)
                    .displayName(templateId)
                    .fileType(fileType)
                    .originalFilename(originalFilename)
                    .filePath(dir.getAbsolutePath())
                    .uploadedAt(Instant.ofEpochMilli(dir.lastModified()))
                    .build();

            templateRepository.save(doc);
            logger.info("Synced template '{}' ({}) into MongoDB", templateId, fileType);
            created++;
        }

        logger.info("Template sync complete: {} new record(s) created out of {} folder(s) scanned",
                created, dirs.length);
        return created;
    }

    /**
     * Detects template type by inspecting files inside the template folder.
     * Mirrors the original TemplateService.resolveTemplateType() logic exactly.
     */
    private String resolveFileType(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.getName().toLowerCase().endsWith(".xlsx")) {
                    return "XLSX";
                }
            }
        }
        return "DOCX";
    }

    /**
     * Finds the actual template filename inside the folder.
     * Falls back to a synthesized name if no file is found.
     */
    private String resolveOriginalFilename(File dir, String fileType) {
        File[] files = dir.listFiles();
        if (files != null) {
            String ext = "." + fileType.toLowerCase();
            for (File f : files) {
                if (f.getName().toLowerCase().endsWith(ext)) {
                    return f.getName();
                }
            }
        }
        return dir.getName() + "." + fileType.toLowerCase();
    }
}

package com.dap.backend.service;

import com.dap.backend.repository.TemplateDocument;
import com.dap.backend.repository.TemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Migration service that imports existing filesystem templates into MongoDB metadata
 * and MongoDB GridFS binary storage.
 * <p>
 * Exposed via {@code POST /api/admin/sync-templates}. Safe to run multiple times —
 * completely idempotent. Never creates duplicate GridFS files, never deletes any data.
 * </p>
 */
@Service
public class TemplateSyncService {

    private static final Logger logger = LoggerFactory.getLogger(TemplateSyncService.class);

    private final TemplateRepository templateRepository;
    private final TemplateFileStorageService templateFileStorageService;

    @Value("${template.storage.path}")
    private String templatePath;

    public TemplateSyncService(TemplateRepository templateRepository,
                               TemplateFileStorageService templateFileStorageService) {
        this.templateRepository = templateRepository;
        this.templateFileStorageService = templateFileStorageService;
    }

    /**
     * Scans the filesystem template directory and synchronizes templates with MongoDB and GridFS.
     * <ul>
     *   <li>If template metadata exists and already has a valid GridFS file, skips to prevent duplicates.</li>
     *   <li>If template metadata exists but GridFS file is missing, uploads binary to GridFS and updates metadata.</li>
     *   <li>If template does not exist in MongoDB, uploads binary to GridFS and creates new metadata record.</li>
     * </ul>
     *
     * @return map with counts of scanned, created, updatedWithGridFs, and alreadyUpToDate templates
     */
    public Map<String, Object> syncTemplates() {
        File root = new File(templatePath);
        File[] dirs = root.listFiles(File::isDirectory);

        if (dirs == null) {
            logger.warn("Template storage path does not exist or is not a directory: {}", templatePath);
            Map<String, Object> emptyResult = new LinkedHashMap<>();
            emptyResult.put("status", "ok");
            emptyResult.put("scanned", 0);
            emptyResult.put("created", 0);
            emptyResult.put("updatedWithGridFs", 0);
            emptyResult.put("alreadyUpToDate", 0);
            emptyResult.put("newRecordsCreated", 0);
            return emptyResult;
        }

        int created = 0;
        int updatedWithGridFs = 0;
        int alreadyUpToDate = 0;

        for (File dir : dirs) {
            String templateId = dir.getName();
            File templateFile = findTemplateFile(dir);
            if (templateFile == null) {
                logger.warn("No .docx or .xlsx template file found in directory '{}' — skipping", templateId);
                continue;
            }

            String fileType = templateFile.getName().toLowerCase().endsWith(".xlsx") ? "XLSX" : "DOCX";
            String contentType = fileType.equals("XLSX")
                    ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                    : "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

            Optional<TemplateDocument> existingOpt = templateRepository.findById(templateId);

            if (existingOpt.isPresent()) {
                TemplateDocument doc = existingOpt.get();

                // 1. If metadata exists and already has a valid GridFS file -> skip upload
                if (doc.getGridFsFileId() != null && templateFileStorageService.exists(doc.getGridFsFileId())) {
                    logger.debug("Template '{}' already has valid GridFS file ({}) — skipping duplicate upload",
                            templateId, doc.getGridFsFileId());
                    alreadyUpToDate++;
                    continue;
                }

                // 2. If metadata exists but gridFsFileId is missing or points to a deleted GridFS file
                try (InputStream is = new FileInputStream(templateFile)) {
                    String gridFsId = templateFileStorageService.storeTemplate(
                            is, templateFile.getName(), contentType, templateId
                    );
                    doc.setGridFsFileId(gridFsId);
                    if (doc.getFileType() == null) doc.setFileType(fileType);
                    if (doc.getOriginalFilename() == null) doc.setOriginalFilename(templateFile.getName());
                    templateRepository.save(doc);
                    logger.info("Uploaded template file '{}' to GridFS ({}) and updated metadata", templateId, gridFsId);
                    updatedWithGridFs++;
                } catch (Exception e) {
                    logger.error("Failed to sync GridFS file for existing template '{}': {}", templateId, e.getMessage(), e);
                }
            } else {
                // 3. Metadata does not exist -> create metadata + GridFS file
                try (InputStream is = new FileInputStream(templateFile)) {
                    String gridFsId = templateFileStorageService.storeTemplate(
                            is, templateFile.getName(), contentType, templateId
                    );
                    TemplateDocument doc = TemplateDocument.builder()
                            .id(templateId)
                            .displayName(templateId)
                            .fileType(fileType)
                            .originalFilename(templateFile.getName())
                            .gridFsFileId(gridFsId)
                            .filePath(dir.getAbsolutePath())
                            .uploadedAt(Instant.ofEpochMilli(dir.lastModified()))
                            .build();
                    templateRepository.save(doc);
                    logger.info("Created new template '{}' with GridFS file ({}) in MongoDB", templateId, gridFsId);
                    created++;
                } catch (Exception e) {
                    logger.error("Failed to upload new template '{}' to GridFS: {}", templateId, e.getMessage(), e);
                }
            }
        }

        logger.info("Template sync complete: {} created, {} updated with GridFS, {} already up-to-date out of {} folders",
                created, updatedWithGridFs, alreadyUpToDate, dirs.length);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", "ok");
        result.put("scanned", dirs.length);
        result.put("created", created);
        result.put("updatedWithGridFs", updatedWithGridFs);
        result.put("alreadyUpToDate", alreadyUpToDate);
        result.put("newRecordsCreated", created + updatedWithGridFs);
        return result;
    }

    /**
     * Finds the template file (.docx or .xlsx) inside a template folder.
     */
    private File findTemplateFile(File dir) {
        File[] files = dir.listFiles((d, name) -> {
            String lower = name.toLowerCase();
            return lower.endsWith(".docx") || lower.endsWith(".xlsx");
        });

        if (files != null && files.length > 0) {
            return files[0];
        }
        return null;
    }
}

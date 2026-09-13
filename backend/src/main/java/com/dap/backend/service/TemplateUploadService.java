package com.dap.backend.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dap.backend.repository.TemplateDocument;
import com.dap.backend.repository.TemplateRepository;

/**
 * Service for handling template file uploads and deletions.
 * <p>
 * Actual template binaries are stored in MongoDB GridFS via {@link TemplateFileStorageService},
 * eliminating dependency on Render's ephemeral filesystem.
 * </p>
 */
@Service
public class TemplateUploadService {

    private static final Logger logger = LoggerFactory.getLogger(TemplateUploadService.class);

    @Value("${template.storage.path}")
    private String templatePath;

    private final TemplateRepository templateRepository;
    private final TemplateFileStorageService templateFileStorageService;

    public TemplateUploadService(TemplateRepository templateRepository,
                                 TemplateFileStorageService templateFileStorageService) {
        this.templateRepository = templateRepository;
        this.templateFileStorageService = templateFileStorageService;
    }

    /**
     * Saves an uploaded template file to MongoDB GridFS, then persists metadata to MongoDB.
     * <p>
     * If saving metadata fails, the newly uploaded GridFS file is cleaned up.
     * If replacing an existing template, the old GridFS file is deleted to avoid orphaned binaries.
     * </p>
     *
     * @param templateId the unique identifier for the template
     * @param file       the uploaded template file (.docx or .xlsx)
     * @return confirmation message string
     * @throws IOException if file reading fails
     */
    public String uploadTemplate(String templateId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty() || file.getOriginalFilename() == null) {
            throw new IllegalArgumentException("Template file cannot be empty.");
        }

        String fileType = resolveFileType(file.getOriginalFilename());
        String contentType = file.getContentType();
        if (contentType == null || contentType.isBlank()) {
            contentType = fileType.equals("XLSX")
                    ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                    : "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        }

        // Check if an existing template with this ID has an old GridFS file
        String oldGridFsFileId = templateRepository.findById(templateId)
                .map(TemplateDocument::getGridFsFileId)
                .orElse(null);

        // 1. Store binary in GridFS
        String gridFsFileId = templateFileStorageService.storeTemplate(
                file.getInputStream(),
                file.getOriginalFilename(),
                contentType,
                templateId
        );

        // 2. Persist metadata in MongoDB
        TemplateDocument doc = TemplateDocument.builder()
                .id(templateId)
                .displayName(templateId)
                .fileType(fileType)
                .originalFilename(file.getOriginalFilename())
                .gridFsFileId(gridFsFileId)
                .uploadedAt(Instant.now())
                .build();

        try {
            templateRepository.save(doc);
            logger.info("Template '{}' metadata saved to MongoDB with GridFS ID: {}", templateId, gridFsFileId);
        } catch (Exception e) {
            // Roll back newly uploaded GridFS file if metadata persistence fails
            logger.error("Failed to save TemplateDocument for '{}', cleaning up GridFS file '{}': {}",
                    templateId, gridFsFileId, e.getMessage());
            templateFileStorageService.deleteTemplate(gridFsFileId);
            throw e;
        }

        // 3. Delete old GridFS file if replacing an existing template
        if (oldGridFsFileId != null && !oldGridFsFileId.equals(gridFsFileId)) {
            templateFileStorageService.deleteTemplate(oldGridFsFileId);
            logger.info("Cleaned up previous GridFS file '{}' for template '{}'", oldGridFsFileId, templateId);
        }

        // 4. Best-effort local filesystem cache (for development/local inspection)
        try {
            Path folder = Paths.get(templatePath, templateId);
            Files.createDirectories(folder);
            Path destination = folder.resolve(file.getOriginalFilename());
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            logger.debug("Local disk cache skipped for template '{}': {}", templateId, e.getMessage());
        }

        return "Template uploaded successfully.";
    }

    /**
     * Deletes a template: removes the GridFS binary file and the MongoDB metadata record.
     * Also performs best-effort cleanup of any local directory if present.
     *
     * @param templateId the identifier of the template to delete
     * @throws IllegalArgumentException if the template does not exist
     */
    public void deleteTemplate(String templateId) throws IOException {
        TemplateDocument doc = templateRepository.findById(templateId).orElse(null);
        Path tempPath = Paths.get(templatePath, templateId);

        if (doc == null && !Files.exists(tempPath)) {
            throw new IllegalArgumentException("Template not found: " + templateId);
        }

        // 1. Delete binary from GridFS if present
        if (doc != null && doc.getGridFsFileId() != null) {
            templateFileStorageService.deleteTemplate(doc.getGridFsFileId());
            logger.info("Template '{}' binary deleted from GridFS ({})", templateId, doc.getGridFsFileId());
        }

        // 2. Delete metadata from MongoDB
        if (doc != null) {
            templateRepository.deleteById(templateId);
            logger.info("Template '{}' metadata removed from MongoDB", templateId);
        }

        // 3. Best-effort local directory cleanup
        if (Files.exists(tempPath)) {
            try {
                Files.walk(tempPath)
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
                logger.info("Template '{}' directory deleted from local filesystem", templateId);
            } catch (Exception e) {
                logger.warn("Could not delete local filesystem directory for '{}': {}", templateId, e.getMessage());
            }
        }
    }

    /**
     * Determines the template file type from the original filename.
     *
     * @param filename the original filename
     * @return {@code "XLSX"} for .xlsx files, {@code "DOCX"} otherwise
     */
    private String resolveFileType(String filename) {
        if (filename != null && filename.toLowerCase().endsWith(".xlsx")) {
            return "XLSX";
        }
        return "DOCX";
    }
}
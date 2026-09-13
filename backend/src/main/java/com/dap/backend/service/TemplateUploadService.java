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
 * Filesystem operations are unchanged. MongoDB metadata is saved/removed
 * alongside each filesystem operation to keep both stores in sync.
 * </p>
 */
@Service
public class TemplateUploadService {

    private static final Logger logger = LoggerFactory.getLogger(TemplateUploadService.class);

    @Value("${template.storage.path}")
    private String templatePath;

    private final TemplateRepository templateRepository;

    public TemplateUploadService(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    /**
     * Saves an uploaded template file to the template storage directory under the given ID,
     * then persists metadata to MongoDB.
     * <p>
     * If a template with this ID already exists in MongoDB, the metadata is overwritten
     * (upsert) so that re-uploads stay consistent.
     * </p>
     *
     * @param templateId the unique identifier used to name the template subdirectory
     * @param file       the uploaded template file (.docx or .xlsx)
     * @return the absolute path of the saved file as a string
     * @throws IOException if directory creation or file writing fails
     */
    public String uploadTemplate(String templateId, MultipartFile file) throws IOException {

        Path folder = Paths.get(templatePath, templateId);
        Files.createDirectories(folder);

        Path destination = folder.resolve(file.getOriginalFilename());
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        logger.info("Template '{}' uploaded to: {}", templateId, destination);

        // Persist metadata to MongoDB (upsert — same id overwrites on re-upload)
        String fileType = resolveFileType(file.getOriginalFilename());

        TemplateDocument doc = TemplateDocument.builder()
                .id(templateId)
                .displayName(templateId)
                .fileType(fileType)
                .originalFilename(file.getOriginalFilename())
                .filePath(folder.toAbsolutePath().toString())
                .uploadedAt(Instant.now())
                .build();

        templateRepository.save(doc);
        logger.info("Template '{}' metadata saved to MongoDB", templateId);

        return destination.toString();
    }

    /**
     * Deletes a template: removes the MongoDB metadata record first, then deletes
     * the filesystem directory. Order is intentional — if the FS delete fails,
     * the MongoDB record is already removed so the template won't appear in listings.
     *
     * @param templateId the identifier of the template to delete
     * @throws IOException              if the filesystem directory cannot be deleted
     * @throws IllegalArgumentException if the template folder does not exist on disk
     */
    public void deleteTemplate(String templateId) throws IOException {

        Path tempPath = Paths.get(templatePath, templateId);

        if (!Files.exists(tempPath)) {
            throw new IllegalArgumentException("Template not found.");
        }

        // Remove from MongoDB first
        templateRepository.deleteById(templateId);
        logger.info("Template '{}' metadata removed from MongoDB", templateId);

        // Then remove from filesystem
        Files.walk(tempPath)
                .sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);

        logger.info("Template '{}' directory deleted from filesystem", templateId);
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
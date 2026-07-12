package com.dap.backend.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service for handling template file uploads. Saves the uploaded file into a
 * dedicated subdirectory under the configured template storage path.
 */
@Service
public class TemplateUploadService {

    private static final Logger logger = LoggerFactory.getLogger(TemplateUploadService.class);

    @Value("${template.storage.path}")
    private String templatePath;

    /**
     * Saves an uploaded template file to the template storage directory under the given ID.
     * Creates the target directory if it does not already exist.
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
        return destination.toString();
    }
}
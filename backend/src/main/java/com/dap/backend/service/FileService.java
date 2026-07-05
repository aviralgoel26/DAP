package com.dap.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class FileService {

    @Value("${template.storage.path}")
    private String templatePath;

    @Value("${generated.storage.path}")
    private String generatedPath;

    /**
     * Generates a unique filename.
     * Example:
     * MRM_20260705_153045.docx
     */
    public String generateFileName(String templateName) {

        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        return templateName + "_" + timestamp + ".docx";
    }

    /**
     * Returns the template path.
     */
    public String buildTemplatePath(String templateName) {

        return templatePath + "/" + templateName + "/template.docx";

    }

    /**
     * Returns the generated output path.
     */
    public String buildOutputPath(String generatedFileName) {

        return generatedPath + "/" + generatedFileName;

    }

    /**
     * Copies template to generated folder.
     */
    public void copyTemplate(String source, String destination)
            throws IOException {

        Files.copy(
                Paths.get(source),
                Paths.get(destination),
                StandardCopyOption.REPLACE_EXISTING
        );

    }

}
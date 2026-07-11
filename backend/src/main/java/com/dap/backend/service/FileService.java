package com.dap.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.File;
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
    public String generateFileName(
        String templateName,
        String templateFile) {

    String timestamp = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

    String extension =
            templateFile.substring(
                    templateFile.lastIndexOf(".")
            );

    return templateName + "_" +
            timestamp +
            extension;

}

    /**
     * Returns the template path.
     */
    public String findTemplateFile(String templateId) {

    File folder = new File(templatePath, templateId);

    if (!folder.exists() || !folder.isDirectory()) {
        throw new RuntimeException(
                "Template folder not found : " + templateId
        );
    }

    File[] files = folder.listFiles(
            (dir, name) ->
                    name.endsWith(".docx") ||
                    name.endsWith(".xlsx")
    );

    if (files == null || files.length == 0) {

        throw new RuntimeException(
                "No template found inside folder : " + templateId
        );

    }

    return files[0].getAbsolutePath();

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
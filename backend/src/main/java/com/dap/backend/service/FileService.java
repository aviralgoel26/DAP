package com.dap.backend.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for file system operations: resolving template paths, generating output filenames,
 * building output paths, and copying template files to the generated folder.
 */
@Service
public class FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);

    @Value("${template.storage.path}")
    private String templatePath;

    @Value("${generated.storage.path}")
    private String generatedPath;

    /**
     * Generates a unique output filename based on the template name, current timestamp,
     * and the original file extension.
     * <p>Example: {@code MRM_20260705_153045.docx}</p>
     *
     * @param templateName the template identifier used as the filename prefix
     * @param templateFile the source template file path (used to extract the extension)
     * @return a unique filename string
     */
    public String generateFileName(String templateName, String templateFile) {

        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        String extension = templateFile.substring(templateFile.lastIndexOf("."));

        return templateName + "_" + timestamp + extension;
    }

    /**
     * Locates and returns the absolute path of the template file (.docx or .xlsx)
     * inside the given template folder.
     *
     * @param templateId the name of the template folder
     * @return the absolute path of the template file
     * @throws RuntimeException if the folder does not exist or contains no supported template file
     */
    public String findTemplateFile(String templateId) {

        File folder = new File(templatePath, templateId);

        if (!folder.exists() || !folder.isDirectory()) {
            throw new RuntimeException("Template folder not found: " + templateId);
        }

        File[] files = folder.listFiles(
                (dir, name) -> name.endsWith(".docx") || name.endsWith(".xlsx")
        );

        if (files == null || files.length == 0) {
            throw new RuntimeException("No template found inside folder: " + templateId);
        }

        return files[0].getAbsolutePath();
    }

    /**
     * Builds the absolute output file path for a generated document.
     *
     * @param generatedFileName the generated filename (e.g. {@code MRM_20260705_153045.docx})
     * @return the absolute path string in the generated storage directory
     */
    public String buildOutputPath(String generatedFileName) {

        return Paths.get(generatedPath, generatedFileName).toString();
    }

    /**
     * Copies a template file to the generated output directory, replacing any existing file.
     *
     * @param source      the absolute path of the source template file
     * @param destination the absolute path of the destination file
     * @throws IOException if the copy operation fails
     */
    public void copyTemplate(String source, String destination) throws IOException {

        Path src = Paths.get(source);
        Path dst = Paths.get(destination);

        logger.debug("Copying template from '{}' to '{}'", source, destination);
        Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
    }



    public String getGeneratedDirectory() {

    return generatedPath;

}
}
package com.dap.backend.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dap.backend.model.TemplateInfo;

/**
 * Service for listing available document templates from the template storage directory.
 */
@Service
public class TemplateService {

    private static final Logger logger = LoggerFactory.getLogger(TemplateService.class);

    @Value("${template.storage.path}")
    private String templatePath;

    /**
     * Returns all templates found in the template storage directory.
     * Each template is represented as a folder containing a {@code .docx} or {@code .xlsx} file.
     * The template type is determined from the actual file found inside the folder.
     *
     * @return a list of {@link TemplateInfo} objects, one per template folder; empty if none found
     */
    public List<TemplateInfo> getAllTemplates() {

        List<TemplateInfo> templates = new ArrayList<>();
        File folder = new File(templatePath);
        File[] directories = folder.listFiles();

        if (directories == null) {
            logger.warn("Template storage path does not exist or is not a directory: {}", templatePath);
            return templates;
        }

        for (File dir : directories) {

            if (!dir.isDirectory()) {
                continue;
            }

            String type = resolveTemplateType(dir);
            templates.add(new TemplateInfo(dir.getName(), type));
        }

        logger.debug("Found {} template(s) in '{}'", templates.size(), templatePath);
        return templates;
    }

    /**
     * Detects the template type by inspecting the files inside the template folder.
     *
     * @param templateDir the template folder
     * @return {@code "XLSX"} if an .xlsx file is found, {@code "DOCX"} otherwise
     */
    private String resolveTemplateType(File templateDir) {

        File[] files = templateDir.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.getName().toLowerCase().endsWith(".xlsx")) {
                    return "XLSX";
                }
            }
        }

        return "DOCX";
    }
}
package com.dap.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dap.backend.model.TemplateInfo;
import com.dap.backend.repository.TemplateDocument;
import com.dap.backend.repository.TemplateRepository;

/**
 * Service for listing available document templates.
 * Previously scanned the filesystem directory at request time;
 * now reads persisted metadata from MongoDB for reliability across restarts.
 * <p>
 * The filesystem template files remain unchanged — MongoDB stores metadata only.
 * </p>
 */
@Service
public class TemplateService {

    private static final Logger logger = LoggerFactory.getLogger(TemplateService.class);

    private final TemplateRepository templateRepository;

    public TemplateService(TemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    /**
     * Returns all templates stored in MongoDB.
     * Each template is mapped to a {@link TemplateInfo} using the templateId (folder name)
     * and the persisted file type — preserving the existing API response format exactly.
     *
     * @return list of {@link TemplateInfo} objects; empty if no templates have been synced yet
     */
    public List<TemplateInfo> getAllTemplates() {
        List<TemplateInfo> templates = templateRepository.findAll()
                .stream()
                .map(this::toTemplateInfo)
                .collect(Collectors.toList());

        logger.debug("Returning {} template(s) from MongoDB", templates.size());
        return templates;
    }

    private TemplateInfo toTemplateInfo(TemplateDocument doc) {
        String type = resolveFileType(doc);
        String fileType = type != null ? type.toLowerCase() : null;
        String fileName = doc.getOriginalFilename();
        return new TemplateInfo(doc.getId(), type, fileType, fileName);
    }

    private String resolveFileType(TemplateDocument doc) {
        if (doc.getFileType() != null && !doc.getFileType().isBlank()) {
            return doc.getFileType().toUpperCase();
        }
        if (doc.getOriginalFilename() != null && !doc.getOriginalFilename().isBlank()) {
            String lower = doc.getOriginalFilename().toLowerCase().trim();
            if (lower.endsWith(".xlsx")) {
                return "XLSX";
            }
            if (lower.endsWith(".docx")) {
                return "DOCX";
            }
        }
        if (doc.getId() != null) {
            String lower = doc.getId().toLowerCase().trim();
            if (lower.endsWith(".xlsx")) {
                return "XLSX";
            }
            if (lower.endsWith(".docx")) {
                return "DOCX";
            }
        }
        return "DOCX";
    }
}
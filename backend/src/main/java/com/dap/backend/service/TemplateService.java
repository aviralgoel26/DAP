package com.dap.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dap.backend.model.TemplateInfo;
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
                .map(doc -> new TemplateInfo(doc.getId(), doc.getFileType()))
                .collect(Collectors.toList());

        logger.debug("Returning {} template(s) from MongoDB", templates.size());
        return templates;
    }
}
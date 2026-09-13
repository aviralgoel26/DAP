package com.dap.backend.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dap.backend.model.TemplateInfo;
import com.dap.backend.repository.TemplateDocument;
import com.dap.backend.repository.TemplateRepository;
import com.dap.backend.service.TemplateFileStorageService;
import com.dap.backend.service.TemplateService;
import com.dap.backend.service.TemplateUploadService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * REST controller for template listing, upload, preview, and deletion operations.
 */
@Tag(
        name = "Template API",
        description = "APIs for uploading, listing, previewing, and managing templates"
)
@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    private static final Logger logger = LoggerFactory.getLogger(TemplateController.class);

    @Autowired
    private TemplateService templateService;

    @Autowired
    private TemplateUploadService templateUploadService;

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private TemplateFileStorageService templateFileStorageService;

    /**
     * Returns a list of all available templates.
     *
     * @return a list of {@link TemplateInfo} objects describing each template
     */
    @Operation(
            summary = "List Templates",
            description = "Returns all available templates."
    )
    @GetMapping
    public List<TemplateInfo> getTemplates() {
        logger.info("Fetching all templates");
        return templateService.getAllTemplates();
    }

    /**
     * Uploads a new template file and registers it in MongoDB GridFS and metadata.
     *
     * @param templateId the unique identifier for the template
     * @param file       the template file (.docx or .xlsx) to upload
     * @return confirmation message string
     * @throws IOException if the file cannot be read
     */
    @Operation(
            summary = "Upload Template",
            description = "Uploads a new Word or Excel template to MongoDB GridFS."
    )
    @PostMapping("/upload")
    public String uploadTemplate(
            @RequestParam String templateId,
            @RequestParam MultipartFile file) throws IOException {

        logger.info("Uploading template '{}' (file: {})", templateId, file.getOriginalFilename());
        return templateUploadService.uploadTemplate(templateId, file);
    }

    /**
     * Deletes a template from MongoDB GridFS and metadata repository.
     *
     * @param templateId the identifier of the template to delete
     * @return status message
     * @throws IOException if deletion encounters an error
     */
    @Operation(
            summary = "Delete Template",
            description = "Deletes a template and its binary files from MongoDB."
    )
    @DeleteMapping("/{templateId}")
    public String deleteTemplate(@PathVariable String templateId) throws IOException {
        logger.info("Deleting template {}", templateId);
        templateUploadService.deleteTemplate(templateId);
        return "Template deleted successfully.";
    }

    /**
     * Streams the template binary directly from MongoDB GridFS for browser preview/download.
     *
     * @param templateName the name of the template
     * @return an {@link InputStreamResource} streaming the template file
     * @throws IOException if streaming fails
     */
    @Operation(
            summary = "Preview Template",
            description = "Streams the template file directly from MongoDB GridFS for preview."
    )
    @GetMapping("/preview/{templateName}")
    public ResponseEntity<InputStreamResource> previewTemplate(
            @PathVariable String templateName) throws IOException {

        logger.info("Preview requested for template: {}", templateName);

        TemplateDocument doc = templateRepository.findById(templateName)
                .orElseThrow(() -> new IllegalArgumentException("Template not found: " + templateName));

        if (doc.getGridFsFileId() == null) {
            throw new IllegalArgumentException("Template binary not found in storage for: " + templateName);
        }

        GridFsResource resource = templateFileStorageService.getTemplateResource(doc.getGridFsFileId());

        String contentType = resource.getContentType();
        if (contentType == null || contentType.isBlank()) {
            if ("XLSX".equalsIgnoreCase(doc.getFileType())) {
                contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            } else if ("DOCX".equalsIgnoreCase(doc.getFileType())) {
                contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            } else {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }
        }

        String filename = doc.getOriginalFilename() != null ? doc.getOriginalFilename() : resource.getFilename();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType(contentType))
                .body(new InputStreamResource(resource.getInputStream()));
    }
}
package com.dap.backend.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.dap.backend.model.TemplateInfo;
import com.dap.backend.service.TemplateService;
import com.dap.backend.service.TemplateUploadService;

import io.swagger.v3.oas.annotations.Operation;
/**
 * REST controller for template listing and upload operations.
 */
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
        name = "Template API",
        description = "APIs for uploading, listing and managing templates"
)
@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    private static final Logger logger = LoggerFactory.getLogger(TemplateController.class);

    @Autowired
    private TemplateService templateService;

    @Autowired
    private TemplateUploadService templateUploadService;

    /**
     * Returns a list of all available templates.
     *
     * @return a list of {@link TemplateInfo} objects describing each template
     */
    @Operation(
    summary="List Templates",
    description="Returns all available templates."
)
    @GetMapping
    public List<TemplateInfo> getTemplates() {

        logger.info("Fetching all templates");
        return templateService.getAllTemplates();
    }

    /**
     * Uploads a new template file and registers it under the given template ID.
     *
     * @param templateId the unique identifier for the template
     * @param file       the template file (.docx or .xlsx) to upload
     * @return the absolute path where the template was saved
     * @throws IOException if the file cannot be written to disk
     */
    @Operation(
    summary="Upload Template",
    description="Uploads a new Word or Excel template."
)
    @PostMapping("/upload")
    public String uploadTemplate(
            @RequestParam String templateId,
            @RequestParam MultipartFile file) throws IOException {

        logger.info("Uploading template '{}' (file: {})", templateId, file.getOriginalFilename());
        return templateUploadService.uploadTemplate(templateId, file);
    }

    @Operation(
    summary = "Delete Template",
    description = "Deletes a template and its files."
)
@DeleteMapping("/{templateId}")
public String deleteTemplate(
        @PathVariable String templateId
) throws IOException {

    logger.info("Deleting template {}", templateId);

    templateUploadService.deleteTemplate(templateId);

    return "Template deleted successfully.";

}
}
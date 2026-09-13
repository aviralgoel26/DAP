package com.dap.backend.controller;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.nio.file.Files;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpHeaders;
import com.dap.backend.model.TemplateInfo;
import com.dap.backend.service.TemplateService;
import com.dap.backend.service.TemplateUploadService;
import com.dap.backend.service.FileService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    
    @Autowired
    private FileService fileService;

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
@GetMapping("/preview/{templateName}")
public ResponseEntity<InputStreamResource> previewTemplate(
        @PathVariable String templateName) throws IOException {

    String path = fileService.findTemplateFile(templateName);

    File file = new File(path);

    if (!file.exists()) {
        throw new IllegalArgumentException("Template not found.");
    }

    InputStreamResource resource =
            new InputStreamResource(new FileInputStream(file));

    String contentType = Files.probeContentType(file.toPath());

    if (contentType == null) {

        if (path.endsWith(".docx")) {
            contentType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
        } else if (path.endsWith(".xlsx")) {
            contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        } else {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
    }

    return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                    "inline; filename=\"" + file.getName() + "\"")
            .contentLength(file.length())
            .contentType(MediaType.parseMediaType(contentType))
            .body(resource);
}
}
package com.dap.backend.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dap.backend.model.BatchRequest;
import com.dap.backend.model.BatchResponse;
import com.dap.backend.service.BatchGenerationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
/**
 * REST controller for batch document generation and ZIP download operations.
 */
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
        name = "Batch API",
        description = "APIs for generating multiple documents and ZIP downloads"
)
@RestController
@RequestMapping("/api/batch")
public class BatchController {

    private static final Logger logger = LoggerFactory.getLogger(BatchController.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private BatchGenerationService batchGenerationService;

    @Value("${generated.storage.path}")
    private String generatedPath;

    /**
     * Generates a batch of documents from multiple templates and packages them into a ZIP file.
     *
     * @param request the batch request containing template names and placeholder values
     * @param logo    optional logo image to embed in the generated documents
     * @return a {@link BatchResponse} containing a status message and the ZIP filename
     */
    @Operation(
    summary="Generate Batch",
    description="Generates multiple documents and returns a ZIP file."
)
    @PostMapping(
        value = "/generate",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
)
public BatchResponse generateBatch(

        @RequestPart("data") String json,

        @RequestPart(
                value = "logo",
                required = false
        ) MultipartFile logo

) throws Exception {

    BatchRequest request =
            objectMapper.readValue(
                    json,
                    BatchRequest.class
            );

    logger.info(
            "Batch generation requested for {} template(s)",
            request.getTemplates().size()
    );

    return batchGenerationService.generateBatch(
            request,
            logo
    );
}

    /**
     * Downloads a previously generated ZIP file by name.
     *
     * @param zipName the name of the ZIP file to download
     * @return a {@link ResponseEntity} streaming the ZIP file, or 404 if not found
     * @throws IOException if the file cannot be read
     */
    @Operation(
    summary="Download ZIP",
    description="Downloads the generated ZIP archive."
)
    @GetMapping("/download/{zipName}")
    public ResponseEntity<Resource> downloadZip(@PathVariable String zipName) throws IOException {

        logger.info("ZIP download requested: {}", zipName);

        Path path = Paths.get(generatedPath, zipName);
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            logger.warn("ZIP file not found: {}", zipName);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + zipName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}

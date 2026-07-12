package com.dap.backend.controller;

import java.io.*;
import java.util.Set;
import java.nio.file.Paths;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dap.backend.model.DocumentRequest;
import com.dap.backend.model.DocumentResponse;
import com.dap.backend.service.DocumentEngineService;
import com.dap.backend.service.FileService;
import com.dap.backend.service.PlaceholderDiscoveryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validator;
import jakarta.validation.ConstraintViolation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
/**
 * REST controller for single-document generation and placeholder discovery.
 */
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(
        name = "Document API",
        description = "APIs for generating Word and Excel documents"
)
@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private DocumentEngineService engineService;

    @Autowired
    private PlaceholderDiscoveryService placeholderDiscoveryService;

    @Autowired
    private FileService fileService;

    @Autowired
    private Validator validator;

    /**
     * Reads the raw text content of a Word template.
     *
     * @param templateName the name of the template folder
     * @return the plain text content of the template document
     */
    @GetMapping("/read/{templateName}")
    public String readDocument(@PathVariable String templateName) {

        logger.info("Reading document for template: {}", templateName);
        return engineService.readDocument(templateName);
    }

    /**
     * Generates a single document from a template by replacing placeholders.
     *
     * @param json the JSON-serialized {@link DocumentRequest}
     * @param logo optional logo image to embed in the document
     * @return a {@link DocumentResponse} with a status message and the generated filename
     * @throws Exception if JSON parsing or document generation fails
     */
    @Operation(
        summary = "Generate Document",
        description = "Generates a Word or Excel document from a selected template using the provided placeholders and optional logo."
)
@ApiResponses({

        @ApiResponse(
                responseCode = "200",
                description = "Document generated successfully"
        ),

        @ApiResponse(
                responseCode = "400",
                description = "Invalid request"
        ),

        @ApiResponse(
                responseCode = "500",
                description = "Internal server error"
        )

})
    @PostMapping(value = "/generate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DocumentResponse generateDocument(
            @RequestPart("data") String json,
            @RequestPart(value = "logo", required = false) MultipartFile logo) throws Exception {

        DocumentRequest request = objectMapper.readValue(json, DocumentRequest.class);
        Set<ConstraintViolation<DocumentRequest>> violations =
        validator.validate(request);

if (!violations.isEmpty()) {

    throw new IllegalArgumentException(
            violations.iterator().next().getMessage()
    );

}

        logger.info("Generating document for template: {}", request.getTemplateName());
        return engineService.generateDocument(request, logo);
    }

    /**
     * Discovers all placeholder keys (e.g. {@code name}, {@code date}) present in a Word template.
     *
     * @param templateName the name of the template folder
     * @return a set of placeholder key names found in the template
     * @throws IOException if the template file cannot be read
     */
    
    @GetMapping("/{templateName}/placeholders")
    public Set<String> discoverPlaceholders(@PathVariable String templateName) throws IOException {

        logger.info("Discovering placeholders for template: {}", templateName);

        String path = fileService.findTemplateFile(templateName);

        try (FileInputStream fis = new FileInputStream(path);
             XWPFDocument document = new XWPFDocument(fis)) {

            return placeholderDiscoveryService.discoverPlaceholders(document);
        }
    }

    @GetMapping("/download/{fileName}")
public ResponseEntity<InputStreamResource> downloadDocument(
        @PathVariable String fileName) throws IOException {
String path =
        Paths.get(
                fileService.getGeneratedDirectory(),
                fileName
        ).toString();

    File file = new File(path);

    if (!file.exists()) {

        throw new IllegalArgumentException(
                "Generated file not found."
        );

    }

    InputStreamResource resource =
            new InputStreamResource(
                    new FileInputStream(file)
            );

    return ResponseEntity.ok()

            .header(
                    HttpHeaders.CONTENT_DISPOSITION,
                    "attachment; filename=\"" + file.getName() + "\""
            )

            .contentLength(file.length())

            .contentType(
                    MediaType.APPLICATION_OCTET_STREAM
            )

            .body(resource);

}
}
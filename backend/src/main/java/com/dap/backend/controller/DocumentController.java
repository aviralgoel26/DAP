package com.dap.backend.controller;

import java.io.*;
import java.time.Instant;
import java.util.Set;
import java.nio.file.Paths;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import com.dap.backend.repository.GenerationHistoryDocument;
import com.dap.backend.repository.GenerationHistoryRepository;
import com.dap.backend.repository.TemplateDocument;
import com.dap.backend.repository.TemplateRepository;
import com.dap.backend.service.DocumentEngineService;
import com.dap.backend.service.FileService;
import com.dap.backend.service.PlaceholderDiscoveryService;
import com.dap.backend.service.TemplateFileStorageService;
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

    @Autowired
    private GenerationHistoryRepository historyRepository;

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private TemplateFileStorageService templateFileStorageService;

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
     * After successful generation, records the event in MongoDB history.
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
        DocumentResponse response = engineService.generateDocument(request, logo);

        // Persist generation history to MongoDB (outside generation logic — engine unchanged)
        if (response.getGeneratedFile() != null) {
            saveHistory(response.getGeneratedFile(), request.getTemplateName());
        }

        return response;
    }

    /**
     * Discovers all placeholder keys (e.g. {@code name}, {@code date}) present in a Word template.
     *
     * @param templateName the name of the template folder
     * @return a set of placeholder key names found in the template
     * @throws IOException if the template file cannot be read
     */
    
    @Operation(
            summary = "Discover Placeholders",
            description = "Discovers all placeholder keys present in a Word or Excel template from MongoDB GridFS."
    )
    @GetMapping("/{templateName}/placeholders")
    public Set<String> discoverPlaceholders(
            @PathVariable String templateName
    ) throws IOException {

        logger.info("Discovering placeholders for template: {}", templateName);

        TemplateDocument doc = templateRepository.findById(templateName)
                .orElseThrow(() -> new IllegalArgumentException("Template not found: " + templateName));

        if (doc.getGridFsFileId() == null) {
            throw new IllegalArgumentException("Template binary not found in storage for: " + templateName);
        }

        try (InputStream is = templateFileStorageService.getTemplateInputStream(doc.getGridFsFileId())) {
            if ("DOCX".equalsIgnoreCase(doc.getFileType())) {
                XWPFDocument document = new XWPFDocument(is);
                return placeholderDiscoveryService.discoverPlaceholders(document);
            } else if ("XLSX".equalsIgnoreCase(doc.getFileType())) {
                XSSFWorkbook workbook = new XSSFWorkbook(is);
                return placeholderDiscoveryService.discoverPlaceholders(workbook);
            }
        }

        throw new IllegalArgumentException("Unsupported template type: " + doc.getFileType());
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

    /**
     * Records a successful document generation event in MongoDB history.
     * Called after the generation engine completes — the engine itself is untouched.
     */
    private void saveHistory(String generatedFilename, String templateName) {
        try {
            File genFile = new File(fileService.getGeneratedDirectory(), generatedFilename);
            GenerationHistoryDocument doc = GenerationHistoryDocument.builder()
                    .filename(generatedFilename)
                    .templateName(templateName)
                    .size(genFile.exists() ? genFile.length() : 0L)
                    .generatedAt(Instant.now())
                    .build();
            historyRepository.save(doc);
            logger.debug("History record saved for '{}'", generatedFilename);
        } catch (Exception e) {
            // Log but don't fail the generation response — history is supplementary
            logger.error("Failed to save history record for '{}': {}", generatedFilename, e.getMessage(), e);
        }
    }
}
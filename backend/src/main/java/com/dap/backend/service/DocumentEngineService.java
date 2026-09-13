package com.dap.backend.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dap.backend.model.DocumentRequest;
import com.dap.backend.model.DocumentResponse;
import com.dap.backend.repository.TemplateDocument;
import com.dap.backend.repository.TemplateRepository;

/**
 * Core document generation engine.
 * <p>
 * Retrieves source template binaries from MongoDB GridFS via {@link TemplateFileStorageService},
 * streams them into the temporary generated directory, and executes placeholder replacement
 * using {@link WordDocumentService} or {@link ExcelDocumentService}.
 * </p>
 */
@Service
public class DocumentEngineService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentEngineService.class);

    @Autowired
    private FileService fileService;

    @Autowired
    private WordDocumentService wordDocumentService;

    @Autowired
    private ExcelDocumentService excelDocumentService;

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private TemplateFileStorageService templateFileStorageService;

    /**
     * Reads and returns the plain text content of a Word (.docx) template directly from MongoDB GridFS.
     *
     * @param templateName the name of the template
     * @return the concatenated text of all paragraphs, or an error message string on failure
     */
    public String readDocument(String templateName) {
        StringBuilder content = new StringBuilder();

        try {
            TemplateDocument doc = templateRepository.findById(templateName)
                    .orElseThrow(() -> new IllegalArgumentException("Template not found: " + templateName));

            if (doc.getGridFsFileId() == null) {
                return "Error : Template binary not found in storage: " + templateName;
            }

            try (InputStream is = templateFileStorageService.getTemplateInputStream(doc.getGridFsFileId());
                 XWPFDocument document = new XWPFDocument(is)) {

                for (XWPFParagraph paragraph : document.getParagraphs()) {
                    content.append(paragraph.getText()).append("\n");
                }
            }
        } catch (Exception e) {
            logger.error("Failed to read document '{}': {}", templateName, e.getMessage(), e);
            return "Error : " + e.getMessage();
        }

        return content.toString();
    }

    /**
     * Generates a document by streaming the template from MongoDB GridFS, replacing placeholders,
     * and optionally embedding a logo. Supports both .docx and .xlsx templates.
     *
     * @param request the document request containing the template name and placeholder values
     * @param logo    optional logo image to embed in the document
     * @return a {@link DocumentResponse} with a status message and the generated filename,
     *         or an error message on failure
     */
    public DocumentResponse generateDocument(DocumentRequest request, MultipartFile logo) {

        if (logo != null) {
            logger.info("Logo received – name: {}, size: {} bytes",
                    logo.getOriginalFilename(), logo.getSize());
        }

        try {
            TemplateDocument doc = templateRepository.findById(request.getTemplateName())
                    .orElseThrow(() -> new IllegalArgumentException("Template not found: " + request.getTemplateName()));

            if (doc.getGridFsFileId() == null) {
                throw new IllegalArgumentException("Template binary not found in storage for: " + request.getTemplateName());
            }

            String originalFilename = doc.getOriginalFilename() != null
                    ? doc.getOriginalFilename()
                    : (doc.getId() + "." + doc.getFileType().toLowerCase());

            String generatedFileName = fileService.generateFileName(request.getTemplateName(), originalFilename);
            String outputFile = fileService.buildOutputPath(generatedFileName);

            // Ensure generated storage directory exists
            File genDir = new File(fileService.getGeneratedDirectory());
            if (!genDir.exists()) {
                genDir.mkdirs();
            }

            // Stream template binary from GridFS directly into generated output file
            try (InputStream is = templateFileStorageService.getTemplateInputStream(doc.getGridFsFileId());
                 FileOutputStream fos = new FileOutputStream(outputFile)) {
                is.transferTo(fos);
            }

            // Process document using existing POI services
            if (outputFile.toLowerCase().endsWith(".docx")) {
                processWordDocument(outputFile, request, logo);
            } else if (outputFile.toLowerCase().endsWith(".xlsx")) {
                processExcelDocument(outputFile, request, logo);
            }

            logger.info("Document generated: {}", generatedFileName);
            return new DocumentResponse("Document generated successfully", generatedFileName);

        } catch (Exception e) {
            logger.error("Document generation failed for template '{}': {}",
                    request.getTemplateName(), e.getMessage(), e);
            return new DocumentResponse(e.getMessage(), null);
        }
    }

    /**
     * Processes a Word (.docx) document: opens it, replaces placeholders, and saves.
     */
    private void processWordDocument(String outputFile, DocumentRequest request, MultipartFile logo)
            throws Exception {

        try (FileInputStream fis = new FileInputStream(outputFile);
             XWPFDocument document = new XWPFDocument(fis)) {

            wordDocumentService.replaceInDocument(document, request.getPlaceholders(), logo);

            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                document.write(fos);
            }
        }
    }

    /**
     * Processes an Excel (.xlsx) document: opens it, replaces placeholders, and saves.
     */
    private void processExcelDocument(String outputFile, DocumentRequest request, MultipartFile logo)
            throws Exception {

        try (FileInputStream fis = new FileInputStream(outputFile);
             XSSFWorkbook workbook = new XSSFWorkbook(fis)) {

            excelDocumentService.replacePlaceholders(workbook, request.getPlaceholders(), logo);

            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                workbook.write(fos);
            }
        }
    }
}
package com.dap.backend.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dap.backend.model.DocumentRequest;
import com.dap.backend.model.DocumentResponse;

/**
 * Core document generation engine. Routes generation to the appropriate
 * sub-service ({@link WordDocumentService} or {@link ExcelDocumentService})
 * based on the template file extension.
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

    @Value("${template.storage.path}")
    private String templatePath;

    @Value("${generated.storage.path}")
    private String generatedPath;

    /**
     * Reads and returns the plain text content of a Word (.docx) template.
     *
     * @param templateName the name of the template folder
     * @return the concatenated text of all paragraphs, or an error message string on failure
     */
    public String readDocument(String templateName) {

        String filePath = templatePath + "/" + templateName + "/template.docx";
        StringBuilder content = new StringBuilder();

        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument document = new XWPFDocument(fis)) {

            for (XWPFParagraph paragraph : document.getParagraphs()) {
                content.append(paragraph.getText()).append("\n");
            }

        } catch (IOException e) {
            logger.error("Failed to read document '{}': {}", templateName, e.getMessage(), e);
            return "Error : " + e.getMessage();
        }

        return content.toString();
    }

    /**
     * Generates a document by copying the template, replacing placeholders,
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
            String templateFile = fileService.findTemplateFile(request.getTemplateName());
            String generatedFileName = fileService.generateFileName(request.getTemplateName(), templateFile);
            String outputFile = fileService.buildOutputPath(generatedFileName);

            fileService.copyTemplate(templateFile, outputFile);

            if (outputFile.endsWith(".docx")) {
                processWordDocument(outputFile, request, logo);
            } else if (outputFile.endsWith(".xlsx")) {
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
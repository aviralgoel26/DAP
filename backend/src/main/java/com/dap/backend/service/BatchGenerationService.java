package com.dap.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dap.backend.model.BatchRequest;
import com.dap.backend.model.BatchResponse;
import com.dap.backend.model.DocumentRequest;
import com.dap.backend.model.DocumentResponse;

/**
 * Service responsible for orchestrating batch document generation across multiple templates.
 * Each template in the batch is processed independently using the {@link DocumentEngineService},
 * and all generated files are packaged into a single ZIP archive via the {@link ZipService}.
 */
@Service
public class BatchGenerationService {

    private static final Logger logger = LoggerFactory.getLogger(BatchGenerationService.class);

    @Autowired
    private DocumentEngineService documentEngineService;

    @Autowired
    private ZipService zipService;

    /**
     * Generates documents for all templates in the batch request and packages them into a ZIP file.
     *
     * @param request the batch request containing template names and shared placeholder values
     * @param logo    optional logo image to embed in each generated document
     * @return a {@link BatchResponse} with a status message and the ZIP filename, or an error message on failure
     */
    public BatchResponse generateBatch(BatchRequest request, MultipartFile logo) {

        try {
            List<String> generatedFiles = new ArrayList<>();

            for (String template : request.getTemplates()) {

                DocumentRequest documentRequest = new DocumentRequest();
                documentRequest.setTemplateName(template);
                documentRequest.setPlaceholders(request.getPlaceholders());

                DocumentResponse response = documentEngineService.generateDocument(documentRequest, logo);
                generatedFiles.add(response.getGeneratedFile());
            }

            String zipFile = zipService.createZip(generatedFiles);

            logger.info("Batch generation complete. ZIP: {}", zipFile);
            return new BatchResponse("Batch generated successfully", zipFile);

        } catch (Exception e) {
            logger.error("Batch generation failed: {}", e.getMessage(), e);
            return new BatchResponse(e.getMessage(), null);
        }
    }
}

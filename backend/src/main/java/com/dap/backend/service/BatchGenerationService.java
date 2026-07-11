package com.dap.backend.service;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dap.backend.model.BatchRequest;
import com.dap.backend.model.BatchResponse;
import com.dap.backend.model.DocumentRequest;
import com.dap.backend.model.DocumentResponse;
import com.dap.backend.service.DocumentEngineService;
import com.dap.backend.service.ZipService;


@Service
public class BatchGenerationService {
    @Autowired
private DocumentEngineService documentEngineService;
@Autowired
private ZipService zipService;

public BatchResponse generateBatch(
        BatchRequest request,
        MultipartFile logo) {

    try {

        List<String> generatedFiles = new ArrayList<>();

        for (String template : request.getTemplates()) {

            DocumentRequest documentRequest = new DocumentRequest();

            documentRequest.setTemplateName(template);

            documentRequest.setPlaceholders(
                    request.getPlaceholders()
            );

            DocumentResponse response =
                    documentEngineService.generateDocument(
                            documentRequest,
                            logo
                    );

            generatedFiles.add(
                    response.getGeneratedFile()
            );
        }

        String zipFile =
                zipService.createZip(generatedFiles);

        return new BatchResponse(
                "Batch generated successfully",
                zipFile
        );

    } catch (Exception e) {

        return new BatchResponse(
                e.getMessage(),
                null
        );

    }
}
}   

package com.dap.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.dap.backend.model.BatchRequest;
import com.dap.backend.model.BatchResponse;
import com.dap.backend.service.BatchGenerationService;

@RestController
@RequestMapping("/api/batch")
public class BatchController {
    @Autowired
    private BatchGenerationService batchGenerationService;
    @PostMapping("/generate")
public BatchResponse generateBatch(
        @RequestPart("request")
        BatchRequest request,

        @RequestPart(value = "logo",
                required = false)
        MultipartFile logo) {

    return batchGenerationService
            .generateBatch(
                    request,
                    logo
            );

}
}

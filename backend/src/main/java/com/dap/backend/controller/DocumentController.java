package com.dap.backend.controller;

import com.dap.backend.model.DocumentRequest;
import com.dap.backend.model.DocumentResponse;
import com.dap.backend.service.DocumentEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentEngineService engineService;

    @GetMapping("/read/{templateName}")
    public String readDocument(@PathVariable String templateName) {

        return engineService.readDocument(templateName);

    }
    @PostMapping("/generate")
public DocumentResponse generateDocument(
        @RequestBody DocumentRequest request) {

    return engineService.generateDocument(request);

}

}
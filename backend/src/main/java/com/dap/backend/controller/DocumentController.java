package com.dap.backend.controller;

import com.dap.backend.model.DocumentRequest;
import com.dap.backend.model.DocumentResponse;
import com.dap.backend.service.DocumentEngineService;
import com.dap.backend.service.FileService;
import com.dap.backend.service.PlaceholderDiscoveryService;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    @Autowired
    private DocumentEngineService engineService;
    @Autowired
private PlaceholderDiscoveryService placeholderDiscoveryService;

@Autowired
private FileService fileService;

    @GetMapping("/read/{templateName}")
    public String readDocument(@PathVariable String templateName) {

        return engineService.readDocument(templateName);

    }
    @PostMapping("/generate")
public DocumentResponse generateDocument(
        @RequestBody DocumentRequest request) {

    return engineService.generateDocument(request);

}
@GetMapping("/{templateName}/placeholders")
public Set<String> discoverPlaceholders(
        @PathVariable String templateName)
        throws IOException {

    String path =
            fileService.findTemplateFile(templateName);

    try(FileInputStream fis = new FileInputStream(path);
        XWPFDocument document = new XWPFDocument(fis)) {

        return placeholderDiscoveryService
                .discoverPlaceholders(document);

    }

}
}
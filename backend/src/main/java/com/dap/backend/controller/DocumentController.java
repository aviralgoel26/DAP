package com.dap.backend.controller;

import com.dap.backend.model.DocumentRequest;
import com.dap.backend.model.DocumentResponse;
import com.dap.backend.service.DocumentEngineService;
import com.dap.backend.service.FileService;
import com.dap.backend.service.PlaceholderDiscoveryService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.RequestPart;



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
    @PostMapping(
        value = "/generate",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
)
public DocumentResponse generateDocument(

        @RequestPart("data") String json,

        @RequestPart(value = "logo",
                required = false)
        MultipartFile logo

) throws Exception {

    ObjectMapper mapper = new ObjectMapper();

    DocumentRequest request =
            mapper.readValue(
                    json,
                    DocumentRequest.class
            );

    return engineService.generateDocument(
            request,
            logo
    );

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
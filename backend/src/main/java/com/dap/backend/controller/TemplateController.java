package com.dap.backend.controller;

import com.dap.backend.service.TemplateService;
import com.dap.backend.service.TemplateUploadService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.dap.backend.model.TemplateInfo;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    @Autowired
    private TemplateService templateService;
    @Autowired
private TemplateUploadService templateUploadService;

    @GetMapping
    public List<TemplateInfo> getTemplates() {

        return templateService.getAllTemplates();

        
    }
    @PostMapping("/upload")
public String uploadTemplate(

        @RequestParam String templateId,

        @RequestParam MultipartFile file)

        throws IOException {

    return templateUploadService
            .uploadTemplate(templateId, file);

}

}
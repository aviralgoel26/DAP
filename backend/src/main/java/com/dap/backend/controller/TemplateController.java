package com.dap.backend.controller;

import com.dap.backend.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.dap.backend.model.TemplateInfo;
import java.util.List;

@RestController
@RequestMapping("/api/templates")
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    @GetMapping
    public List<TemplateInfo> getTemplates() {

        return templateService.getAllTemplates();

    }

}
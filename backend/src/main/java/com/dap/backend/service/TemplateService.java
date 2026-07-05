package com.dap.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import com.dap.backend.model.TemplateInfo;

@Service
public class TemplateService {

    @Value("${template.storage.path}")
    private String templatePath;

public List<TemplateInfo> getAllTemplates() {

    List<TemplateInfo> templates = new ArrayList<>();

    File folder = new File(templatePath);

    File[] files = folder.listFiles();

    if (files == null) {
        return templates;
    }

    for (File file : files) {

        if (file.isDirectory()) {

            TemplateInfo template =
                    new TemplateInfo(file.getName(), "DOCX");

            templates.add(template);

        }

    }

    return templates;
}

}
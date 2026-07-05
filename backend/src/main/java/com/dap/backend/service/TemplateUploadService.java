package com.dap.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Service
public class TemplateUploadService {

    @Value("${template.storage.path}")
    private String templatePath;

    public String uploadTemplate(
            String templateId,
            MultipartFile file)
            throws IOException {

        Path folder =
                Paths.get(templatePath, templateId);

        Files.createDirectories(folder);

        Path destination =
                folder.resolve(file.getOriginalFilename());

        Files.copy(
                file.getInputStream(),
                destination,
                StandardCopyOption.REPLACE_EXISTING
        );

        return destination.toString();

    }

}
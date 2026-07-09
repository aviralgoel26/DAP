package com.dap.backend.service;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.dap.backend.model.DocumentRequest;
import com.dap.backend.model.DocumentResponse;
import java.nio.file.Files;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import com.dap.backend.service.WordDocumentService;


@Service
public class DocumentEngineService {
    @Autowired
private FileService fileService;
@Autowired
private PlaceholderService placeholderService;
@Autowired
private WordDocumentService wordDocumentService;

    @Value("${template.storage.path}")
    private String templatePath;

    @Value("${generated.storage.path}")
private String generatedPath;

    public String readDocument(String templateName) {

        String filePath = templatePath + "/" + templateName + "/template.docx";

        StringBuilder content = new StringBuilder();

        try (FileInputStream fis = new FileInputStream(filePath);
             XWPFDocument document = new XWPFDocument(fis)) {

            for (XWPFParagraph paragraph : document.getParagraphs()) {

                content.append(paragraph.getText())
                        .append("\n");

            }

        } catch (IOException e) {

            return "Error : " + e.getMessage();

        }

        return content.toString();


    }

    public DocumentResponse generateDocument(DocumentRequest request, MultipartFile logo) {

        if (logo != null) {

    System.out.println("Logo Name : "
            + logo.getOriginalFilename());

    System.out.println("Logo Size : "
            + logo.getSize());

}
    try {

        String templateFile =
        fileService.findTemplateFile(
                request.getTemplateName()
        );

       String generatedFileName =
        fileService.generateFileName(
                request.getTemplateName()
        );

        String outputFile =
        fileService.buildOutputPath(
                generatedFileName
        );

        fileService.copyTemplate(
        templateFile,
        outputFile
);
        FileInputStream fis = new FileInputStream(outputFile);

        XWPFDocument document = new XWPFDocument(fis);

       Map<String,String> placeholders = new HashMap<>();

for (Map.Entry<String,String> entry :
        request.getPlaceholders().entrySet()) {

    placeholders.put(
            "{{" + entry.getKey() + "}}",
            entry.getValue()
    );

}

wordDocumentService.replaceInDocument(
        document,
        request.getPlaceholders(),logo
);

FileOutputStream fos = new FileOutputStream(outputFile);

document.write(fos);

fos.close();
document.close();
fis.close();
        return new DocumentResponse(
                "Document generated successfully",
                generatedFileName
        );
        


    } catch (Exception e) {

        return new DocumentResponse(
                e.getMessage(),
                null
        );

    }
    

}

}
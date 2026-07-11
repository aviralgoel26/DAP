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
import com.dap.backend.service.WordDocumentService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;



@Service
public class DocumentEngineService {
    @Autowired
private FileService fileService;
@Autowired
private WordDocumentService wordDocumentService;
@Autowired
private ExcelDocumentService excelDocumentService;

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
                request.getTemplateName(),
                templateFile
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

if (outputFile.endsWith(".docx")) {

    XWPFDocument document = new XWPFDocument(fis);

    wordDocumentService.replaceInDocument(
            document,
            request.getPlaceholders(),
            logo
    );

    FileOutputStream fos = new FileOutputStream(outputFile);

    document.write(fos);

    fos.close();
    document.close();

}
else if (outputFile.endsWith(".xlsx")) {

    XSSFWorkbook workbook = new XSSFWorkbook(fis);

    excelDocumentService.replacePlaceholders(
        workbook,
        request.getPlaceholders()
);

    FileOutputStream fos = new FileOutputStream(outputFile);

    workbook.write(fos);

    fos.close();
    workbook.close();

}

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
package com.dap.backend.service;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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


@Service
public class DocumentEngineService {

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
private String generateFileName(String templateName) {

    String timestamp = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

    return templateName + "_" + timestamp + ".docx";

}
    public DocumentResponse generateDocument(DocumentRequest request) {

    try {

        String templateFile =
                templatePath + "/" +
                request.getTemplateName() +
                "/template.docx";

        String generatedFileName =
                generateFileName(request.getTemplateName());

        String outputFile =
                generatedPath + "/" + generatedFileName;

        Files.copy(
                Paths.get(templateFile),
                Paths.get(outputFile),
                StandardCopyOption.REPLACE_EXISTING
        );
        FileInputStream fis = new FileInputStream(outputFile);

        XWPFDocument document = new XWPFDocument(fis);

        replacePlaceholder(
        document,
        "{{companyName}}",
        request.getCompanyName()
);

replacePlaceholder(
        document,
        "{{address}}",
        request.getAddress()
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

private void replacePlaceholder(XWPFDocument document,
                                String placeholder,
                                String replacement) {

    for (XWPFParagraph paragraph : document.getParagraphs()) {

        String text = paragraph.getText();

        if (text.contains(placeholder)) {

            text = text.replace(placeholder, replacement);

            int runCount = paragraph.getRuns().size();

            for (int i = runCount - 1; i >= 0; i--) {
                paragraph.removeRun(i);
            }

            paragraph.createRun().setText(text);

        }

    }

}

}
package com.dap.backend.service;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class WordDocumentService {

    @Autowired
    private PlaceholderEngine placeholderEngine;

  

    public void replaceInDocument(
        XWPFDocument document,
        Map<String,String> placeholders, MultipartFile logo) throws Exception {

    replaceParagraphs(document, placeholders, logo);

    replaceTables(document, placeholders,logo);

    replaceHeaders(document, placeholders, logo);

    replaceFooters(document, placeholders, logo);

}
private void replaceParagraphs(
        XWPFDocument document,
        Map<String,String> placeholders, MultipartFile logo) throws Exception {

    for (XWPFParagraph paragraph : document.getParagraphs()) {

        replaceParagraph(paragraph, placeholders, logo);

    }

}
private void replaceTables(
        XWPFDocument document,
        Map<String,String> placeholders, MultipartFile logo) throws Exception {

    for (XWPFTable table : document.getTables()) {

        replaceTable(table, placeholders,logo);

    }

}
private void replaceTable(
        XWPFTable table,
        Map<String,String> placeholders, MultipartFile logo) throws Exception {

    for (XWPFTableRow row : table.getRows()) {

        for (XWPFTableCell cell : row.getTableCells()) {

            for (XWPFParagraph paragraph :
                    cell.getParagraphs()) {

                replaceParagraph(
                        paragraph,
                        placeholders, logo
                );

            }

        }

    }

}
private void replaceHeaders(
        XWPFDocument document,
        Map<String,String> placeholders, MultipartFile logo) throws Exception {

    for (XWPFHeader header :
            document.getHeaderList()) {

        for (XWPFParagraph paragraph :
                header.getParagraphs()) {

            replaceParagraph(
                    paragraph,
                    placeholders,logo
            );

        }

    }

}
private void replaceFooters(
        XWPFDocument document,
        Map<String,String> placeholders, MultipartFile logo) throws Exception {

    for (XWPFFooter footer :
            document.getFooterList()) {

        for (XWPFParagraph paragraph :
                footer.getParagraphs()) {

            replaceParagraph(
                    paragraph,
                    placeholders,logo
            );

        }

    }

}

    private void replaceParagraph(
        XWPFParagraph paragraph,
        Map<String, String> placeholders, MultipartFile logo) throws Exception {
            

    if (paragraph.getRuns().isEmpty()) {
        return;
    }
    replaceLogo(paragraph, logo);

    if (paragraph.getText().isEmpty() && paragraph.getRuns().size()> 0) {
        return;
    }


    // Step 1: Join all runs
    StringBuilder builder = new StringBuilder();

    for (XWPFRun run : paragraph.getRuns()) {

        String text = run.getText(0);

        if (text != null) {
            builder.append(text);
        }

    }

    String original = builder.toString();

    // Step 2: Replace placeholders
    String updated =
            placeholderEngine.replacePlaceholders(
                    original,
                    placeholders
            );

    // Step 3: Nothing changed
    if (original.equals(updated)) {
        return;
    }

    // Step 4: Keep formatting from first run
    XWPFRun firstRun = paragraph.getRuns().get(0);

    firstRun.setText("", 0);
    firstRun.setText(updated, 0);

    // Step 5: Clear remaining runs
    for (int i = paragraph.getRuns().size() - 1; i > 0; i--) {

        paragraph.removeRun(i);

    }
    

}

private void replaceLogo(
        XWPFParagraph paragraph,
        MultipartFile logo)
        throws Exception {

if (logo == null) {
    System.out.println("Logo is NULL");
    return;
}

if (!paragraph.getText().contains("{{logo}}")) {
    return;
}




    // Remove all runs
    XWPFRun firstRun = paragraph.getRuns().get(0);

for (int i = paragraph.getRuns().size() - 1; i > 0; i--) {
    paragraph.removeRun(i);
}

firstRun.setText("", 0);

firstRun.addPicture(
        logo.getInputStream(),
        getPictureType(logo),
        logo.getOriginalFilename(),
        Units.toEMU(120),
        Units.toEMU(120)
);
return;

}
private int getPictureType(
        MultipartFile file) {

    String name =
            file.getOriginalFilename()
                    .toLowerCase();

    if (name.endsWith(".png")) {

        return Document.PICTURE_TYPE_PNG;

    }

    if (name.endsWith(".jpg")
            || name.endsWith(".jpeg")) {

        return Document.PICTURE_TYPE_JPEG;

    }

    throw new RuntimeException(
            "Unsupported image format."
    );

}

}
package com.dap.backend.service;

import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Map;

@Service
public class WordDocumentService {

    @Autowired
    private PlaceholderEngine placeholderEngine;

  

    public void replaceInDocument(
        XWPFDocument document,
        Map<String,String> placeholders) {

    replaceParagraphs(document, placeholders);

    replaceTables(document, placeholders);

    replaceHeaders(document, placeholders);

    replaceFooters(document, placeholders);

}
private void replaceParagraphs(
        XWPFDocument document,
        Map<String,String> placeholders) {

    for (XWPFParagraph paragraph : document.getParagraphs()) {

        replaceParagraph(paragraph, placeholders);

    }

}
private void replaceTables(
        XWPFDocument document,
        Map<String,String> placeholders) {

    for (XWPFTable table : document.getTables()) {

        replaceTable(table, placeholders);

    }

}
private void replaceTable(
        XWPFTable table,
        Map<String,String> placeholders) {

    for (XWPFTableRow row : table.getRows()) {

        for (XWPFTableCell cell : row.getTableCells()) {

            for (XWPFParagraph paragraph :
                    cell.getParagraphs()) {

                replaceParagraph(
                        paragraph,
                        placeholders
                );

            }

        }

    }

}
private void replaceHeaders(
        XWPFDocument document,
        Map<String,String> placeholders) {

    for (XWPFHeader header :
            document.getHeaderList()) {

        for (XWPFParagraph paragraph :
                header.getParagraphs()) {

            replaceParagraph(
                    paragraph,
                    placeholders
            );

        }

    }

}
private void replaceFooters(
        XWPFDocument document,
        Map<String,String> placeholders) {

    for (XWPFFooter footer :
            document.getFooterList()) {

        for (XWPFParagraph paragraph :
                footer.getParagraphs()) {

            replaceParagraph(
                    paragraph,
                    placeholders
            );

        }

    }

}

    private void replaceParagraph(
        XWPFParagraph paragraph,
        Map<String, String> placeholders) {

    if (paragraph.getRuns().isEmpty()) {
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

}
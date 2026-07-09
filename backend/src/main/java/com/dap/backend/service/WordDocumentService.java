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

    System.out.println("----- PARAGRAPH -----");

    for (XWPFRun run : paragraph.getRuns()) {

        String text = run.getText(0);

        System.out.println("RUN = [" + text + "]");

        if (text == null) {
            continue;
        }

        String updated =
                placeholderEngine.replacePlaceholders(
                        text,
                        placeholders
                );

        if (!text.equals(updated)) {
            run.setText(updated, 0);
        }
        
    }

    
}

}
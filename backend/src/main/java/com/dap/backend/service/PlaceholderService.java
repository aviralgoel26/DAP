package com.dap.backend.service;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PlaceholderService {

    public void replaceAllPlaceholders(
            XWPFDocument document,
            Map<String, String> placeholders) {

        for (XWPFParagraph paragraph : document.getParagraphs()) {

            replaceInParagraph(paragraph, placeholders);

        }

    }

    private void replaceInParagraph(
        XWPFParagraph paragraph,
        Map<String, String> placeholders) {

    if (paragraph.getRuns() == null) {
        return;
    }

    paragraph.getRuns().forEach(run -> {

        String text = run.getText(0);

        if (text == null) {
            return;
        }

        boolean modified = false;

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {

            if (text.contains(entry.getKey())) {

                text = text.replace(
                        entry.getKey(),
                        entry.getValue()
                );

                modified = true;
            }
        }

        if (modified) {

            run.setText(text, 0);

        }

    });

}

}
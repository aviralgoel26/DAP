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

        String text = paragraph.getText();

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

        if (!modified) {
            return;
        }

        int runCount = paragraph.getRuns().size();

        for (int i = runCount - 1; i >= 0; i--) {

            paragraph.removeRun(i);

        }

        paragraph.createRun().setText(text);

    }

}
package com.dap.backend.service;

import org.apache.poi.xwpf.usermodel.*;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PlaceholderDiscoveryService {

    private static final Pattern PLACEHOLDER_PATTERN =
            Pattern.compile("\\{\\{(.*?)\\}\\}");

    public Set<String> discoverPlaceholders(XWPFDocument document) {

        Set<String> placeholders = new HashSet<>();

        for (XWPFParagraph paragraph : document.getParagraphs()) {

            Matcher matcher =
                    PLACEHOLDER_PATTERN.matcher(paragraph.getText());

            while (matcher.find()) {

                placeholders.add(matcher.group(1));

            }

        }
        for (XWPFTable table : document.getTables()) {

            scanTable(table, placeholders);

        }

        return placeholders;
    }

    // ⭐⭐⭐ Add the scanTable() method HERE
    private void scanTable(
            XWPFTable table,
            Set<String> placeholders) {

        for (XWPFTableRow row : table.getRows()) {

            for (XWPFTableCell cell : row.getTableCells()) {

                for (XWPFParagraph paragraph : cell.getParagraphs()) {

                    Matcher matcher =
                            PLACEHOLDER_PATTERN.matcher(paragraph.getText());

                    while (matcher.find()) {

                        placeholders.add(matcher.group(1));

                    }

                }

            }

        }   
       
    }
}
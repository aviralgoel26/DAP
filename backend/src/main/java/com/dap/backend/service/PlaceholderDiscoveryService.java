package com.dap.backend.service;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service for discovering placeholder keys (e.g. {@code {{name}}}) within
 * Word (.docx) and Excel (.xlsx) documents.
 */
@Service
public class PlaceholderDiscoveryService {

    private static final Logger logger = LoggerFactory.getLogger(PlaceholderDiscoveryService.class);

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{(.*?)\\}\\}");

    /**
     * Discovers all placeholder keys present in a Word (.docx) document,
     * including body paragraphs and table cells.
     *
     * @param document the parsed Word document
     * @return a set of placeholder key names (without the surrounding {@code {{ }}})
     */
    public Set<String> discoverPlaceholders(XWPFDocument document) {

        Set<String> placeholders = new HashSet<>();

        for (XWPFParagraph paragraph : document.getParagraphs()) {
            extractPlaceholders(paragraph.getText(), placeholders);
        }

        for (XWPFTable table : document.getTables()) {
            scanTable(table, placeholders);
        }

        logger.debug("Discovered {} placeholder(s) in Word document", placeholders.size());
        return placeholders;
    }

    /**
     * Discovers all placeholder keys present in an Excel (.xlsx) workbook,
     * scanning all sheets and string-type cells.
     *
     * @param workbook the parsed Excel workbook
     * @return a set of placeholder key names (without the surrounding {@code {{ }}})
     */
    public Set<String> discoverPlaceholders(XSSFWorkbook workbook) {

        Set<String> placeholders = new HashSet<>();

        for (Sheet sheet : workbook) {
            scanSheet(sheet, placeholders);
        }

        logger.debug("Discovered {} placeholder(s) in Excel workbook", placeholders.size());
        return placeholders;
    }

    /**
     * Scans a Word table recursively for placeholders in all cell paragraphs.
     */
    private void scanTable(XWPFTable table, Set<String> placeholders) {

        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                for (XWPFParagraph paragraph : cell.getParagraphs()) {
                    extractPlaceholders(paragraph.getText(), placeholders);
                }
            }
        }
    }

    /**
     * Scans an Excel sheet for placeholders in all string-type cells.
     */
    private void scanSheet(Sheet sheet, Set<String> placeholders) {

        for (Row row : sheet) {
            for (Cell cell : row) {

                if (cell.getCellType() != CellType.STRING) {
                    continue;
                }

                extractPlaceholders(cell.getStringCellValue(), placeholders);
            }
        }
    }

    /**
     * Extracts all placeholder key names from a text string and adds them to the result set.
     */
    private void extractPlaceholders(String text, Set<String> placeholders) {

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);

        while (matcher.find()) {
            placeholders.add(matcher.group(1));
        }
    }
}
package com.dap.backend.service;

import java.util.Map;

import org.apache.poi.util.Units;
import org.apache.poi.xwpf.usermodel.Document;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFFooter;
import org.apache.poi.xwpf.usermodel.XWPFHeader;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service for processing Word (.docx) documents: replacing placeholder text and
 * optionally embedding a logo image in paragraphs, tables, headers, and footers.
 */
@Service
public class WordDocumentService {

    private static final Logger logger = LoggerFactory.getLogger(WordDocumentService.class);

    @Autowired
    private PlaceholderEngine placeholderEngine;

    /**
     * Replaces all placeholder tokens and logo references throughout the entire document,
     * including body paragraphs, tables, headers, and footers.
     *
     * @param document     the Word document to process (modified in place)
     * @param placeholders a map of placeholder keys to their replacement values
     * @param logo         optional logo image to embed at {@code {{logo}}} positions
     * @throws Exception if image data cannot be read or an unsupported format is provided
     */
    public void replaceInDocument(
            XWPFDocument document,
            Map<String, String> placeholders,
            MultipartFile logo) throws Exception {

        replaceParagraphs(document, placeholders, logo);
        replaceTables(document, placeholders, logo);
        replaceHeaders(document, placeholders, logo);
        replaceFooters(document, placeholders, logo);
    }

    /**
     * Replaces placeholders in all body paragraphs of the document.
     */
    private void replaceParagraphs(
            XWPFDocument document,
            Map<String, String> placeholders,
            MultipartFile logo) throws Exception {

        for (XWPFParagraph paragraph : document.getParagraphs()) {
            replaceParagraph(paragraph, placeholders, logo);
        }
    }

    /**
     * Replaces placeholders in all tables of the document.
     */
    private void replaceTables(
            XWPFDocument document,
            Map<String, String> placeholders,
            MultipartFile logo) throws Exception {

        for (XWPFTable table : document.getTables()) {
            replaceTable(table, placeholders, logo);
        }
    }

    /**
     * Replaces placeholders in all cell paragraphs of a single table.
     */
    private void replaceTable(
            XWPFTable table,
            Map<String, String> placeholders,
            MultipartFile logo) throws Exception {

        for (XWPFTableRow row : table.getRows()) {
            for (XWPFTableCell cell : row.getTableCells()) {
                for (XWPFParagraph paragraph : cell.getParagraphs()) {
                    replaceParagraph(paragraph, placeholders, logo);
                }
            }
        }
    }

    /**
     * Replaces placeholders in all headers of the document.
     */
    private void replaceHeaders(
            XWPFDocument document,
            Map<String, String> placeholders,
            MultipartFile logo) throws Exception {

        for (XWPFHeader header : document.getHeaderList()) {
            for (XWPFParagraph paragraph : header.getParagraphs()) {
                replaceParagraph(paragraph, placeholders, logo);
            }
        }
    }

    /**
     * Replaces placeholders in all footers of the document.
     */
    private void replaceFooters(
            XWPFDocument document,
            Map<String, String> placeholders,
            MultipartFile logo) throws Exception {

        for (XWPFFooter footer : document.getFooterList()) {
            for (XWPFParagraph paragraph : footer.getParagraphs()) {
                replaceParagraph(paragraph, placeholders, logo);
            }
        }
    }

    /**
     * Processes a single paragraph: attempts logo replacement first, then text placeholder replacement.
     * All runs are joined into a single string, placeholders are substituted, and the result
     * is written back to the first run while clearing the remaining runs to preserve formatting.
     */
    private void replaceParagraph(
            XWPFParagraph paragraph,
            Map<String, String> placeholders,
            MultipartFile logo) throws Exception {

        if (paragraph.getRuns().isEmpty()) {
            return;
        }

        replaceLogo(paragraph, logo);

        if (paragraph.getText().isEmpty() && paragraph.getRuns().size() > 0) {
            return;
        }

        // Step 1: Join all runs into a single string
        StringBuilder builder = new StringBuilder();
        for (XWPFRun run : paragraph.getRuns()) {
            String text = run.getText(0);
            if (text != null) {
                builder.append(text);
            }
        }

        String original = builder.toString();

        // Step 2: Replace placeholders
        String updated = placeholderEngine.replacePlaceholders(original, placeholders);

        // Step 3: Skip if nothing changed
        if (original.equals(updated)) {
            return;
        }

        // Step 4: Write to first run to preserve its formatting
        XWPFRun firstRun = paragraph.getRuns().get(0);
        firstRun.setText("", 0);
        firstRun.setText(updated, 0);

        // Step 5: Remove all subsequent runs
        for (int i = paragraph.getRuns().size() - 1; i > 0; i--) {
            paragraph.removeRun(i);
        }
    }

    /**
     * Replaces a {@code {{logo}}} token in a paragraph with an embedded image.
     * Does nothing if no logo is provided or if the paragraph does not contain the logo token.
     */
    private void replaceLogo(XWPFParagraph paragraph, MultipartFile logo) throws Exception {

        if (logo == null) {
            logger.debug("No logo provided; skipping logo replacement.");
            return;
        }

        if (!paragraph.getText().contains("{{logo}}")) {
            return;
        }

        // Clear all runs except the first
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
    }

    /**
     * Resolves the Apache POI picture type constant from the logo file extension.
     *
     * @param file the uploaded logo file
     * @return the POI {@link Document} picture type constant
     * @throws IllegalArgumentException if the filename is missing or the format is unsupported
     */
    private int getPictureType(MultipartFile file) {

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("Logo file has no filename.");
        }

        String name = originalFilename.toLowerCase();

        if (name.endsWith(".png")) {
            return Document.PICTURE_TYPE_PNG;
        }

        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
            return Document.PICTURE_TYPE_JPEG;
        }

        throw new IllegalArgumentException("Unsupported image format: " + originalFilename);
    }
}
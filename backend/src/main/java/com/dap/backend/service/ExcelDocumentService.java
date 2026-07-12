package com.dap.backend.service;

import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Service for processing Excel (.xlsx) documents: replacing text placeholders
 * and optionally embedding a logo image.
 */
@Service
public class ExcelDocumentService {

    private static final Logger logger = LoggerFactory.getLogger(ExcelDocumentService.class);

    @Autowired
    private PlaceholderDiscoveryService placeholderDiscoveryService;

    @Autowired
    private PlaceholderEngine placeholderEngine;

    /**
     * Diagnostic utility: logs the sheet names and discovered placeholders of a workbook.
     * Intended for development use only; not exposed via any API endpoint.
     *
     * @param workbook the workbook to inspect
     */
    public void readWorkbook(XSSFWorkbook workbook) {

        logger.debug("===== WORKBOOK =====");

        for (Sheet sheet : workbook) {
            logger.debug("Sheet: {}", sheet.getSheetName());
        }

        Set<String> placeholders = placeholderDiscoveryService.discoverPlaceholders(workbook);

        logger.debug("===== PLACEHOLDERS =====");
        placeholders.forEach(p -> logger.debug("{}", p));
    }

    /**
     * Iterates over all sheets in the workbook and replaces placeholder text and logo in each cell.
     *
     * @param workbook     the Excel workbook to process
     * @param placeholders a map of placeholder keys to replacement values
     * @param logo         optional logo image to embed at the {@code {{logo}}} cell position
     * @throws Exception if image data cannot be read or an unsupported image format is supplied
     */
    public void replacePlaceholders(
            XSSFWorkbook workbook,
            Map<String, String> placeholders,
            MultipartFile logo) throws Exception {

        for (Sheet sheet : workbook) {
            replaceSheet(sheet, placeholders, logo);
        }
    }

    /**
     * Processes a single sheet, replacing placeholders and logo in each string cell.
     */
    private void replaceSheet(
            Sheet sheet,
            Map<String, String> placeholders,
            MultipartFile logo) throws Exception {

        for (Row row : sheet) {

            for (Cell cell : row) {

                if (cell.getCellType() != CellType.STRING) {
                    continue;
                }

                replaceLogo(sheet, cell, logo);

                if (cell.getStringCellValue().isEmpty()) {
                    continue;
                }

                String original = cell.getStringCellValue();
                String updated = placeholderEngine.replacePlaceholders(original, placeholders);

                if (!original.equals(updated)) {
                    cell.setCellValue(updated);
                }
            }
        }
    }

    /**
     * Replaces a {@code {{logo}}} cell with an embedded picture if a logo is provided.
     */
    private void replaceLogo(Sheet sheet, Cell cell, MultipartFile logo) throws Exception {

        if (logo == null) {
            return;
        }

        if (!"{{logo}}".equals(cell.getStringCellValue())) {
            return;
        }

        cell.setCellValue("");

        Drawing<?> drawing = sheet.createDrawingPatriarch();
        CreationHelper helper = sheet.getWorkbook().getCreationHelper();
        ClientAnchor anchor = helper.createClientAnchor();

        anchor.setCol1(cell.getColumnIndex());
        anchor.setRow1(cell.getRowIndex());

        int pictureIndex = sheet.getWorkbook().addPicture(logo.getBytes(), getPictureType(logo));
        Picture picture = drawing.createPicture(anchor, pictureIndex);
        picture.resize();
    }

    /**
     * Resolves the Apache POI picture type constant from the logo file extension.
     *
     * @param file the uploaded logo file
     * @return the POI {@link Workbook} picture type constant
     * @throws IllegalArgumentException if the filename is missing or the format is unsupported
     */
    private int getPictureType(MultipartFile file) {

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IllegalArgumentException("Logo file has no filename.");
        }

        String name = originalFilename.toLowerCase();

        if (name.endsWith(".png")) {
            return Workbook.PICTURE_TYPE_PNG;
        }

        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
            return Workbook.PICTURE_TYPE_JPEG;
        }

        throw new IllegalArgumentException("Unsupported image format: " + originalFilename);
    }
}
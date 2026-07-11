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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class ExcelDocumentService {
    @Autowired
private PlaceholderDiscoveryService placeholderDiscoveryService;
@Autowired
private PlaceholderEngine placeholderEngine;

    public void readWorkbook(
        XSSFWorkbook workbook) {

    System.out.println("===== WORKBOOK =====");

    for (Sheet sheet : workbook) {

        System.out.println(
                "Sheet : " +
                sheet.getSheetName()
        );

    }

    Set<String> placeholders =
            placeholderDiscoveryService
                    .discoverPlaceholders(workbook);

    System.out.println();

    System.out.println("===== PLACEHOLDERS =====");

    placeholders.forEach(System.out::println);

}
public void replacePlaceholders(
        XSSFWorkbook workbook,
        Map<String,String> placeholders,
        MultipartFile logo) throws Exception {
            for (Sheet sheet : workbook) {

        replaceSheet(sheet, placeholders,logo);

    }
        }
private void replaceSheet(
        Sheet sheet,
        Map<String,String> placeholders, MultipartFile logo) throws Exception {

    for (Row row : sheet) {

        for (Cell cell : row) {

            if (cell.getCellType()
                    != CellType.STRING) {

                continue;

            }
            replaceLogo(sheet, cell,logo);

if (cell.getStringCellValue().isEmpty()) {
    continue;
}

            String original = cell.getStringCellValue();
            String updated =
                    placeholderEngine
                            .replacePlaceholders(
                                    original,
                                    placeholders
                            );

            if (!original.equals(updated)) {

                cell.setCellValue(updated);

            }

        }

    }

}
private void replaceLogo(
        Sheet sheet,
        Cell cell,
        MultipartFile logo) throws Exception {

    if (logo == null) {
        return;
    }

    if (!"{{logo}}".equals(cell.getStringCellValue())) {
        return;
    }

    cell.setCellValue("");

    Drawing<?> drawing =
            sheet.createDrawingPatriarch();

    CreationHelper helper =
            sheet.getWorkbook()
                    .getCreationHelper();

    ClientAnchor anchor =
            helper.createClientAnchor();

    anchor.setCol1(cell.getColumnIndex());
    anchor.setRow1(cell.getRowIndex());

    int pictureIndex =
            sheet.getWorkbook().addPicture(
                    logo.getBytes(),
                    getPictureType(logo)
            );

    Picture picture =
            drawing.createPicture(
                    anchor,
                    pictureIndex
            );

    picture.resize();

}
private int getPictureType(
        MultipartFile file) {

    String name =
            file.getOriginalFilename()
                    .toLowerCase();

    if (name.endsWith(".png")) {
        return Workbook.PICTURE_TYPE_PNG;
    }

    if (name.endsWith(".jpg")
            || name.endsWith(".jpeg")) {
        return Workbook.PICTURE_TYPE_JPEG;
    }

    throw new RuntimeException(
            "Unsupported image format."
    );

}
}
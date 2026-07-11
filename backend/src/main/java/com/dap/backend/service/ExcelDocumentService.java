package com.dap.backend.service;

import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


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
        Map<String,String> placeholders) {
            for (Sheet sheet : workbook) {

        replaceSheet(sheet, placeholders);

    }
        }
private void replaceSheet(
        Sheet sheet,
        Map<String,String> placeholders) {

    for (Row row : sheet) {

        for (Cell cell : row) {

            if (cell.getCellType()
                    != CellType.STRING) {

                continue;

            }

            String original = cell.getStringCellValue();

if ("{{logo}}".equals(original)) {
    continue;
}
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
}
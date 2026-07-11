package com.dap.backend.service;

import java.util.Set;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ExcelDocumentService {
    @Autowired
private PlaceholderDiscoveryService placeholderDiscoveryService;

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

}
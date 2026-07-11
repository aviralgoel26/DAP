package com.dap.backend.service;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class ExcelDocumentService {

    public void readWorkbook(XSSFWorkbook workbook) {

        System.out.println("===== WORKBOOK =====");

        for (Sheet sheet : workbook) {

            System.out.println("Sheet : " + sheet.getSheetName());

        }

    }

}
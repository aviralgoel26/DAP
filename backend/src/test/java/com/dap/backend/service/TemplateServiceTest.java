package com.dap.backend.service;

import com.dap.backend.model.TemplateInfo;
import com.dap.backend.repository.TemplateDocument;
import com.dap.backend.repository.TemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class TemplateServiceTest {

    private TemplateRepository templateRepository;
    private TemplateService templateService;

    @BeforeEach
    void setUp() {
        templateRepository = Mockito.mock(TemplateRepository.class);
        templateService = new TemplateService(templateRepository);
    }

    @Test
    void testGetAllTemplatesReturnsCorrectFileTypesAndNames() {
        TemplateDocument hrPolicy = TemplateDocument.builder()
                .id("HR Policy")
                .displayName("HR Policy")
                .fileType("DOCX")
                .originalFilename("HR POLICY (2).docx")
                .build();

        TemplateDocument induction1 = TemplateDocument.builder()
                .id("Induction Training")
                .displayName("Induction Training")
                .fileType("XLSX")
                .originalFilename("6.2.1 INDUCTION TRAINNINGA.xlsx")
                .build();

        TemplateDocument induction2 = TemplateDocument.builder()
                .id("Induction Training 2")
                .displayName("Induction Training 2")
                .fileType("XLSX")
                .originalFilename("6.2.1 INDUCTION TRAINNINGA.xlsx")
                .build();

        TemplateDocument legacyDocxWithoutFileType = TemplateDocument.builder()
                .id("Invoice_Template")
                .originalFilename("invoice.docx")
                .build();

        when(templateRepository.findAll()).thenReturn(List.of(hrPolicy, induction1, induction2, legacyDocxWithoutFileType));

        List<TemplateInfo> results = templateService.getAllTemplates();

        assertEquals(4, results.size());

        // HR Policy -> Word
        assertEquals("HR Policy", results.get(0).getName());
        assertEquals("DOCX", results.get(0).getType());
        assertEquals("docx", results.get(0).getFileType());
        assertEquals("HR POLICY (2).docx", results.get(0).getFileName());

        // Induction Training -> Excel
        assertEquals("Induction Training", results.get(1).getName());
        assertEquals("XLSX", results.get(1).getType());
        assertEquals("xlsx", results.get(1).getFileType());

        // Induction Training 2 -> Excel
        assertEquals("Induction Training 2", results.get(2).getName());
        assertEquals("XLSX", results.get(2).getType());
        assertEquals("xlsx", results.get(2).getFileType());

        // Legacy record without fileType -> correctly resolved from originalFilename
        assertEquals("Invoice_Template", results.get(3).getName());
        assertEquals("DOCX", results.get(3).getType());
        assertEquals("docx", results.get(3).getFileType());
    }
}

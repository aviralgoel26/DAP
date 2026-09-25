package com.dap.backend.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateInfo {

    private String name;

    private String type;

    private String fileType;

    private String fileName;

    public TemplateInfo(String name, String type) {
        this.name = name;
        this.type = type;
        this.fileType = type != null ? type.toLowerCase() : null;
    }
}
package com.dap.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppSettings {

    private String companyName;

    private String companyAddress;

    private String companyEmail;

    private boolean autoDownload;

    private boolean keepHistory;

}
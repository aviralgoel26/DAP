package com.dap.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HistoryItem {

    private String filename;

    private long size;

    private long lastModified;

}
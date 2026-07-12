package com.dap.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {

    private String timestamp;

    private int status;

    private String message;

    private String path;

}
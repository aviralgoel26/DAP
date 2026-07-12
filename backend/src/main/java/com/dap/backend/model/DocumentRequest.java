package com.dap.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRequest {

@NotBlank(message = "Template name is required.")
    private String templateName;

    @NotEmpty(message = "Placeholders cannot be empty.")
    private Map<String, String> placeholders;

}
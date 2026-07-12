package com.dap.backend.model;

import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotEmpty;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BatchRequest {
    @NotEmpty(message = "At least one template is required.")
    private List<String> templates;

    @NotEmpty(message = "Placeholders cannot be empty.")
    private Map<String,String> placeholders;

}
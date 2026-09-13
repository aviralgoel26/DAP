package com.dap.backend.repository;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB document for persisting application settings.
 * Uses a fixed singleton id so that save() always upserts the same document,
 * giving us a single persistent settings record in MongoDB.
 * Fields mirror {@link com.dap.backend.model.AppSettings} exactly.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "app_settings")
public class AppSettingsDocument {

    /**
     * Fixed value "singleton" — ensures only one settings document exists.
     * save() with this id performs an upsert, never creates a second record.
     */
    @Id
    private String id;

    private String companyName;
    private String companyAddress;
    private String companyEmail;
    private boolean autoDownload;
    private boolean keepHistory;
}

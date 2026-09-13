package com.dap.backend.controller;

import com.dap.backend.service.HistorySyncService;
import com.dap.backend.service.TemplateSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Admin endpoints for one-time data migration from filesystem into MongoDB.
 *
 * <p><strong>IMPORTANT — Security notice:</strong> These endpoints are NOT protected
 * by authentication (none exists in this application). Before wider production exposure:</p>
 * <ul>
 *   <li>Restrict access via an IP allowlist on Render</li>
 *   <li>Add Spring Security if authentication is introduced later</li>
 *   <li>Consider removing these endpoints after migration is complete</li>
 * </ul>
 *
 * <p>Both endpoints are <strong>idempotent</strong> — safe to call multiple times.
 * They never create duplicates, never delete files, and never delete MongoDB records.</p>
 */
@Tag(
        name = "Admin API",
        description = "One-time data migration/sync endpoints (not production-facing)"
)
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final TemplateSyncService templateSyncService;
    private final HistorySyncService historySyncService;

    public AdminController(TemplateSyncService templateSyncService,
                           HistorySyncService historySyncService) {
        this.templateSyncService = templateSyncService;
        this.historySyncService = historySyncService;
    }

    /**
     * Scans the filesystem {@code templates/} directory and imports any template folder
     * not already present in the MongoDB {@code templates} collection.
     * <p>Run once after initial deployment to migrate the ~63 existing templates.</p>
     *
     * @return JSON with status and count of newly created records
     */
    @Operation(
            summary = "Sync Templates to MongoDB",
            description = "One-time idempotent import of filesystem templates into MongoDB. Safe to run multiple times."
    )
    @PostMapping("/sync-templates")
    public Map<String, Object> syncTemplates() {
        logger.info("Admin: template filesystem → MongoDB sync requested");
        int created = templateSyncService.syncTemplates();
        logger.info("Admin: template sync complete — {} new record(s) created", created);
        return Map.of(
                "status", "ok",
                "newRecordsCreated", created
        );
    }

    /**
     * Scans the filesystem {@code generated/} directory and imports any generated file
     * not already present in the MongoDB {@code generation_history} collection.
     * <p>Run once after initial deployment to preserve existing generated-document history.</p>
     *
     * @return JSON with status and count of newly created records
     */
    @Operation(
            summary = "Sync Generation History to MongoDB",
            description = "One-time idempotent import of existing generated files into MongoDB history. Safe to run multiple times."
    )
    @PostMapping("/sync-history")
    public Map<String, Object> syncHistory() {
        logger.info("Admin: history filesystem → MongoDB sync requested");
        int created = historySyncService.syncHistory();
        logger.info("Admin: history sync complete — {} new record(s) created", created);
        return Map.of(
                "status", "ok",
                "newRecordsCreated", created
        );
    }
}

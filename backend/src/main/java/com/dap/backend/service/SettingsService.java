package com.dap.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.dap.backend.model.AppSettings;
import com.dap.backend.repository.AppSettingsDocument;
import com.dap.backend.repository.AppSettingsRepository;

/**
 * Service for reading and persisting application settings.
 * <p>
 * Previously held settings in memory (lost on restart). Settings are now
 * persisted to MongoDB as a singleton document (id = "singleton"), so they
 * survive Render restarts and redeployments.
 * </p>
 * <p>
 * The existing {@link AppSettings} DTO and the {@code GET /api/settings} /
 * {@code PUT /api/settings} API contract are completely unchanged.
 * </p>
 */
@Service
public class SettingsService {

    private static final Logger logger = LoggerFactory.getLogger(SettingsService.class);

    /** Fixed MongoDB document id — ensures only one settings record ever exists. */
    private static final String SETTINGS_ID = "singleton";

    private final AppSettingsRepository appSettingsRepository;

    public SettingsService(AppSettingsRepository appSettingsRepository) {
        this.appSettingsRepository = appSettingsRepository;
    }

    /**
     * Retrieves current application settings from MongoDB.
     * Returns default values if no settings have been saved yet,
     * matching the previous in-memory default behaviour exactly.
     *
     * @return current {@link AppSettings}
     */
    public AppSettings getSettings() {
        return appSettingsRepository.findById(SETTINGS_ID)
                .map(this::toAppSettings)
                .orElseGet(() -> {
                    logger.debug("No settings found in MongoDB — returning defaults");
                    return new AppSettings("", "", "", true, true);
                });
    }

    /**
     * Persists the provided settings to MongoDB (upsert).
     * Subsequent calls with the same or different values always update the
     * singleton document — no duplicate records are created.
     *
     * @param request the settings to save
     * @return the saved settings (identical to input)
     */
    public AppSettings saveSettings(AppSettings request) {
        AppSettingsDocument doc = AppSettingsDocument.builder()
                .id(SETTINGS_ID)
                .companyName(request.getCompanyName())
                .companyAddress(request.getCompanyAddress())
                .companyEmail(request.getCompanyEmail())
                .autoDownload(request.isAutoDownload())
                .keepHistory(request.isKeepHistory())
                .build();

        appSettingsRepository.save(doc);
        logger.info("Settings persisted to MongoDB");
        return request;
    }

    /** Maps a MongoDB document to the existing AppSettings DTO. */
    private AppSettings toAppSettings(AppSettingsDocument doc) {
        return new AppSettings(
                doc.getCompanyName(),
                doc.getCompanyAddress(),
                doc.getCompanyEmail(),
                doc.isAutoDownload(),
                doc.isKeepHistory()
        );
    }
}
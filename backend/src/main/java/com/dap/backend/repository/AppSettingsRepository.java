package com.dap.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * MongoDB repository for application settings.
 * Only ever contains one document with id = "singleton".
 */
public interface AppSettingsRepository extends MongoRepository<AppSettingsDocument, String> {
}

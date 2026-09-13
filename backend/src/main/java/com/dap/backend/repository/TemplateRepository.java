package com.dap.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * MongoDB repository for template metadata.
 * The document @Id is the templateId (filesystem folder name),
 * so existsById(templateId) and deleteById(templateId) work directly.
 */
public interface TemplateRepository extends MongoRepository<TemplateDocument, String> {
}

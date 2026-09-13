package com.dap.backend.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

/**
 * Dedicated storage service encapsulating MongoDB GridFS operations for template files.
 * <p>
 * Provides a clean abstraction so that rest of the application interacts with
 * template binary storage without spreading GridFS-specific code.
 * </p>
 */
@Service
public class TemplateFileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(TemplateFileStorageService.class);

    private final GridFsTemplate gridFsTemplate;

    public TemplateFileStorageService(GridFsTemplate gridFsTemplate) {
        this.gridFsTemplate = gridFsTemplate;
    }

    /**
     * Stores a template binary file in GridFS with associated metadata.
     *
     * @param content     the input stream of the template file
     * @param filename    the original filename (e.g. "template.docx")
     * @param contentType the MIME type of the file
     * @param templateId  the associated template ID (folder name)
     * @return the hex string representation of the GridFS {@link ObjectId}
     */
    public String storeTemplate(InputStream content, String filename, String contentType, String templateId) {
        Document metadata = new Document();
        metadata.put("templateId", templateId);
        metadata.put("uploadedAt", System.currentTimeMillis());

        ObjectId objectId = gridFsTemplate.store(content, filename, contentType, metadata);
        String gridFsFileId = objectId.toHexString();
        logger.info("Stored template file '{}' in GridFS for template '{}' with ID: {}",
                filename, templateId, gridFsFileId);
        return gridFsFileId;
    }

    /**
     * Retrieves a {@link GridFsResource} for the specified GridFS file ID.
     *
     * @param gridFsFileId the hex string of the GridFS file ObjectId
     * @return the {@link GridFsResource} representing the file
     * @throws IllegalArgumentException if the ID is invalid or file is not found
     */
    public GridFsResource getTemplateResource(String gridFsFileId) {
        if (gridFsFileId == null || !ObjectId.isValid(gridFsFileId)) {
            throw new IllegalArgumentException("Invalid GridFS file ID: " + gridFsFileId);
        }

        GridFSFile gridFSFile = gridFsTemplate.findOne(
                Query.query(Criteria.where("_id").is(new ObjectId(gridFsFileId)))
        );

        if (gridFSFile == null) {
            throw new IllegalArgumentException("Template file not found in GridFS: " + gridFsFileId);
        }

        return gridFsTemplate.getResource(gridFSFile);
    }

    /**
     * Opens and returns an {@link InputStream} for the specified GridFS file ID.
     *
     * @param gridFsFileId the hex string of the GridFS file ObjectId
     * @return the binary {@link InputStream} of the template file
     * @throws IOException if reading the stream fails
     */
    public InputStream getTemplateInputStream(String gridFsFileId) throws IOException {
        GridFsResource resource = getTemplateResource(gridFsFileId);
        return resource.getInputStream();
    }

    /**
     * Deletes a template file from GridFS. Safely handles null or invalid IDs without throwing.
     *
     * @param gridFsFileId the hex string of the GridFS file ObjectId to delete
     */
    public void deleteTemplate(String gridFsFileId) {
        if (gridFsFileId == null || !ObjectId.isValid(gridFsFileId)) {
            logger.warn("deleteTemplate called with null or invalid GridFS ID: {}", gridFsFileId);
            return;
        }

        try {
            gridFsTemplate.delete(Query.query(Criteria.where("_id").is(new ObjectId(gridFsFileId))));
            logger.info("Deleted GridFS file with ID: {}", gridFsFileId);
        } catch (Exception e) {
            logger.error("Failed to delete GridFS file '{}': {}", gridFsFileId, e.getMessage(), e);
        }
    }

    /**
     * Checks if a file exists in GridFS for the given file ID.
     *
     * @param gridFsFileId the hex string of the GridFS file ObjectId
     * @return true if the file exists in GridFS, false otherwise
     */
    public boolean exists(String gridFsFileId) {
        if (gridFsFileId == null || !ObjectId.isValid(gridFsFileId)) {
            return false;
        }

        return gridFsTemplate.findOne(
                Query.query(Criteria.where("_id").is(new ObjectId(gridFsFileId)))
        ) != null;
    }
}

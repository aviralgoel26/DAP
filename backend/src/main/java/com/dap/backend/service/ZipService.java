package com.dap.backend.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for packaging a list of generated document files into a single ZIP archive.
 */
@Service
public class ZipService {

    private static final Logger logger = LoggerFactory.getLogger(ZipService.class);

    @Value("${generated.storage.path}")
    private String generatedPath;

    /**
     * Creates a ZIP archive containing all specified generated files and saves it
     * to the generated storage directory.
     *
     * @param generatedFiles a list of filenames (relative to the generated storage path) to include
     * @return the filename of the created ZIP archive
     * @throws IOException if any file cannot be read or the ZIP cannot be written
     */
    public String createZip(List<String> generatedFiles) throws IOException {

        String zipName = "generated_" + System.currentTimeMillis() + ".zip";
        String zipPath = generatedPath + File.separator + zipName;

        logger.info("Creating ZIP archive: {}", zipName);

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipPath))) {

            for (String fileName : generatedFiles) {

                File file = new File(generatedPath, fileName);

                try (FileInputStream fis = new FileInputStream(file)) {

                    zos.putNextEntry(new ZipEntry(fileName));

                    byte[] buffer = new byte[4096];
                    int length;

                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }

                    zos.closeEntry();
                }
            }
        }

        logger.info("ZIP archive created with {} file(s): {}", generatedFiles.size(), zipName);
        return zipName;
    }
}

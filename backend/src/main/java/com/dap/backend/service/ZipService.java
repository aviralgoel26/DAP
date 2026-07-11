package com.dap.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class ZipService {
    @Autowired
private FileService fileService;
@Value("${generated.storage.path}")
private String generatedPath;

    public String createZip(
        List<String> generatedFiles) throws IOException {
            String zipName =
        "generated_" +
        System.currentTimeMillis() +
        ".zip";
        String zipPath =
        generatedPath +
        File.separator +
        zipName;
        ZipOutputStream zos =
        new ZipOutputStream(
                new FileOutputStream(zipPath)
        );
        for (String fileName : generatedFiles) {
File file =
        new File(
                generatedPath,
                fileName
        );
        FileInputStream fis =
        new FileInputStream(file);
        zos.putNextEntry(
        new ZipEntry(fileName)
);
byte[] buffer = new byte[4096];

int length;

while ((length = fis.read(buffer)) > 0) {

    zos.write(buffer, 0, length);

}
zos.closeEntry();

fis.close();
}
zos.close();

return zipName;
        }
    
}

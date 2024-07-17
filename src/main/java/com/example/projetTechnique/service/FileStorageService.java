package com.example.projetTechnique.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {
    private final Path rootLocation = Paths.get("upload-dir");

    public FileStorageService() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage!", e);
        }
    }

    public String store(MultipartFile file) {
        String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        try {
            Files.copy(file.getInputStream(), this.rootLocation.resolve(filename));
            return filename;
        } catch (Exception e) {
            throw new RuntimeException("Failed to store file " + filename, e);
        }
    }
}
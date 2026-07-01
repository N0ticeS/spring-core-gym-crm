package com.example.core.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class StorageDataLoader {

    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;

    @Value("${storage.file.path}")
    private String storageFilePath;

    public StorageData load() {
        log.info("Loading initial storage data from file: {}", storageFilePath);

        Resource resource = resourceLoader.getResource("classpath:" + storageFilePath);

        try (InputStream inputStream = resource.getInputStream()) {
            StorageData storageData = objectMapper.readValue(inputStream, StorageData.class);
            log.info("Initial storage data loaded successfully");
            return storageData;
        } catch (IOException e) {
            log.error("Failed to load initial storage data from file: {}", storageFilePath, e);
            throw new IllegalStateException("Failed to load initial storage data", e);
        }
    }
}

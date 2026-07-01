package com.example.core.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class StorageDataLoaderTest {

    private ObjectMapper objectMapper;
    private ResourceLoader resourceLoader;
    private StorageDataLoader storageDataLoader;

    @BeforeEach
    void setup() {
        objectMapper = mock(ObjectMapper.class);
        resourceLoader = mock(ResourceLoader.class);
        storageDataLoader = new StorageDataLoader(objectMapper, resourceLoader);

        ReflectionTestUtils.setField(
                storageDataLoader,
                "storageFilePath",
                "data/storage-data.json"
        );
    }

    @Test
    void shouldLoadStorageDataSuccessfully() throws IOException {
        var resource = new ByteArrayResource("{}".getBytes());
        var expectedStorageData = new StorageData();

        when(resourceLoader.getResource("classpath:data/storage-data.json"))
                .thenReturn(resource);

        when(objectMapper.readValue(any(InputStream.class), eq(StorageData.class)))
                .thenReturn(expectedStorageData);

        var result = storageDataLoader.load();

        assertEquals(
                expectedStorageData,
                result,
                "Loaded storage data should match expected storage data"
        );

        verify(resourceLoader).getResource("classpath:data/storage-data.json");
        verify(objectMapper).readValue(any(InputStream.class), eq(StorageData.class));
    }

    @Test
    void shouldThrowIllegalStateExceptionWhenLoadingFails() throws IOException {
        var resource = new ByteArrayResource("invalid json".getBytes());

        when(resourceLoader.getResource("classpath:data/storage-data.json"))
                .thenReturn(resource);

        when(objectMapper.readValue(any(InputStream.class), eq(StorageData.class)))
                .thenThrow(new IOException("Invalid file"));

        var exception = assertThrows(
                IllegalStateException.class,
                () -> storageDataLoader.load(),
                "Loading invalid storage data should throw IllegalStateException"
        );

        assertEquals(
                "Failed to load initial storage data",
                exception.getMessage(),
                "Exception message should describe storage loading failure"
        );

        verify(resourceLoader).getResource("classpath:data/storage-data.json");
        verify(objectMapper).readValue(any(InputStream.class), eq(StorageData.class));
    }
}

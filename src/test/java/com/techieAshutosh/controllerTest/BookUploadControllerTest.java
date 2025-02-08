package com.techieAshutosh.controllerTest;

import com.techieAshutosh.controller.BookUploadController;
import com.techieAshutosh.service.BookUploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.*;

class BookUploadControllerTest {

    @Mock
    private BookUploadService bookUploadService;

    @Mock
    private FilePart filePart;

    @InjectMocks
    private BookUploadController bookUploadController;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        webTestClient = WebTestClient.bindToController(bookUploadController).build();
    }

    @Test
    void testUploadFile_Success() {
        when(bookUploadService.uploadBooks(filePart)).thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/books/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(filePart)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class)
                .isEqualTo("File uploaded successfully");

        verify(bookUploadService, times(1)).uploadBooks(filePart);
    }

    @Test
    void testUploadFile_Failure() {
        when(bookUploadService.uploadBooks(filePart)).thenReturn(Mono.error(new RuntimeException("Upload failed")));

        webTestClient.post()
                .uri("/books/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(filePart)
                .exchange()
                .expectStatus().is5xxServerError();

        verify(bookUploadService, times(1)).uploadBooks(filePart);
    }
}

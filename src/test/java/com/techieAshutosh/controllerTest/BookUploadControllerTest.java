package com.techieAshutosh.controllerTest;

import com.techieAshutosh.controller.BookUploadController;
import com.techieAshutosh.service.BookUploadService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
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
        when(filePart.headers()).thenReturn(new HttpHeaders());
        when(filePart.filename()).thenReturn("test-file.xlsx");

        DataBuffer dataBuffer = new DefaultDataBufferFactory().wrap("Sample Excel Content".getBytes());
        when(filePart.content()).thenReturn(Flux.just(dataBuffer));

        when(bookUploadService.uploadBooks(filePart)).thenReturn(Mono.empty());

        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("file", filePart.content(), MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=file; filename=test-file.xlsx");

        webTestClient.post()
                .uri("/books/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(bodyBuilder.build())
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class)
                .isEqualTo("File uploaded successfully");

        verify(bookUploadService, times(1)).uploadBooks(filePart);
    }

    @Test
    void testUploadFile_Failure() {
        when(filePart.headers()).thenReturn(new HttpHeaders());
        when(filePart.filename()).thenReturn("test-file.xlsx");

        DataBuffer dataBuffer = new DefaultDataBufferFactory().wrap("Sample Content".getBytes());
        when(filePart.content()).thenReturn(Flux.just(dataBuffer));

        when(bookUploadService.uploadBooks(filePart)).thenReturn(Mono.error(new RuntimeException("Upload failed")));

        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("file", filePart.content(), MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=file; filename=test-file.xlsx");

        webTestClient.post()
                .uri("/books/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(bodyBuilder.build())
                .exchange()
                .expectStatus().is5xxServerError();

        verify(bookUploadService, times(1)).uploadBooks(filePart);
    }

    @Test
    void testUploadEmptyFile() {
        when(filePart.headers()).thenReturn(new HttpHeaders());
        when(filePart.filename()).thenReturn("empty-file.xlsx");

        when(filePart.content()).thenReturn(Flux.empty());

        when(bookUploadService.uploadBooks(filePart)).thenReturn(Mono.error(new RuntimeException("Uploaded file is empty")));

        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("file", filePart.content(), MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=file; filename=empty-file.xlsx");

        webTestClient.post()
                .uri("/books/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(bodyBuilder.build())
                .exchange()
                .expectStatus().isBadRequest();

        verify(bookUploadService, times(1)).uploadBooks(filePart);
    }

    @Test
    void testUploadInvalidFileFormat() {
        when(filePart.headers()).thenReturn(new HttpHeaders());
        when(filePart.filename()).thenReturn("invalid-file.txt");

        DataBuffer dataBuffer = new DefaultDataBufferFactory().wrap("Invalid Content".getBytes());
        when(filePart.content()).thenReturn(Flux.just(dataBuffer));

        when(bookUploadService.uploadBooks(filePart)).thenReturn(Mono.error(new RuntimeException("Invalid file format")));

        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("file", filePart.content(), MediaType.TEXT_PLAIN)
                .header(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=file; filename=invalid-file.txt");

        webTestClient.post()
                .uri("/books/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(bodyBuilder.build())
                .exchange()
                .expectStatus().isBadRequest();

        verify(bookUploadService, times(1)).uploadBooks(filePart);
    }

    @Test
    void testUploadNoFileProvided() {
        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();

        webTestClient.post()
                .uri("/books/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .bodyValue(bodyBuilder.build())
                .exchange()
                .expectStatus().isBadRequest();
    }
}

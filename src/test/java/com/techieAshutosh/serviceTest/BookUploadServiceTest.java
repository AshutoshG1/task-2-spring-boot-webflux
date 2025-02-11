package com.techieAshutosh.serviceTest;

import com.techieAshutosh.model.Book;
import com.techieAshutosh.repository.BookRepository;
import com.techieAshutosh.service.BookUploadService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.codec.multipart.FilePart;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class BookUploadServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private FilePart filePart;

    @InjectMocks
    private BookUploadService bookUploadService;

    private final DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private byte[] createMockExcelFile() throws IOException {
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            var sheet = workbook.createSheet("Books");
            var headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Title");
            headerRow.createCell(1).setCellValue("Author");
            headerRow.createCell(2).setCellValue("Year");

            var row = sheet.createRow(1);
            row.createCell(0).setCellValue("Book Title");
            row.createCell(1).setCellValue("Author Name");
            row.createCell(2).setCellValue(2023);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    @Test
    void testUploadBooks_Success() throws IOException {
        byte[] excelBytes = createMockExcelFile();

        // Convert bytes to DataBuffer
        DataBuffer dataBuffer = dataBufferFactory.wrap(ByteBuffer.wrap(excelBytes));

        when(filePart.content()).thenReturn(Flux.just(dataBuffer));
        when(bookRepository.saveAll(anyList())).thenReturn(Flux.fromIterable(Collections.emptyList()));

        StepVerifier.create(bookUploadService.uploadBooks(filePart))
                .verifyComplete();

        verify(bookRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testUploadBooks_Failure_EmptyFile() {
        when(filePart.content()).thenReturn(Flux.empty());

        StepVerifier.create(bookUploadService.uploadBooks(filePart))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("Uploaded file is empty"))
                .verify();

        verify(bookRepository, never()).saveAll(anyList());
    }

    @Test
    void testUploadBooks_Failure_InvalidExcel() {
        byte[] invalidBytes = "Invalid Content".getBytes();
        DataBuffer dataBuffer = dataBufferFactory.wrap(ByteBuffer.wrap(invalidBytes));

        when(filePart.content()).thenReturn(Flux.just(dataBuffer));

        StepVerifier.create(bookUploadService.uploadBooks(filePart))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().contains("Failed to parse Excel file"))
                .verify();

        verify(bookRepository, never()).saveAll(anyList());
    }
}

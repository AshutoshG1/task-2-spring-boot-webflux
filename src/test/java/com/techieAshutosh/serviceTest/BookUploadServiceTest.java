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

    @Test
    void testUploadBooks_Success() throws IOException {
        // Create a mock Excel file
        XSSFWorkbook workbook = new XSSFWorkbook();
        var sheet = workbook.createSheet("Books");
        var headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Title");
        headerRow.createCell(1).setCellValue("Author");
        headerRow.createCell(2).setCellValue(2023);

        var row = sheet.createRow(1);
        row.createCell(0).setCellValue("Book Title");
        row.createCell(1).setCellValue("Author Name");
        row.createCell(2).setCellValue(2023);

        // Write workbook to byte array output stream
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        byte[] excelBytes = outputStream.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(excelBytes);

        // Convert to DataBuffer
        DataBuffer dataBuffer = dataBufferFactory.wrap(excelBytes);

        when(filePart.content()).thenReturn(Flux.just(dataBuffer));
        when(bookRepository.saveAll(anyList())).thenReturn(Flux.empty());

        StepVerifier.create(bookUploadService.uploadBooks(filePart))
                .verifyComplete();

        verify(bookRepository, times(1)).saveAll(anyList());
    }

    @Test
    void testUploadBooks_Failure() {
        when(filePart.content()).thenReturn(Flux.error(new RuntimeException("File read error")));

        StepVerifier.create(bookUploadService.uploadBooks(filePart))
                .expectError(RuntimeException.class)
                .verify();

        verify(bookRepository, never()).saveAll(anyList());
    }
}


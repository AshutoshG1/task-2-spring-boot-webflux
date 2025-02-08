package com.techieAshutosh.service;



import com.techieAshutosh.model.Book;
import com.techieAshutosh.repository.BookRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class BookUploadService {

    private final BookRepository bookRepository;

    public BookUploadService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Mono<Void> uploadBooks(FilePart filePart) {
        return filePart.content().collectList().flatMap(byteBuffers -> {
            try {
                InputStream is = byteBuffers.get(0).asInputStream();
                Workbook workbook = new XSSFWorkbook(is);
                Sheet sheet = workbook.getSheetAt(0);
                List<Book> books = new ArrayList<>();

                for (Row row : sheet) {
                    if (row.getRowNum() == 0) continue; // Skip header

                    Book book = new Book();
                    book.setTitle(row.getCell(0).getStringCellValue());
                    book.setAuthor(row.getCell(1).getStringCellValue());
                    book.setPublicationYear((int) row.getCell(2).getNumericCellValue());
                    book.setCreatedDate(new Date());
                    book.setModifiedDate(new Date());

                    books.add(book);
                }

                return bookRepository.saveAll(books).then();
            } catch (Exception e) {
                return Mono.error(new RuntimeException("Failed to parse Excel file"));
            }
        });
    }
}
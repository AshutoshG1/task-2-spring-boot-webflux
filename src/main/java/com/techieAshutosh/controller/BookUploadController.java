package com.techieAshutosh.controller;

import com.techieAshutosh.service.BookUploadService;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/books/upload")
public class BookUploadController {

    private final BookUploadService bookUploadService;

    public BookUploadController(BookUploadService bookUploadService) {
        this.bookUploadService = bookUploadService;
    }

    @PostMapping
    public Mono<Void> uploadFile(@RequestPart("file") FilePart filePart) {
        return bookUploadService.uploadBooks(filePart);

    }
}

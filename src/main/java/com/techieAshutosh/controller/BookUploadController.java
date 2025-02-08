package com.techieAshutosh.controller;

import com.techieAshutosh.service.BookUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public Mono<ResponseEntity<String>> uploadFile(@RequestPart("file") FilePart filePart) {
        return bookUploadService.uploadBooks(filePart)
                .then(Mono.just(ResponseEntity.status(HttpStatus.CREATED).body("File uploaded successfully")));
    }
}

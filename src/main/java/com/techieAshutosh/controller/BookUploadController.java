package com.techieAshutosh.controller;

import com.techieAshutosh.service.BookUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
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

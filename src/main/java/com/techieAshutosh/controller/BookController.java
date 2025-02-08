package com.techieAshutosh.controller;

import com.techieAshutosh.model.Book;
import com.techieAshutosh.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public Flux<Book> getAllBooks() {
        return bookService.getAllBooks();
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Book>> getBookById(@PathVariable String id) {
        return bookService.getBookById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Book> createBook(@RequestBody Book book) {
        return bookService.createBook(book);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Book>> updateBook(@PathVariable String id, @RequestBody Book updatedBook) {
        return bookService.updateBook(id, updatedBook)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteBook(@PathVariable String id) {
        return bookService.deleteBook(id);
    }
}
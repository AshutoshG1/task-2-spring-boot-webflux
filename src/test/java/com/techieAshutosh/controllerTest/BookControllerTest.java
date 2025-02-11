package com.techieAshutosh.controllerTest;

import com.techieAshutosh.controller.BookController;
import com.techieAshutosh.model.Book;
import com.techieAshutosh.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

import static org.mockito.Mockito.*;

class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private WebTestClient webTestClient;

    private Book book1;
    private Book book2;
    private Book newBook;
    private Book savedBook;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        webTestClient = WebTestClient.bindToController(bookController).build();

        Date now = new Date();
        book1 = new Book("1", "Book One", "Author One", 2021, now, now);
        book2 = new Book("2", "Book Two", "Author Two", 2022, now, now);
        newBook = new Book(null, "New Book", "New Author", 2023, null, null);
        savedBook = new Book("1", "New Book", "New Author", 2023, now, now);
    }

    @Test
    void testGetAllBooks() {
        when(bookService.getAllBooks()).thenReturn(Flux.just(book1, book2));

        webTestClient.get().uri("/books")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Book.class)
                .hasSize(2);

        verify(bookService, times(1)).getAllBooks();
    }

    @Test
    void testGetBookById() {
        when(bookService.getBookById("1")).thenReturn(Mono.just(book1));

        webTestClient.get().uri("/books/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Book.class)
                .isEqualTo(book1);

        verify(bookService, times(1)).getBookById("1");
    }

    @Test
    void testCreateBook() {
        when(bookService.createBook(any(Book.class))).thenReturn(Mono.just(savedBook));

        webTestClient.post().uri("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(newBook)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Book.class)
                .isEqualTo(savedBook);

        verify(bookService, times(1)).createBook(any(Book.class));
    }

    @Test
    void testDeleteBook() {
        when(bookService.deleteBook("1")).thenReturn(Mono.empty());

        webTestClient.delete().uri("/books/1")
                .exchange()
                .expectStatus().isNoContent();

        verify(bookService, times(1)).deleteBook("1");
    }
}

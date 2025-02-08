package com.techieAshutosh.serviceTest;


import com.techieAshutosh.model.Book;
import com.techieAshutosh.repository.BookRepository;
import com.techieAshutosh.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Date;

import static org.mockito.Mockito.*;

class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllBooks() {
        Book book1 = new Book("1", "Book One", "Author One", 2021, new Date(), new Date());
        Book book2 = new Book("2", "Book Two", "Author Two", 2022, new Date(), new Date());

        when(bookRepository.findAll()).thenReturn(Flux.just(book1, book2));

        StepVerifier.create(bookService.getAllBooks())
                .expectNext(book1, book2)
                .verifyComplete();

        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void testGetBookById() {
        Book book = new Book("1", "Book One", "Author One", 2021, new Date(), new Date());

        when(bookRepository.findById("1")).thenReturn(Mono.just(book));

        StepVerifier.create(bookService.getBookById("1"))
                .expectNext(book)
                .verifyComplete();

        verify(bookRepository, times(1)).findById("1");
    }

    @Test
    void testCreateBook() {
        Book book = new Book(null, "Book Created", "New Author", 2023, null, null);
        Book savedBook = new Book("1", "Book Created", "New Author", 2023, new Date(), new Date());

        when(bookRepository.save(any(Book.class))).thenReturn(Mono.just(savedBook));

        StepVerifier.create(bookService.createBook(book))
                .expectNext(savedBook)
                .verifyComplete();

        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testUpdateBook() {
        Book existingBook = new Book("1", "Old Title", "Old Author", 2020, new Date(), new Date());
        Book updatedBook = new Book("1", "Updated Title", "Updated Author", 2023, new Date(), new Date());

        when(bookRepository.findById("1")).thenReturn(Mono.just(existingBook));
        when(bookRepository.save(any(Book.class))).thenReturn(Mono.just(updatedBook));

        StepVerifier.create(bookService.updateBook("1", updatedBook))
                .expectNext(updatedBook)
                .verifyComplete();

        verify(bookRepository, times(1)).findById("1");
        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testDeleteBook() {
        when(bookRepository.deleteById("1")).thenReturn(Mono.empty());

        StepVerifier.create(bookService.deleteBook("1"))
                .verifyComplete();

        verify(bookRepository, times(1)).deleteById("1");
    }
}

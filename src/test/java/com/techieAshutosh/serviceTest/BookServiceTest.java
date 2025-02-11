package com.techieAshutosh.serviceTest;

import com.techieAshutosh.model.Book;
import com.techieAshutosh.repository.BookRepository;
import com.techieAshutosh.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Date;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    private Book book1;
    private Book book2;
    private Book newBook;
    private Book updatedBook;

    @BeforeEach
    void setUp() {
        Date now = new Date();
        book1 = new Book("1", "Book One", "Author One", 2021, now, now);
        book2 = new Book("2", "Book Two", "Author Two", 2022, now, now);
        newBook = new Book(null, "Book Created", "New Author", 2023, null, null);
        updatedBook = new Book("1", "Updated Title", "Updated Author", 2023, now, now);
    }

    @Test
    void testGetAllBooks() {
        when(bookRepository.findAll()).thenReturn(Flux.just(book1, book2));

        StepVerifier.create(bookService.getAllBooks())
                .expectNext(book1, book2)
                .verifyComplete();

        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void testGetBookById() {
        when(bookRepository.findById("1")).thenReturn(Mono.just(book1));

        StepVerifier.create(bookService.getBookById("1"))
                .expectNext(book1)
                .verifyComplete();

        verify(bookRepository, times(1)).findById("1");
    }

    @Test
    void testCreateBook() {
        Book savedBook = new Book("1", newBook.getTitle(), newBook.getAuthor(), newBook.getPublicationYear(), new Date(), new Date());
        when(bookRepository.save(any(Book.class))).thenReturn(Mono.just(savedBook));

        StepVerifier.create(bookService.createBook(newBook))
                .expectNext(savedBook)
                .verifyComplete();

        verify(bookRepository, times(1)).save(any(Book.class));
    }

    @Test
    void testUpdateBook() {
        when(bookRepository.findById("1")).thenReturn(Mono.just(book1));
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

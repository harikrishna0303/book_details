package com.example.demo.service;

import com.example.demo.db.Book;
import com.example.demo.db.BookRepository;
import com.example.demo.google.GoogleBook;
import com.example.demo.google.GoogleBookService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service layer responsible for business logic.
 * Handles fetching data from the external Google API, validating the response,
 * manually mapping the DTO to the database entity, and persisting the record.
 */

@Slf4j
@Service
public class BookService {
    private static final Logger log = LoggerFactory.getLogger(BookService.class);
    private final GoogleBookService googleBookService;
    private final BookRepository bookRepository;

    BookService(GoogleBookService googleBookService, BookRepository bookRepository) {
        this.bookRepository = bookRepository;
        this.googleBookService = googleBookService;
    }

    @Transactional
    public Book fetchAndSaveBook(String googleId) {
        log.info("Fetching book with Google ID: {}", googleId);
        GoogleBook.Item googleItem = googleBookService.getBookById(googleId);
        if (googleItem == null || googleItem.volumeInfo() == null || googleItem.volumeInfo().title() == null) {
            throw new IllegalArgumentException("Invalid response from Google Books API: Missing volume info or title.");
        }
        Book book = mapToEntity(googleItem);

        log.info("Saving book '{}' to database.", book.getTitle());
        return bookRepository.save(book);
    }

    /**
     * Maps the Google DTO to the Database Entity manually
     */
    private Book mapToEntity(GoogleBook.Item item) {
        Book book = new Book();
        book.setId(item.id());
        book.setTitle(item.volumeInfo().title());
        book.setPageCount(item.volumeInfo().pageCount());

        if (item.volumeInfo().authors() != null && !item.volumeInfo().authors().isEmpty()) {
            book.setAuthor(item.volumeInfo().authors().get(0));
        } else {
            book.setAuthor("Unknown Author");
        }
        return book;
    }
}

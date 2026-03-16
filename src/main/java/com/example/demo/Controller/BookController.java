package com.example.demo.Controller;

import com.example.demo.entity.Book;
import com.example.demo.repository.BookRepository;
import com.example.demo.dto.GoogleBook;
import com.example.demo.service.GoogleBookService;
import com.example.demo.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@RestController
public class BookController {
    private final BookRepository bookRepository;
    private final GoogleBookService googleBookService;
    private final BookService bookService;

    @Autowired
    public BookController(BookRepository bookRepository, GoogleBookService googleBookService, BookService bookService) {
        this.bookRepository = bookRepository;
        this.googleBookService = googleBookService;
        this.bookService = bookService;
    }

    @GetMapping("/books")
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @GetMapping("/google")
    public GoogleBook searchGoogleBooks(@RequestParam("q") String query,
                                        @RequestParam(value = "maxResults", required = false) Integer maxResults,
                                        @RequestParam(value = "startIndex", required = false) Integer startIndex) {
        return googleBookService.searchBooks(query, maxResults, startIndex);
    }

    /**
     * Fetches book details from Google Books API and persists it locally.
     * @param googleId The Google Volume ID
     * @return 201 Created with the persisted Book
     */
    @PostMapping("/books/{googleId}")
    public ResponseEntity<Book> saveBookFromGoogle(@PathVariable String googleId) {
        Book savedBook = bookService.fetchAndSaveBook(googleId);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBook);
    }
}

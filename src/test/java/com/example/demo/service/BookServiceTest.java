package com.example.demo.service;

import com.example.demo.db.Book;
import com.example.demo.db.BookRepository;
import com.example.demo.google.GoogleBook;
import com.example.demo.google.GoogleBookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    @Mock
    private GoogleBookService googleBookService;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void testFetchAndSaveBook_Success() {
        GoogleBook.VolumeInfo volumeInfo = new GoogleBook.VolumeInfo(
                "Mocked Title", List.of("Mocked Author"), null, null, 250, null, null, null, null, null, null);
        GoogleBook.Item mockItem = new GoogleBook.Item("test-id-123", null, volumeInfo, null);

        Book mockSavedBook = new Book();
        mockSavedBook.setId("test-id-123");
        mockSavedBook.setTitle("Mocked Title");

        when(googleBookService.getBookById("test-id-123")).thenReturn(mockItem);
        when(bookRepository.save(any(Book.class))).thenReturn(mockSavedBook);

        Book result = bookService.fetchAndSaveBook("test-id-123");

        assertNotNull(result);
        assertEquals("Mocked Title", result.getTitle());
        verify(bookRepository, times(1)).save(any(Book.class)); // Ensures save was called exactly once
    }

    @Test
    void testFetchAndSaveBook_InvalidResponse_ThrowsException() {
        when(googleBookService.getBookById("invalid-id")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> {
            bookService.fetchAndSaveBook("invalid-id");
        });

        verify(bookRepository, never()).save(any());
    }
}

package com.example.demo;

import com.example.demo.db.Book;
import com.example.demo.db.BookRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BookIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BookRepository bookRepository;

    @BeforeEach
    void setup() {
        bookRepository.deleteAll();
    }

    @Test
    @Order(1)
    void shouldSearchBooksFromGoogleApi() throws Exception {

        mockMvc.perform(get("/google")
                        .param("q", "java")
                        .param("maxResults", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray());
    }


    @Test
    @Order(2)
    void shouldFetchBookFromGoogleAndSaveToDatabase() throws Exception {

        String googleId = "zyTCAlFPjgYC";

        mockMvc.perform(post("/books/" + googleId))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(googleId))
                .andExpect(jsonPath("$.title").exists());

        Optional<Book> book = bookRepository.findById(googleId);
        assertTrue(book.isPresent());
    }

    @Test
    @Order(3)
    void shouldReturnAllBooks() throws Exception {

        String googleId = "zyTCAlFPjgYC";

        mockMvc.perform(post("/books/" + googleId))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @Order(5)
    void shouldSearchBooksWithPagination() throws Exception {

        mockMvc.perform(get("/google")
                        .param("q", "spring")
                        .param("maxResults", "3")
                        .param("startIndex", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray());
    }
}

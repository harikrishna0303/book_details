package com.example.demo.google;

import com.example.demo.exception.GoogleApiException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class GoogleBookService {
    private static final Logger log = LoggerFactory.getLogger(GoogleBookService.class);
    private final RestClient restClient;
    private final String apiKey;

    public GoogleBookService(@Value("${google.books.base-url:https://www.googleapis.com/books/v1}") String baseUrl,
                             @Value("${google.books.api-key}") String apiKey) {
        this.apiKey = apiKey;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) -> {
                    int statusCode = response.getStatusCode().value();
                    log.error("Google API returned status code: {}", statusCode);

                    if (statusCode == 401 || statusCode == 403) {
                        throw new GoogleApiException("Unauthorized or Forbidden: Check Google API Key", (HttpStatus) response.getStatusCode());
                    } else if (statusCode == 404) {
                        throw new GoogleApiException("Book not found in Google API", (HttpStatus) response.getStatusCode());
                    } else if (statusCode >= 500) {
                        throw new GoogleApiException("Google API Server Error", (HttpStatus) response.getStatusCode());
                    } else {
                        throw new GoogleApiException("Error calling Google API", (HttpStatus) response.getStatusCode());
                    }
                })
                .build();    }

    public GoogleBook searchBooks(String query, Integer maxResults, Integer startIndex) {
        log.info("Searching Google Books for query: {}", query);
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/volumes")
                        .queryParam("q", query)
                        .queryParam("maxResults", maxResults != null ? maxResults : 10)
                        .queryParam("startIndex", startIndex != null ? startIndex : 0)
                        .queryParam("key", apiKey)
                        .build())
                .retrieve()
                .body(GoogleBook.class);
    }

    /**
     * Fetches a single volume by ID from Google Books.
     */
    public GoogleBook.Item getBookById(String id) {
        log.info("Fetching Google Book by ID: {}", id);
        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/volumes/{id}")
                        .queryParam("key", apiKey) // Added API Key
                        .build(id))
                .retrieve()
                .body(GoogleBook.Item.class);
    }
}


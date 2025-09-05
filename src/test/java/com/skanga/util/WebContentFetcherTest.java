package com.skanga.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WebContentFetcherTest {

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    private WebContentFetcher webContentFetcher;

    @BeforeEach
    void setUp() {
        webContentFetcher = new WebContentFetcher(httpClient);
    }

    @Test
    void fetchRawContent_success() throws IOException, InterruptedException {
        // Given
        String url = "http://example.com";
        String html = "<html><body><h1>Title</h1><p>Content</p></body></html>";
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn(html);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

        // When
        Optional<String> result = webContentFetcher.fetchRawContent(url);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Title Content", result.get());
    }

    @Test
    void fetchRawContent_httpError() throws IOException, InterruptedException {
        // Given
        String url = "http://example.com";
        when(httpResponse.statusCode()).thenReturn(404);
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);

        // When
        Optional<String> result = webContentFetcher.fetchRawContent(url);

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void fetchRawContent_ioException() throws IOException, InterruptedException {
        // Given
        String url = "http://example.com";
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenThrow(new IOException());

        // When
        Optional<String> result = webContentFetcher.fetchRawContent(url);

        // Then
        assertTrue(result.isEmpty());
    }
}

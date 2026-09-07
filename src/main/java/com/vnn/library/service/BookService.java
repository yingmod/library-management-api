package com.vnn.library.service;

import com.vnn.library.dto.request.BookRequestDto;
import com.vnn.library.dto.response.BookResponseDto;
import com.vnn.library.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookResponseDto createBook(BookRequestDto request);
    PageResponse<BookResponseDto> searchBooks(String keyword, Long categoryId, Long authorId, Pageable pageable);
    BookResponseDto getBookById(Long id);
    BookResponseDto updateBook(Long id, BookRequestDto request);
    void deleteBook(Long id);
}

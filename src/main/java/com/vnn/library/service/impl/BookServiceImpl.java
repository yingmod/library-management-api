package com.vnn.library.service.impl;

import com.vnn.library.dto.request.BookRequestDto;
import com.vnn.library.dto.response.AuthorResponseDto;
import com.vnn.library.dto.response.BookResponseDto;
import com.vnn.library.dto.response.CategoryResponseDto;
import com.vnn.library.dto.response.PageResponse;
import com.vnn.library.exception.BadRequestException;
import com.vnn.library.exception.ResourceNotFoundException;
import com.vnn.library.model.Author;
import com.vnn.library.model.Book;
import com.vnn.library.model.Category;
import com.vnn.library.repository.AuthorRepository;
import com.vnn.library.repository.BookRepository;
import com.vnn.library.repository.CategoryRepository;
import com.vnn.library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public BookResponseDto createBook(BookRequestDto request) {
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new BadRequestException("Mã ISBN '" + request.getIsbn() + "' đã tồn tại trong hệ thống!");
        }

        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tác giả với ID: " + request.getAuthorId()));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thể loại với ID: " + request.getCategoryId()));

        Book book = Book.builder()
                .title(request.getTitle())
                .isbn(request.getIsbn())
                .publishYear(request.getPublishYear())
                .totalCopies(request.getTotalCopies())
                .availableCopies(request.getTotalCopies())
                .author(author)
                .category(category)
                .build();

        Book savedBook = bookRepository.save(book);
        return mapToResponseDto(savedBook);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookResponseDto> searchBooks(String keyword, Long categoryId, Long authorId, Pageable pageable) {
        String searchKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        Page<Book> bookPage = bookRepository.searchBooks(searchKeyword, categoryId, authorId, pageable);
        return PageResponse.from(bookPage.map(this::mapToResponseDto));
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponseDto getBookById(Long id) {
        Book book = bookRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách với ID: " + id));
        return mapToResponseDto(book);
    }

    @Override
    @Transactional
    public BookResponseDto updateBook(Long id, BookRequestDto request) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách với ID: " + id));

        // Nếu đổi ISBN, kiểm tra xem có trùng với sách khác không
        if (!book.getIsbn().equalsIgnoreCase(request.getIsbn()) && bookRepository.existsByIsbn(request.getIsbn())) {
            throw new BadRequestException("Mã ISBN '" + request.getIsbn() + "' đã được sử dụng bởi sách khác!");
        }

        Author author = authorRepository.findById(request.getAuthorId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy tác giả với ID: " + request.getAuthorId()));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thể loại với ID: " + request.getCategoryId()));

        int differenceInTotal = request.getTotalCopies() - book.getTotalCopies();
        int newAvailable = book.getAvailableCopies() + differenceInTotal;
        if (newAvailable < 0) {
            throw new BadRequestException("Không thể giảm tổng số lượng xuống thấp hơn số sách đang được mượn!");
        }

        book.setTitle(request.getTitle());
        book.setIsbn(request.getIsbn());
        book.setPublishYear(request.getPublishYear());
        book.setTotalCopies(request.getTotalCopies());
        book.setAvailableCopies(newAvailable);
        book.setAuthor(author);
        book.setCategory(category);

        Book updatedBook = bookRepository.save(book);
        return mapToResponseDto(updatedBook);
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách với ID: " + id));

        if (!book.getAvailableCopies().equals(book.getTotalCopies())) {
            throw new BadRequestException("Không thể xóa sách này vì đang có độc giả mượn!");
        }

        bookRepository.delete(book);
    }

    private BookResponseDto mapToResponseDto(Book book) {
        return BookResponseDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .publishYear(book.getPublishYear())
                .totalCopies(book.getTotalCopies())
                .availableCopies(book.getAvailableCopies())
                .author(AuthorResponseDto.builder()
                        .id(book.getAuthor().getId())
                        .name(book.getAuthor().getName())
                        .email(book.getAuthor().getEmail())
                        .biography(book.getAuthor().getBiography())
                        .build())
                .category(CategoryResponseDto.builder()
                        .id(book.getCategory().getId())
                        .name(book.getCategory().getName())
                        .description(book.getCategory().getDescription())
                        .build())
                .createdAt(book.getCreatedAt())
                .build();
    }
}

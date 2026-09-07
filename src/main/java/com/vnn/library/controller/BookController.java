package com.vnn.library.controller;

import com.vnn.library.dto.request.BookRequestDto;
import com.vnn.library.dto.response.ApiResponse;
import com.vnn.library.dto.response.BookResponseDto;
import com.vnn.library.dto.response.PageResponse;
import com.vnn.library.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Tag(name = "Book Management", description = "Các API quản lý Sách và tồn kho")
public class BookController {

    private final BookService bookService;

    @PostMapping
    @Operation(summary = "Thêm sách mới vào thư viện")
    public ResponseEntity<ApiResponse<BookResponseDto>> createBook(@Valid @RequestBody BookRequestDto request) {
        BookResponseDto data = bookService.createBook(request);
        return new ResponseEntity<>(ApiResponse.ok(data, "Thêm sách thành công!"), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Tìm kiếm và lọc sách (kèm phân trang)")
    public ResponseEntity<ApiResponse<PageResponse<BookResponseDto>>> getBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PageResponse<BookResponseDto> data = bookService.searchBooks(keyword, categoryId, authorId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(data, "Lấy danh sách sách thành công!"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Xem chi tiết sách theo ID")
    public ResponseEntity<ApiResponse<BookResponseDto>> getBookById(@PathVariable Long id) {
        BookResponseDto data = bookService.getBookById(id);
        return ResponseEntity.ok(ApiResponse.ok(data, "Lấy chi tiết sách thành công!"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin sách")
    public ResponseEntity<ApiResponse<BookResponseDto>> updateBook(
            @PathVariable Long id,
            @Valid @RequestBody BookRequestDto request
    ) {
        BookResponseDto data = bookService.updateBook(id, request);
        return ResponseEntity.ok(ApiResponse.ok(data, "Cập nhật sách thành công!"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa sách (chỉ xóa được khi không có ai đang mượn)")
    public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.ok(ApiResponse.ok(null, "Xóa sách thành công!"));
    }
}

package com.vnn.library.controller;

import com.vnn.library.dto.request.BorrowRequestDto;
import com.vnn.library.dto.response.ApiResponse;
import com.vnn.library.dto.response.BorrowResponseDto;
import com.vnn.library.dto.response.PageResponse;
import com.vnn.library.model.enums.BorrowStatus;
import com.vnn.library.service.BorrowService;
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
@RequestMapping("/api/v1/borrows")
@RequiredArgsConstructor
@Tag(name = "Borrow Management", description = "Các API mượn trả sách và tính tiền phạt")
public class BorrowController {

    private final BorrowService borrowService;

    @PostMapping
    @Operation(summary = "Tạo phiếu mượn sách (Kiểm tra tồn kho và trừ sách trên giá)")
    public ResponseEntity<ApiResponse<BorrowResponseDto>> borrowBooks(@Valid @RequestBody BorrowRequestDto request) {
        BorrowResponseDto data = borrowService.borrowBooks(request);
        return new ResponseEntity<>(ApiResponse.ok(data, "Mượn sách thành công!"), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/return")
    @Operation(summary = "Trả sách (Hoàn lại tồn kho sách và tính tiền phạt nếu trễ hạn)")
    public ResponseEntity<ApiResponse<BorrowResponseDto>> returnBooks(@PathVariable Long id) {
        BorrowResponseDto data = borrowService.returnBooks(id);
        return ResponseEntity.ok(ApiResponse.ok(data, "Trả sách thành công!"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Xem chi tiết phiếu mượn theo ID")
    public ResponseEntity<ApiResponse<BorrowResponseDto>> getBorrowRecordById(@PathVariable Long id) {
        BorrowResponseDto data = borrowService.getBorrowRecordById(id);
        return ResponseEntity.ok(ApiResponse.ok(data, "Lấy thông tin phiếu mượn thành công!"));
    }

    @GetMapping
    @Operation(summary = "Tìm kiếm danh sách phiếu mượn (lọc theo trạng thái, độc giả)")
    public ResponseEntity<ApiResponse<PageResponse<BorrowResponseDto>>> searchBorrowRecords(
            @RequestParam(required = false) BorrowStatus status,
            @RequestParam(required = false) Long memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        PageResponse<BorrowResponseDto> data = borrowService.searchBorrowRecords(status, memberId, pageable);
        return ResponseEntity.ok(ApiResponse.ok(data, "Lấy danh sách phiếu mượn thành công!"));
    }
}

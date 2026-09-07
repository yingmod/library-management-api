package com.vnn.library.service;

import com.vnn.library.dto.request.BorrowRequestDto;
import com.vnn.library.dto.response.BorrowResponseDto;
import com.vnn.library.dto.response.PageResponse;
import com.vnn.library.model.enums.BorrowStatus;
import org.springframework.data.domain.Pageable;

public interface BorrowService {
    BorrowResponseDto borrowBooks(BorrowRequestDto request);
    BorrowResponseDto returnBooks(Long recordId);
    BorrowResponseDto getBorrowRecordById(Long id);
    PageResponse<BorrowResponseDto> searchBorrowRecords(BorrowStatus status, Long memberId, Pageable pageable);
}

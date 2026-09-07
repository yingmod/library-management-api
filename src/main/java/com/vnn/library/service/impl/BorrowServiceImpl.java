package com.vnn.library.service.impl;

import com.vnn.library.dto.request.BorrowRequestDto;
import com.vnn.library.dto.response.BorrowItemResponseDto;
import com.vnn.library.dto.response.BorrowResponseDto;
import com.vnn.library.dto.response.MemberResponseDto;
import com.vnn.library.dto.response.PageResponse;
import com.vnn.library.exception.BadRequestException;
import com.vnn.library.exception.ResourceNotFoundException;
import com.vnn.library.model.Book;
import com.vnn.library.model.BorrowItem;
import com.vnn.library.model.BorrowRecord;
import com.vnn.library.model.Member;
import com.vnn.library.model.enums.BorrowStatus;
import com.vnn.library.model.enums.MemberStatus;
import com.vnn.library.repository.BookRepository;
import com.vnn.library.repository.BorrowRecordRepository;
import com.vnn.library.repository.MemberRepository;
import com.vnn.library.service.BorrowService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BorrowServiceImpl implements BorrowService {

    private final BorrowRecordRepository borrowRecordRepository;
    private final MemberRepository memberRepository;
    private final BookRepository bookRepository;

    // Đơn giá phạt trễ hạn: 5.000 VNĐ / ngày
    private static final double DAILY_PENALTY_FEE = 5000.0;

    @Override
    @Transactional
    public BorrowResponseDto borrowBooks(BorrowRequestDto request) {
        // 1. Kiểm tra độc giả
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy độc giả với ID: " + request.getMemberId()));

        if (member.getStatus() == MemberStatus.BLOCKED) {
            throw new BadRequestException("Thẻ độc giả đang bị khóa, không thể mượn sách!");
        }

        // 2. Khởi tạo phiếu mượn
        int borrowDays = (request.getBorrowDays() != null && request.getBorrowDays() > 0) ? request.getBorrowDays() : 14;
        String borrowCode = "BRW-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        BorrowRecord record = BorrowRecord.builder()
                .borrowCode(borrowCode)
                .member(member)
                .borrowDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(borrowDays))
                .status(BorrowStatus.BORROWING)
                .penaltyFee(0.0)
                .build();

        // 3. Kiểm tra từng cuốn sách, trừ tồn kho và tạo BorrowItem
        for (Long bookId : request.getBookIds()) {
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy sách với ID: " + bookId));

            if (book.getAvailableCopies() <= 0) {
                throw new BadRequestException("Sách '" + book.getTitle() + "' đã hết trên giá, không thể mượn!");
            }

            // Trừ số lượng sách còn lại trong kho
            book.setAvailableCopies(book.getAvailableCopies() - 1);
            bookRepository.save(book);

            // Thêm chi tiết mượn
            BorrowItem item = BorrowItem.builder()
                    .book(book)
                    .quantity(1)
                    .build();

            record.addBorrowItem(item);
        }

        // 4. Lưu phiếu mượn (CascadeType.ALL sẽ tự động lưu các items)
        BorrowRecord savedRecord = borrowRecordRepository.save(record);
        return mapToResponseDto(savedRecord);
    }

    @Override
    @Transactional
    public BorrowResponseDto returnBooks(Long recordId) {
        // 1. Tìm phiếu mượn
        BorrowRecord record = borrowRecordRepository.findByIdWithDetails(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu mượn với ID: " + recordId));

        if (record.getStatus() == BorrowStatus.RETURNED) {
            throw new BadRequestException("Phiếu mượn này đã được trả sách trước đó rồi!");
        }

        LocalDate today = LocalDate.now();
        record.setReturnDate(today);

        // 2. Tính tiền phạt nếu trả trễ hạn
        if (today.isAfter(record.getDueDate())) {
            long overdueDays = ChronoUnit.DAYS.between(record.getDueDate(), today);
            record.setPenaltyFee(overdueDays * DAILY_PENALTY_FEE);
        } else {
            record.setPenaltyFee(0.0);
        }

        // 3. Hoàn lại số lượng sách vào kho
        for (BorrowItem item : record.getItems()) {
            Book book = item.getBook();
            book.setAvailableCopies(book.getAvailableCopies() + item.getQuantity());
            bookRepository.save(book);
        }

        // 4. Cập nhật trạng thái phiếu mượn
        record.setStatus(BorrowStatus.RETURNED);
        BorrowRecord updatedRecord = borrowRecordRepository.save(record);

        return mapToResponseDto(updatedRecord);
    }

    @Override
    @Transactional(readOnly = true)
    public BorrowResponseDto getBorrowRecordById(Long id) {
        BorrowRecord record = borrowRecordRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu mượn với ID: " + id));
        return mapToResponseDto(record);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BorrowResponseDto> searchBorrowRecords(BorrowStatus status, Long memberId, Pageable pageable) {
        Page<BorrowRecord> page = borrowRecordRepository.searchBorrowRecords(status, memberId, pageable);
        return PageResponse.from(page.map(this::mapToResponseDto));
    }

    private BorrowResponseDto mapToResponseDto(BorrowRecord record) {
        List<BorrowItemResponseDto> itemDtos = record.getItems().stream()
                .map(item -> BorrowItemResponseDto.builder()
                        .id(item.getId())
                        .bookId(item.getBook().getId())
                        .bookTitle(item.getBook().getTitle())
                        .isbn(item.getBook().getIsbn())
                        .quantity(item.getQuantity())
                        .build())
                .collect(Collectors.toList());

        return BorrowResponseDto.builder()
                .id(record.getId())
                .borrowCode(record.getBorrowCode())
                .member(MemberResponseDto.builder()
                        .id(record.getMember().getId())
                        .fullName(record.getMember().getFullName())
                        .email(record.getMember().getEmail())
                        .phoneNumber(record.getMember().getPhoneNumber())
                        .status(record.getMember().getStatus())
                        .createdAt(record.getMember().getCreatedAt())
                        .build())
                .borrowDate(record.getBorrowDate())
                .dueDate(record.getDueDate())
                .returnDate(record.getReturnDate())
                .status(record.getStatus())
                .penaltyFee(record.getPenaltyFee())
                .items(itemDtos)
                .createdAt(record.getCreatedAt())
                .build();
    }
}

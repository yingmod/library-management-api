package com.vnn.library.dto.response;

import com.vnn.library.model.enums.BorrowStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowResponseDto {
    private Long id;
    private String borrowCode;
    private MemberResponseDto member;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private BorrowStatus status;
    private Double penaltyFee;
    private List<BorrowItemResponseDto> items;
    private LocalDateTime createdAt;
}

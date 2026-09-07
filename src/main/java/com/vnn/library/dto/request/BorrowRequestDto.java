package com.vnn.library.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowRequestDto {

    @NotNull(message = "ID độc giả không được để trống")
    private Long memberId;

    @NotEmpty(message = "Danh sách sách mượn không được để trống")
    private List<Long> bookIds;

    @Builder.Default
    private Integer borrowDays = 14; // Số ngày mượn, mặc định 14 ngày
}

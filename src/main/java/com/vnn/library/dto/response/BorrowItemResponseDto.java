package com.vnn.library.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowItemResponseDto {
    private Long id;
    private Long bookId;
    private String bookTitle;
    private String isbn;
    private Integer quantity;
}

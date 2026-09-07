package com.vnn.library.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookResponseDto {
    private Long id;
    private String title;
    private String isbn;
    private Integer publishYear;
    private Integer totalCopies;
    private Integer availableCopies;
    private AuthorResponseDto author;
    private CategoryResponseDto category;
    private LocalDateTime createdAt;
}

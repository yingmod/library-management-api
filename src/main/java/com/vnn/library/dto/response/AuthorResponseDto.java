package com.vnn.library.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorResponseDto {
    private Long id;
    private String name;
    private String email;
    private String biography;
}

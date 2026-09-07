package com.vnn.library.dto.response;

import com.vnn.library.model.enums.MemberStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberResponseDto {
    private Long id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private MemberStatus status;
    private LocalDateTime createdAt;
}

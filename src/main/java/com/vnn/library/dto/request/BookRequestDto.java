package com.vnn.library.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookRequestDto {

    @NotBlank(message = "Tên sách không được để trống")
    @Size(max = 200, message = "Tên sách không vượt quá 200 ký tự")
    private String title;

    @NotBlank(message = "Mã ISBN không được để trống")
    @Size(max = 50, message = "Mã ISBN không vượt quá 50 ký tự")
    private String isbn;

    private Integer publishYear;

    @NotNull(message = "Số lượng sách không được để trống")
    @Min(value = 1, message = "Số lượng sách tối thiểu phải là 1")
    private Integer totalCopies;

    @NotNull(message = "Tác giả không được để trống")
    private Long authorId;

    @NotNull(message = "Thể loại không được để trống")
    private Long categoryId;
}

package com.vnn.library.config;

import com.vnn.library.model.Author;
import com.vnn.library.model.Book;
import com.vnn.library.model.Category;
import com.vnn.library.model.Member;
import com.vnn.library.model.enums.MemberStatus;
import com.vnn.library.repository.AuthorRepository;
import com.vnn.library.repository.BookRepository;
import com.vnn.library.repository.CategoryRepository;
import com.vnn.library.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;
    private final BookRepository bookRepository;
    private final MemberRepository memberRepository;

    @Override
    public void run(String... args) {
        if (authorRepository.count() > 0) {
            return;
        }

        log.info("===> Khởi tạo dữ liệu mẫu cho Thư viện...");

        // 1. Khởi tạo Tác giả
        Author author1 = authorRepository.save(Author.builder()
                .name("Robert C. Martin (Uncle Bob)")
                .email("unclebob@cleancoder.com")
                .biography("Chuyên gia phần mềm huyền thoại, tác giả của Clean Code.")
                .build());

        Author author2 = authorRepository.save(Author.builder()
                .name("Joshua Bloch")
                .email("joshua.bloch@effectivejava.com")
                .biography("Cựu kỹ sư trưởng Java tại Sun Microsystems và Google.")
                .build());

        // 2. Khởi tạo Thể loại
        Category cat1 = categoryRepository.save(Category.builder()
                .name("Công nghệ thông tin")
                .description("Sách lập trình, kiến trúc hệ thống và công nghệ.")
                .build());

        Category cat2 = categoryRepository.save(Category.builder()
                .name("Kỹ năng & Tư duy")
                .description("Sách phát triển bản thân và tư duy nghề nghiệp.")
                .build());

        // 3. Khởi tạo Độc giả
        memberRepository.save(Member.builder()
                .fullName("Nguyễn Văn An")
                .email("an.nguyen@example.com")
                .phoneNumber("0987654321")
                .status(MemberStatus.ACTIVE)
                .build());

        memberRepository.save(Member.builder()
                .fullName("Trần Thị Bình")
                .email("binh.tran@example.com")
                .phoneNumber("0912345678")
                .status(MemberStatus.BLOCKED) // Thẻ bị khóa để test bắt lỗi
                .build());

        // 4. Khởi tạo Sách (kèm số lượng tồn kho)
        bookRepository.save(Book.builder()
                .title("Clean Code - Mã Sạch")
                .isbn("978-0132350884")
                .publishYear(2008)
                .totalCopies(3)
                .availableCopies(3)
                .author(author1)
                .category(cat1)
                .build());

        bookRepository.save(Book.builder()
                .title("Effective Java - Lập Trình Java Hiệu Quả")
                .isbn("978-0134685991")
                .publishYear(2018)
                .totalCopies(1) // Chỉ có 1 cuốn để test trường hợp hết sách!
                .availableCopies(1)
                .author(author2)
                .category(cat1)
                .build());

        bookRepository.save(Book.builder()
                .title("Clean Architecture - Kiến Trúc Sạch")
                .isbn("978-0134494166")
                .publishYear(2017)
                .totalCopies(2)
                .availableCopies(2)
                .author(author1)
                .category(cat1)
                .build());

        log.info("===> Khởi tạo dữ liệu mẫu hoàn tất! Sẵn sàng kiểm thử.");
    }
}

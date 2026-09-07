package com.vnn.library.model;

import com.vnn.library.model.enums.BorrowStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "borrow_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String borrowCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BorrowStatus status;

    @Builder.Default
    private Double penaltyFee = 0.0;

    @OneToMany(mappedBy = "borrowRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BorrowItem> items = new ArrayList<>();

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.borrowDate == null) {
            this.borrowDate = LocalDate.now();
        }
        if (this.dueDate == null) {
            this.dueDate = LocalDate.now().plusDays(14);
        }
        if (this.status == null) {
            this.status = BorrowStatus.BORROWING;
        }
        if (this.penaltyFee == null) {
            this.penaltyFee = 0.0;
        }
    }

    public void addBorrowItem(BorrowItem item) {
        items.add(item);
        item.setBorrowRecord(this);
    }
}

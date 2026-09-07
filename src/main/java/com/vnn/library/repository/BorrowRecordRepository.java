package com.vnn.library.repository;

import com.vnn.library.model.BorrowRecord;
import com.vnn.library.model.enums.BorrowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    Optional<BorrowRecord> findByBorrowCode(String borrowCode);

    @Query("SELECT DISTINCT br FROM BorrowRecord br " +
           "JOIN FETCH br.member " +
           "LEFT JOIN FETCH br.items i " +
           "LEFT JOIN FETCH i.book " +
           "WHERE br.id = :id")
    Optional<BorrowRecord> findByIdWithDetails(@Param("id") Long id);

    @Query(value = "SELECT br FROM BorrowRecord br JOIN FETCH br.member WHERE " +
                   "(:status IS NULL OR br.status = :status) AND " +
                   "(:memberId IS NULL OR br.member.id = :memberId)",
           countQuery = "SELECT COUNT(br) FROM BorrowRecord br WHERE " +
                        "(:status IS NULL OR br.status = :status) AND " +
                        "(:memberId IS NULL OR br.member.id = :memberId)")
    Page<BorrowRecord> searchBorrowRecords(@Param("status") BorrowStatus status,
                                           @Param("memberId") Long memberId,
                                           Pageable pageable);
}

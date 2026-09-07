package com.vnn.library.repository;

import com.vnn.library.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    boolean existsByIsbn(String isbn);

    @Query(value = "SELECT b FROM Book b JOIN FETCH b.author JOIN FETCH b.category WHERE b.id = :id")
    Optional<Book> findByIdWithDetails(@Param("id") Long id);

    @Query(value = "SELECT b FROM Book b JOIN FETCH b.author JOIN FETCH b.category WHERE " +
                   "(:keyword IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                   " OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
                   "(:categoryId IS NULL OR b.category.id = :categoryId) AND " +
                   "(:authorId IS NULL OR b.author.id = :authorId)",
           countQuery = "SELECT COUNT(b) FROM Book b WHERE " +
                        "(:keyword IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
                        " OR LOWER(b.isbn) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
                        "(:categoryId IS NULL OR b.category.id = :categoryId) AND " +
                        "(:authorId IS NULL OR b.author.id = :authorId)")
    Page<Book> searchBooks(@Param("keyword") String keyword,
                           @Param("categoryId") Long categoryId,
                           @Param("authorId") Long authorId,
                           Pageable pageable);
}

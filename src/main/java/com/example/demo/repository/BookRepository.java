package com.example.demo.repository;

import com.example.demo.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("""
        SELECT b FROM Book b
        WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(b.category) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<Book> searchBooks(@Param("keyword") String keyword);

    @Query("""
        SELECT b FROM Book b
        WHERE (:keyword IS NULL OR :keyword = '' OR
                LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:category IS NULL OR :category = '' OR b.category = :category)
          AND (:minPrice IS NULL OR b.price >= :minPrice)
          AND (:maxPrice IS NULL OR b.price <= :maxPrice)
    """)
    List<Book> filterBooks(
            @Param("keyword") String keyword,
            @Param("category") String category,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );

    @Query("SELECT DISTINCT b.category FROM Book b")
    List<String> findDistinctCategories();
}

package com.example.demo.repository;

import com.example.demo.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("SELECT b FROM Book b WHERE " +
            "LOWER(b.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.author) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.category) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Book> searchBooks(String keyword);

    @Query("SELECT DISTINCT b.category FROM Book b " +
            "WHERE b.category IS NOT NULL " +
            "ORDER BY b.category")
    List<String> findDistinctCategories();
}

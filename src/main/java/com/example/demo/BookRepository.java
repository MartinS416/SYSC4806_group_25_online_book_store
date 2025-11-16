package com.example.demo;

import com.example.demo.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * Returns books filtered by optional category, price range and stock.
     * Any parameter can be null / default to mean "no filter".
     */
    @Query("""
           SELECT b FROM Book b
           WHERE (:category IS NULL OR b.category = :category)
             AND (:minPrice IS NULL OR b.price >= :minPrice)
             AND (:maxPrice IS NULL OR b.price <= :maxPrice)
             AND (:inStockOnly = false OR b.stock > 0)
           """)
    List<Book> findByFilters(@Param("category") String category,
                             @Param("minPrice")  Double minPrice,
                             @Param("maxPrice")  Double maxPrice,
                             @Param("inStockOnly") boolean inStockOnly);

    /**
     * Very simple recommendation: return a few cheap books that are in stock.
     *
     */
    List<Book> findTop4ByStockGreaterThanOrderByPriceAsc(int stock);
}
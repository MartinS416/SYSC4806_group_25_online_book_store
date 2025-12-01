package com.bookstore.inventory.controller;

import com.bookstore.inventory.model.Book;
import com.bookstore.inventory.service.BookService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/admin/books")
public class AdminBookController {

    private final BookService bookService;

    public AdminBookController(BookService bookService) {
        this.bookService = bookService;
    }

    // LIST ALL BOOKS
    @GetMapping
    public String listBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            Model model
    ) {

        BigDecimal safeMin = (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) >= 0)
                ? minPrice
                : null;

        BigDecimal safeMax = (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) >= 0)
                ? maxPrice
                : null;

        if (safeMin != null && safeMax != null && safeMin.compareTo(safeMax) > 0) {
            BigDecimal temp = safeMin;
            safeMin = safeMax;
            safeMax = temp;
        }

        // --- Fetch filtered books using normalized values ---
        model.addAttribute("books",
                bookService.filterBooks(keyword, category, safeMin, safeMax));

        // --- Sticky form fields ---
        model.addAttribute("keyword", keyword);
        model.addAttribute("category", category);
        model.addAttribute("minPrice", safeMin);
        model.addAttribute("maxPrice", safeMax);

        model.addAttribute("categories", bookService.findAllCategories());

        return "admin/admin-books";
    }

    //EXPORT TO CSV
    @GetMapping("/export/csv")
    public void exportFilteredBooksToCsv(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            HttpServletResponse response
    ) throws IOException {

        // Normalize same as UI filtering
        BigDecimal safeMin = (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) >= 0)
                ? minPrice
                : null;

        BigDecimal safeMax = (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) >= 0)
                ? maxPrice
                : null;

        if (safeMin != null && safeMax != null && safeMin.compareTo(safeMax) > 0) {
            BigDecimal temp = safeMin;
            safeMin = safeMax;
            safeMax = temp;
        }

        List<Book> filteredBooks = bookService.filterBooks(keyword, category, safeMin, safeMax);

        response.setContentType("text/csv");
        String filename = "books-filtered-" + System.currentTimeMillis() + ".csv";
        response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");

        PrintWriter writer = response.getWriter();
        writer.println("ID,Title,Author,Category,Price,Stock");

        for (Book book : filteredBooks) {
            writer.printf("%d,\"%s\",\"%s\",\"%s\",%s,%d%n",
                    book.getId(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getCategory(),
                    book.getPrice(),
                    book.getStock()
            );
        }

        writer.flush();
    }

    // ADD NEW BOOK
    @GetMapping("/new")
    public String newBook(Model model) {
        model.addAttribute("book", new Book());
        return "admin/admin-book-form";
    }

    // SAVE NEW OR EDITED BOOK
    @PostMapping
    public String saveBook(@ModelAttribute Book book) {
        bookService.save(book);
        return "redirect:/admin/books";
    }

    // EDIT EXISTING BOOK
    @GetMapping("/edit/{id}")
    public String editBook(@PathVariable Long id, Model model) {
        model.addAttribute("book", bookService.findById(id));
        return "admin/admin-book-form";
    }

    // DELETE BOOK
    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookService.delete(id);
        return "redirect:/admin/books";
    }
}

package com.example.demo.controller;

import com.example.demo.model.Book;
import com.example.demo.service.BookService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/books")
public class AdminBookController {

    private final BookService bookService;

    public AdminBookController(BookService bookService) {
        this.bookService = bookService;
    }

    // LIST ALL BOOKS
    @GetMapping
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "admin/admin-books";
    }

    // ADD NEW BOOK PAGE
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

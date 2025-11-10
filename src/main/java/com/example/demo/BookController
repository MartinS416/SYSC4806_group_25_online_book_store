package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class BookController {

    private final BookRepository repo;

    public BookController(BookRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/books/{id}")
    public String viewBook(@PathVariable Long id, Model model) {
        Book book = repo.findById(id).orElse(null);
        model.addAttribute("book", book);
        return "book-details";
    }
}

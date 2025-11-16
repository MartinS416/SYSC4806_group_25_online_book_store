package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
public class ShopController {

    private final BookRepository repo;

    public ShopController(BookRepository repo) {
        this.repo = repo;
    }

    @GetMapping("/shop")
    public String showShop(@RequestParam(required = false) String keyword, Model model) {
        List<Book> books;
        if (keyword != null && !keyword.isEmpty()) {
            books = repo.searchBooks(keyword);
        } else {
            books = repo.findAll();
        }
        model.addAttribute("books", books);
        model.addAttribute("keyword", keyword);
        return "shop";
    }
}

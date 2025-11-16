package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class ShopController {

    private final BookRepository br;

    public ShopController(BookRepository br) {
        this.br = br;
    }

    @GetMapping("/shop")
    public String showShopPage(@RequestParam(value = "keyword", required = false) String keyword,
                               Model model) {

        List<Book> books;

        // If keyword exists → perform search
        if (keyword != null && !keyword.trim().isEmpty()) {
            books = br.searchBooks(keyword);
        } else {
            // Otherwise return all books
            books = br.findAll();
        }

        model.addAttribute("books", books);
        model.addAttribute("keyword", keyword); // Keeps search value in the input field

        return "shop";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/shop";
    }
}

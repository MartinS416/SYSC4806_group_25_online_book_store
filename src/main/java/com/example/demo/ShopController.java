package com.example.demo;

import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ShopController {

    private final BookRepository br;

    public ShopController(BookRepository br) {
        this.br = br;
    }

    @GetMapping("/shop")
    public String showShop(@RequestParam(required = false) String keyword, Model model) {

        List<Book> books;

        if (keyword != null && !keyword.isEmpty()) {
            books = br.searchBooks(keyword);
        } else {
            books = br.findAll();
        }

        model.addAttribute("books", books);
        model.addAttribute("keyword", keyword);

        return "shop";
    }
}

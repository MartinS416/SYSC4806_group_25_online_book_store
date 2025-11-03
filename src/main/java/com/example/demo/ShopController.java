package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import java.util.List;


@Controller
public class ShopController {

    private final BookRepository br;

    public ShopController(BookRepository br) {
        this.br = br;
    }


    @GetMapping("/shop")
    public String showShopPage(Model model) {
        List<Book> books = br.findAll();
        model.addAttribute("books",books);
        return "shop";
    }
}

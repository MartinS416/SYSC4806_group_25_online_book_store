package com.example.demo;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;
import java.util.List;



@Controller
public class ShopController {

    private final BookRepository bookRepository;

    public ShopController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * Shows the shop page with:
     *  - filters (category, price range, in-stock)
     *  - list of books matching filters
     *  - simple recommended books
     */
    @GetMapping("/shop")
    public String showShop(@RequestParam(required = false) String category,
                           @RequestParam(required = false) Double minPrice,
                           @RequestParam(required = false) Double maxPrice,
                           @RequestParam(required = false, defaultValue = "false") boolean inStockOnly,
                           Model model) {

        // Books after applying filters
        List<Book> books = bookRepository.findByFilters(category, minPrice, maxPrice, inStockOnly);

        // Simple recommendation list – books in stock, ordered by price
        List<Book> recommendedBooks =
                bookRepository.findTop4ByStockGreaterThanOrderByPriceAsc(0);

        model.addAttribute("books", books);
        model.addAttribute("recommendedBooks", recommendedBooks);

        // Keep filter values in the form so they stay after submit
        model.addAttribute("selectedCategory", category);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("inStockOnly", inStockOnly);

        return "shop";
    }
}
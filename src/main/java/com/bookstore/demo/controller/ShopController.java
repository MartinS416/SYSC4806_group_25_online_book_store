package com.bookstore.demo.controller;

import com.bookstore.demo.model.Book;
import com.bookstore.demo.model.Customer;
import com.bookstore.pos.model.Order;
import com.bookstore.pos.model.OrderLine;
import com.bookstore.demo.repository.BookRepository;
import com.bookstore.demo.repository.CustomerRepository;
import com.bookstore.pos.repository.OrderLineRepository;
import com.bookstore.pos.repository.OrderRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ShopController {

    private final BookRepository br;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final OrderLineRepository orderLineRepository;

    public ShopController(BookRepository br,
                          CustomerRepository customerRepository,
                          OrderRepository orderRepository,
                          OrderLineRepository orderLineRepository) {

        this.br = br;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.orderLineRepository = orderLineRepository;
    }

    @GetMapping("/shop")
    public String showShopPage(@RequestParam(value = "keyword", required = false) String keyword,
                               @RequestParam(value = "category", required = false) String category,
                               @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
                               @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
                               @RequestParam(value = "inStock", required = false) Boolean inStock,
                               Model model,
                               Principal principal) {

        // --- Fix for reversed price range (min > max) ---
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            BigDecimal temp = minPrice;
            minPrice = maxPrice;
            maxPrice = temp;
        }

        // --- Base book list: keyword search or all books ---
        List<Book> books;
        if (keyword != null && !keyword.trim().isEmpty()) {
            books = br.searchBooks(keyword.trim());
        } else {
            books = br.findAll();
        }

        // --- Category filter ---
        if (category != null && !category.isBlank()) {
            String selected = category.trim();
            books = books.stream()
                    .filter(b -> b.getCategory() != null &&
                            b.getCategory().equalsIgnoreCase(selected))
                    .collect(Collectors.toList());
        }

        // --- Min price filter ---
        if (minPrice != null) {
            BigDecimal min = minPrice;
            books = books.stream()
                    .filter(b -> b.getPrice() != null &&
                            b.getPrice().compareTo(min) >= 0)
                    .collect(Collectors.toList());
        }

        // --- Max price filter ---
        if (maxPrice != null) {
            BigDecimal max = maxPrice;
            books = books.stream()
                    .filter(b -> b.getPrice() != null &&
                            b.getPrice().compareTo(max) <= 0)
                    .collect(Collectors.toList());
        }

        // --- In-stock filter ---
        if (Boolean.TRUE.equals(inStock)) {
            books = books.stream()
                    .filter(b -> b.getStock() > 0)
                    .collect(Collectors.toList());
        }

        // --- Add filtered results + sticky values ---
        model.addAttribute("books", books);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categories", br.findDistinctCategories());
        model.addAttribute("selectedCategory", category);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("inStock", inStock);

        // --- Recommendations for logged-in user ---
        model.addAttribute("recommendedBooks", getRecommendationsForCurrentUser(principal));

        return "shop";
    }

    // ------------------------------------------------------
    //                RECOMMENDATION SYSTEM
    // ------------------------------------------------------

    private List<Book> getRecommendationsForCurrentUser(Principal principal) {
        if (principal == null) {
            return Collections.emptyList();
        }

        return customerRepository.findByEmail(principal.getName())
                .map(this::calculateRecommendationsForCustomer)
                .orElse(Collections.emptyList());
    }

    private List<Book> calculateRecommendationsForCustomer(Customer customer) {
        Long customerId = customer.getId();
        if (customerId == null) return Collections.emptyList();

        // Map customerId -> set of purchased book IDs
        Map<Long, Set<Long>> customerToBooks = new HashMap<>();

        for (Order order : orderRepository.findAll()) {
            if (order.getCustomer() == null || order.getCustomer().getId() == null) continue;

            Long cid = order.getCustomer().getId();
            Set<Long> purchasedBooks = customerToBooks.computeIfAbsent(cid, id -> new HashSet<>());

            List<OrderLine> lines = orderLineRepository.findByOrder(order);
            if (lines == null) continue;

            for (OrderLine line : lines) {
                if (line.getBook() != null && line.getBook().getId() != null) {
                    purchasedBooks.add(line.getBook().getId());
                }
            }
        }

        Set<Long> myBooks = customerToBooks.getOrDefault(customerId, Set.of());
        if (myBooks.isEmpty()) {
            return getMostPopularBooks(customerToBooks, 5);
        }

        // Jaccard similarity computation
        Map<Long, Double> similarity = new HashMap<>();

        for (var entry : customerToBooks.entrySet()) {
            Long otherId = entry.getKey();
            if (otherId.equals(customerId)) continue;

            Set<Long> otherBooks = entry.getValue();
            long intersection = myBooks.stream().filter(otherBooks::contains).count();
            if (intersection == 0) continue;

            long union = myBooks.size() + otherBooks.size() - intersection;
            if (union == 0) continue;

            similarity.put(otherId, (double) intersection / union);
        }

        if (similarity.isEmpty()) {
            return getMostPopularBooks(customerToBooks, 5);
        }

        // Top 3 nearest neighbors
        List<Long> neighbours = similarity.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .toList();

        // Collect candidate books they bought that user hasn't
        Set<Long> candidateBooks = new HashSet<>();
        for (Long id : neighbours) {
            candidateBooks.addAll(customerToBooks.getOrDefault(id, Set.of()));
        }
        candidateBooks.removeAll(myBooks);

        if (candidateBooks.isEmpty()) {
            return getMostPopularBooks(customerToBooks, 5);
        }

        return br.findAllById(candidateBooks).stream()
                .limit(5)
                .toList();
    }

    private List<Book> getMostPopularBooks(Map<Long, Set<Long>> customerToBooks, int limit) {
        Map<Long, Long> counts = new HashMap<>();

        for (var books : customerToBooks.values()) {
            for (Long id : books) {
                counts.put(id, counts.getOrDefault(id, 0L) + 1);
            }
        }

        if (counts.isEmpty()) {
            return br.findAll().stream().limit(limit).toList();
        }

        List<Long> ids = counts.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .toList();

        return br.findAllById(ids);
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/shop";
    }
}

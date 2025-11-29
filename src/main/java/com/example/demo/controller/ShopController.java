package com.example.demo.controller;

import com.example.demo.model.Book;
import com.example.demo.model.Customer;
import com.example.demo.model.Order;
import com.example.demo.model.OrderLine;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.OrderLineRepository;
import com.example.demo.repository.OrderRepository;
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

        // Base list: keyword search or all books
        List<Book> books;
        if (keyword != null && !keyword.trim().isEmpty()) {
            books = br.searchBooks(keyword.trim());
        } else {
            books = br.findAll();
        }

        // Category filter
        if (category != null && !category.isBlank()) {
            String selectedCategory = category.trim();
            books = books.stream()
                    .filter(b -> b.getCategory() != null
                            && b.getCategory().equalsIgnoreCase(selectedCategory))
                    .collect(Collectors.toList());
        }

        // Min price filter
        if (minPrice != null) {
            books = books.stream()
                    .filter(b -> b.getPrice() != null
                            && b.getPrice().compareTo(minPrice) >= 0)
                    .collect(Collectors.toList());
        }

        // Max price filter
        if (maxPrice != null) {
            books = books.stream()
                    .filter(b -> b.getPrice() != null
                            && b.getPrice().compareTo(maxPrice) <= 0)
                    .collect(Collectors.toList());
        }

        // In-stock filter
        if (Boolean.TRUE.equals(inStock)) {
            books = books.stream()
                    .filter(b -> b.getStock() > 0)
                    .collect(Collectors.toList());
        }

        // Categories for dropdown
        List<String> categories = br.findDistinctCategories();

        model.addAttribute("books", books);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("inStock", inStock);

        // Recommendations
        List<Book> recommendedBooks = getRecommendationsForCurrentUser(principal);
        model.addAttribute("recommendedBooks", recommendedBooks);

        return "shop";
    }

    private List<Book> getRecommendationsForCurrentUser(Principal principal) {
        if (principal == null) {
            return Collections.emptyList();
        }

        return customerRepository.findByEmail(principal.getName())
                .map(this::calculateRecommendationsForCustomer)
                .orElse(Collections.emptyList());
    }

    /**
     * Recommend books using Jaccard similarity between current customer and others.
     */
    private List<Book> calculateRecommendationsForCustomer(Customer customer) {
        Long currentCustomerId = customer.getId();

        // customerId -> set of purchased bookIds
        Map<Long, Set<Long>> customerToBooks = new HashMap<>();

        List<Order> allOrders = orderRepository.findAll();
        for (Order order : allOrders) {
            if (order.getCustomer() == null) {
                continue;
            }

            Long custId = order.getCustomer().getId();
            List<OrderLine> lines = orderLineRepository.findByOrder(order);
            if (lines == null || lines.isEmpty()) {
                continue;
            }

            Set<Long> bookIds = customerToBooks.computeIfAbsent(custId, id -> new HashSet<>());

            for (OrderLine line : lines) {
                if (line.getBook() != null && line.getBook().getId() != null) {
                    bookIds.add(line.getBook().getId());
                }
            }
        }

        Set<Long> myBooks = customerToBooks.getOrDefault(currentCustomerId, Collections.emptySet());

        // No history → popular books fallback
        if (myBooks.isEmpty()) {
            return getMostPopularBooks(customerToBooks, 5);
        }

        // Jaccard similarity with other customers
        Map<Long, Double> similarity = new HashMap<>();

        for (Map.Entry<Long, Set<Long>> entry : customerToBooks.entrySet()) {
            Long otherId = entry.getKey();
            if (otherId.equals(currentCustomerId)) {
                continue;
            }

            Set<Long> otherBooks = entry.getValue();
            if (otherBooks.isEmpty()) {
                continue;
            }

            long intersection = myBooks.stream().filter(otherBooks::contains).count();
            if (intersection == 0) {
                continue;
            }

            long union = myBooks.size() + otherBooks.size() - intersection;
            double jaccard = union == 0 ? 0.0 : (double) intersection / union;

            if (jaccard > 0) {
                similarity.put(otherId, jaccard);
            }
        }

        if (similarity.isEmpty()) {
            return getMostPopularBooks(customerToBooks, 5);
        }

        // Top 3 neighbours
        List<Long> neighbours = similarity.entrySet().stream()
                .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        // Books neighbours bought that I didn't
        Set<Long> candidateBookIds = new HashSet<>();
        for (Long neighbourId : neighbours) {
            Set<Long> neighbourBooks =
                    customerToBooks.getOrDefault(neighbourId, Collections.emptySet());
            candidateBookIds.addAll(neighbourBooks);
        }
        candidateBookIds.removeAll(myBooks);

        if (candidateBookIds.isEmpty()) {
            return Collections.emptyList();
        }

        return br.findAllById(candidateBookIds).stream()
                .limit(5)
                .collect(Collectors.toList());
    }

    private List<Book> getMostPopularBooks(Map<Long, Set<Long>> customerToBooks, int limit) {
        Map<Long, Long> counts = new HashMap<>();

        for (Set<Long> books : customerToBooks.values()) {
            for (Long bookId : books) {
                counts.merge(bookId, 1L, Long::sum);
            }
        }

        if (counts.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> popularIds = counts.entrySet().stream()
                .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return br.findAllById(popularIds);
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/shop";
    }
}

package com.example.demo.service;

import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) { this.bookRepository = bookRepository; }

    public Book create(Book book) { return bookRepository.save(book); }

    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));
    }

    public List<Book> findAll() { return bookRepository.findAll(); }

    public Book update(Long id, Book updated) {
        Book book = findById(id);

        book.setTitle(updated.getTitle());
        book.setAuthor(updated.getAuthor());
        book.setPrice(updated.getPrice());
        book.setCategory(updated.getCategory());
        book.setStock(updated.getStock());

        return bookRepository.save(book);
    }

    public void delete(Long id) { bookRepository.deleteById(id); }
}
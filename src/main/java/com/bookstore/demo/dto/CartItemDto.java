package com.bookstore.demo.dto;

public class CartItemDto {
    private Long bookId;
    private int quantity;

    public CartItemDto() {}
    public CartItemDto(Long bookId, int quantity) {
        this.bookId = bookId;
        this.quantity = quantity;
    }

    public Long getBookId() { return bookId; }
    public int getQuantity() { return quantity; }
    public void setBookId(Long bookId) { this.bookId = bookId; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
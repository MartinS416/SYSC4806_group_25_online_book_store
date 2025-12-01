package com.bookstore.common.dto;


import java.math.BigDecimal;

public record OrderLineDto(
        String title,
        int quantity,
        BigDecimal price,
        BigDecimal subtotal
) {}
package com.example.demo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ShopControllerTest {

    @Mock BookRepository bookRepository;
    @Mock Model model;

    @InjectMocks ShopController controller;

    @Test
    @DisplayName("showShopPage: loads books and returns 'shop' view")
    void showShopPage_returnsShop() {
        when(bookRepository.findAll()).thenReturn(List.of(new Book(), new Book()));

        String view = controller.showShopPage(model);

        verify(bookRepository).findAll();
        verify(model).addAttribute(eq("books"), any());
        assertEquals("shop", view);
    }
}

package com.bookstore.inventory.controller;

import com.bookstore.inventory.model.Book;
import com.bookstore.inventory.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link AdminBookController}.
 *
 * <h2>Test Category:</h2> Unit Tests (UT) – Web/controller layer.
 * <h2>Scope:</h2> Admin book listing, filtering, CSV export, CRUD navigation.
 * <h2>Dependencies:</h2> {@link BookService} (mocked).
 *
 * @author Lavji, Fareen
 * @version 3.0
 * @since 2025-12-01
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AdminBookController Unit Tests")
class AdminBookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private AdminBookController controller;

    @Test
    @DisplayName("listBooks populates model with filters and categories")
    void listBooks_populatesModel() {
        Model model = new ExtendedModelMap();
        when(bookService.filterBooks(null, null, null, null)).thenReturn(List.of());
        when(bookService.findAllCategories()).thenReturn(List.of("Cat1", "Cat2"));

        String view = controller.listBooks(null, null, null, null, model);

        assertEquals("admin/admin-books", view);
        assertNotNull(model.getAttribute("books"));
        assertEquals(List.of("Cat1", "Cat2"), model.getAttribute("categories"));
    }

    @Test
    @DisplayName("newBook adds empty book to model")
    void newBook_addsEmptyBook() {
        Model model = new ExtendedModelMap();

        String view = controller.newBook(model);

        assertEquals("admin/admin-book-form", view);
        assertInstanceOf(Book.class, model.getAttribute("book"));
    }

    @Test
    @DisplayName("saveBook delegates to service and redirects")
    void saveBook_savesAndRedirects() {
        Book book = new Book();

        String view = controller.saveBook(book);

        assertEquals("redirect:/admin/books", view);
        verify(bookService).save(book);
    }

    @Test
    @DisplayName("editBook loads book and populates model")
    void editBook_populatesModel() {
        Book book = new Book();
        when(bookService.findById(1L)).thenReturn(book);
        Model model = new ExtendedModelMap();

        String view = controller.editBook(1L, model);

        assertEquals("admin/admin-book-form", view);
        assertSame(book, model.getAttribute("book"));
    }

    @Test
    @DisplayName("deleteBook delegates to service and redirects")
    void deleteBook_deletesAndRedirects() {
        String view = controller.deleteBook(1L);

        assertEquals("redirect:/admin/books", view);
        verify(bookService).delete(1L);
    }

    @Test
    @DisplayName("exportFilteredBooksToCsv writes CSV header and rows")
    void exportFilteredBooksToCsv_writesCsv() throws Exception {
        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        Book b = new Book();
        b.setId(1L);
        b.setTitle("Title");
        b.setAuthor("Author");
        b.setCategory("Cat");
        b.setPrice(BigDecimal.TEN);
        b.setStock(5);

        when(bookService.filterBooks(null, null, null, null))
                .thenReturn(List.of(b));

        controller.exportFilteredBooksToCsv(null, null, null, null, response);

        verify(response).setContentType("text/csv");
        verify(response).setHeader(startsWith("Content-Disposition"), anyString());
        verify(writer).println("ID,Title,Author,Category,Price,Stock");
        verify(writer).printf(
                "%d,\"%s\",\"%s\",\"%s\",%s,%d%n",
                1L,
                "Title",
                "Author",
                "Cat",
                BigDecimal.TEN,
                5
        );
        verify(writer).flush();
    }
}
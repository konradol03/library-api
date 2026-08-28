package pl.konradoldakowski.libraryapi;


import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;

@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookService bookService;

    @Test
    public void shouldReturnBookWhenBookExists() throws Exception {
        Book book = createBook();
        when(bookService.getBookById(book.getId())).thenReturn(book);
        mockMvc.perform(get("/books/"+book.getId())).
                andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(book.getId()))
                .andExpect(jsonPath("$.title").value(book.getTitle()))
                .andExpect(jsonPath("$.author").value(book.getAuthor()))
                .andExpect(jsonPath("$.publicationYear").value(book.getPublicationYear()))
                .andExpect(jsonPath("$.isbn").value(book.getIsbn()));
    }
    @Test
    public void shouldReturnNotFoundWhenBookDoesNotExist() throws Exception {
        Long id = 999L;
        when(bookService.getBookById(id)).thenThrow(new BookNotFoundException("Book with id "+id+" not found"));
        mockMvc.perform(get("/books/{id}",id)).andExpect(status().isNotFound());
    }
    @Test
    public void shouldReturnAllBooks() throws Exception {
        List<Book> books = List.of(createBook(), createBook(), createBook(), createBook());

        when(bookService.getAllBooks()).thenReturn(books);
        mockMvc.perform(get("/books")).andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.length()").value(books.size()))
                .andExpect(jsonPath("$[0].id").value(books.get(0).getId()))
                .andExpect(jsonPath("$[0].title").value(books.get(0).getTitle()));
    }
    @Test
    public void shouldReturnEmptyListWhenNoBooksExist() throws Exception {
        when(bookService.getAllBooks()).thenReturn(List.of());
        mockMvc.perform(get("/books")).andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty())
                .andExpect(jsonPath("$.length()").value(0));
    }
    @Test
    public void shouldCreateBook() throws Exception {
        Book book = createBook();
        when(bookService.createBook(any(Book.class))).thenReturn(book);
        mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "title": "Harry Potter",
                                "author": "J.K. Rowling",
                                "publicationYear": 2001,
                                "isbn": "978-1234567890"
                            }
                            """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(book.getId()))
                .andExpect(jsonPath("$.title").value(book.getTitle()))
                .andExpect(jsonPath("$.author").value(book.getAuthor()))
                .andExpect(jsonPath("$.publicationYear").value(book.getPublicationYear()))
                .andExpect(jsonPath("$.isbn").value(book.getIsbn()));
    }
    @Test
    public void shouldReturnConflictWhenBookAlreadyExists() throws Exception {
        Book book = createBook();
        when(bookService.createBook(any(Book.class))).thenThrow(new BookAlreadyExistsException("Book with id "+book.getId()+" already exists"));

        mockMvc.perform(post("/books").contentType(MediaType.APPLICATION_JSON)
                .content("""
                      {
                        "title": "Harry Potter",
                        "author": "J.K. Rowling",
                        "publicationYear": 2001,
                        "isbn": "978-1234567890"
                      }
                        """))
                .andExpect(status().isConflict());
    }
    private static @NonNull Book createBook() {
        Book book = new Book();
        book.setId(1);
        book.setTitle("Harry Potter");
        book.setAuthor("J.K. Rowling");
        book.setPublicationYear(2001);
        book.setIsbn("978-1234567890");
        return book;
    }
}

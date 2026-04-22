package com.app.book.resource;

import com.app.book.dto.Book;
import com.app.book.mapper.BookMapper;
import com.app.book.model.BookEntity;
import com.app.book.repository.BookRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Path("/books")
@Slf4j
public class BookResource {

    private final BookRepository repository;
    private final BookMapper mapper;

    @Inject
    public BookResource(BookRepository repository, BookMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchBooks(@QueryParam("title") String title, @QueryParam("author") String author) {
    	var bookEntities = repository.findByTitleAndAuthor(title, author);
        var books = bookEntities.stream().map(mapper::toDto).toList();
    	log.info("Fetched {} books for search criteria.", books.size());
    	return Response.ok(books).build();
    }
    
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") long id) {
    	return repository.findById(id)
                .map(mapper::toDto)
                .map(book -> {
                		log.info("The book with id '{}' fetched.", id);
                    return Response.ok(book).build();
                })
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .entity(bookNotFound(id)).build()
                );
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response create(Book book) {
    	var bookEntity = mapper.toEntity(book);
        var savedBookEntity = repository.insert(bookEntity);
        var savedBook = mapper.toDto(savedBookEntity);
        log.info("The book '{}' saved.", savedBook.getTitle());
        return Response.status(Response.Status.CREATED).entity(savedBook).build();
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response update(@PathParam("id") long id, Book book) {
        return repository.findById(id)
            .map(_ -> {
            		var updatedBookEntity = BookEntity.builder().id(id).title(book.getTitle()).author(book.getAuthor()).build();
            		repository.merge(updatedBookEntity);
            		var updatedBook = mapper.toDto(updatedBookEntity);
                log.info("The book '{}' updated.", updatedBook.getTitle());
                return Response.ok(updatedBook).build();
            })
            .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                    .entity(bookNotFound(id)).build()
            );
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response delete(@PathParam("id") long id) {
        return repository.findById(id)
            .map(book -> {
                repository.delete(book);
                log.info("The book '{}' deleted.", book.getTitle());
                return Response.noContent().build();
            })
            .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                    .entity(bookNotFound(id)).build()
            );
    }

    private String bookNotFound(long id) {
        log.error("The book with id '{}' not found.", id);
        return """
                {
                    "error": "The book with id '%d' not found"
                }
                """.formatted(id);
    }
}

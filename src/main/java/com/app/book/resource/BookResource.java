package com.app.book.resource;

import com.app.book.model.Book;
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

    @Inject
    public BookResource(BookRepository repository) {
        this.repository = repository;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchBooks(@QueryParam("author") String author, @QueryParam("title") String title) {
    	record SearchCriteria(String author, String title) {}
        var criteria = new SearchCriteria(author, title);
        var books = switch (criteria) {
            case SearchCriteria c when c.author() != null -> repository.findByAuthor(c.author());
            case SearchCriteria c when c.title() != null  -> repository.findByTitle(c.title());
            default -> repository.findAll();
        };
    	log.info("Fetched {} books for search criteria.", books.size());
    	return Response.ok(books).build();
    }
    
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getById(@PathParam("id") long id) {
    	return repository.findById(id)
                .map(book -> {
                		log.info("The book with id '{}' fetched.", id);
                    return Response.ok(book).build();
                })
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                        .entity(bookNotFound(id)).build()
                );
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response create(Book book) {
        var savedBook = repository.insert(book);
        log.info("The book '{}' saved.", savedBook.getTitle());
        return Response.status(Response.Status.CREATED).entity(savedBook).build();
    }

    @PUT
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Transactional
    public Response update(@PathParam("id") long id, Book book) {
        return repository.findById(id)
            .map(_ -> {
            		book.setId(id);
            		repository.merge(book);
                log.info("The book '{}' updated.", book.getTitle());
                return Response.ok(book).build();
            })
            .orElseGet(() -> Response.status(Response.Status.NOT_FOUND)
                    .entity(bookNotFound(book.getId())).build()
            );
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
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

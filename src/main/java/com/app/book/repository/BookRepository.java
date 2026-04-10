package com.app.book.repository;

import com.app.book.model.Book;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class BookRepository {

    private EntityManager em;

    @Inject
    public BookRepository(EntityManager em) {
        this.em = em;
    }

    public List<Book> findAll() {
        return em.createNamedQuery("Book.findAll", Book.class).getResultList();
    }

    public Optional<Book> findById(long id) {
        return Optional.ofNullable(em.find(Book.class, id));
    }

    public List<Book> findByTitle(String title) {
        return em.createNamedQuery("Book.findByTitle", Book.class)
                .setParameter("title", title).getResultList();
    }

    public List<Book> findByAuthor(String author) {
        return em.createNamedQuery("Book.findByAuthor", Book.class)
                .setParameter("author", author).getResultList();
    }

    public Book insert(Book book) {
        em.persist(book);
        return book;
    }
    
    public Book merge(Book book) {
        return em.merge(book);
    }

    public void delete(Book book) {
        em.remove(book);
    }
}

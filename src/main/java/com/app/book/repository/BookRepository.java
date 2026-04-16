package com.app.book.repository;

import com.app.book.model.BookEntity;
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

    public List<BookEntity> findAll() {
        return em.createNamedQuery("Book.findAll", BookEntity.class).getResultList();
    }

    public Optional<BookEntity> findById(long id) {
        return Optional.ofNullable(em.find(BookEntity.class, id));
    }

    public List<BookEntity> findByTitle(String title) {
        return em.createNamedQuery("Book.findByTitle", BookEntity.class)
                .setParameter("title", title).getResultList();
    }

    public List<BookEntity> findByAuthor(String author) {
        return em.createNamedQuery("Book.findByAuthor", BookEntity.class)
                .setParameter("author", author).getResultList();
    }

    public BookEntity insert(BookEntity book) {
        em.persist(book);
        return book;
    }
    
    public BookEntity merge(BookEntity book) {
        return em.merge(book);
    }

    public void delete(BookEntity book) {
        em.remove(book);
    }
}

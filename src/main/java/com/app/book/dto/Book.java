 package com.app.book.dto;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;

@Getter
public class Book {
    private final Long id;
    private final String title;
    private final String author;
    
    @JsonbCreator
    public Book(
            @JsonbProperty("id") Long id, 
            @JsonbProperty("title") String title, 
            @JsonbProperty("author") String author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }
}

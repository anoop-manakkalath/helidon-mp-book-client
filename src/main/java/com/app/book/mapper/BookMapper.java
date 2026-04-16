package com.app.book.mapper;

import org.mapstruct.Mapper;

import com.app.book.dto.Book;
import com.app.book.model.BookEntity;

@Mapper(componentModel = "cdi")
public interface BookMapper {

    Book toDto(BookEntity entity);

    BookEntity toEntity(Book dto);
}

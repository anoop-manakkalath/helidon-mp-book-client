package com.app.book.mapper;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;

import com.app.book.dto.Book;
import com.app.book.model.BookEntity;

@Mapper(
	componentModel = "cdi",
	builder = @Builder(disableBuilder = false)
)
public interface BookMapper {

    Book toDto(BookEntity entity);

    BookEntity toEntity(Book dto);
}

package com.techieAshutosh.model;

import jakarta.annotation.Generated;
import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.Objects;


@Document(collection = "books")
public class Book {
    @Id
    private String id;
    private String title;
    private String author;
    private Integer publicationYear;
    private Date createdDate;
    private Date modifiedDate;

    public Book() {
    }

    public Book(String id, String title, String author, Integer publicationYear, Date createdDate, Date modifiedDate) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Date modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(id, book.id) && Objects.equals(title, book.title) && Objects.equals(author, book.author) && Objects.equals(publicationYear, book.publicationYear) && Objects.equals(createdDate, book.createdDate) && Objects.equals(modifiedDate, book.modifiedDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, author, publicationYear, createdDate, modifiedDate);
    }
}
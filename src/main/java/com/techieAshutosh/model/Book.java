package com.techieAshutosh.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "books")
public class Book {
    @Id
    private String id;
    private String title;
    private String author;
    private Integer publicationYear;
    private Date createdDate;
    private Date modifiedDate;
}
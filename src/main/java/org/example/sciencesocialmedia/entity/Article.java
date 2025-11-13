package org.example.sciencesocialmedia.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@Entity(name = "articles")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private String id;
    private String authorId;

    private String title;
    private String author;

    private String journal;
    private Integer number;
    private String pages;
    private Integer year;
    private String publisher;

    private LocalDateTime published;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "LONGTEXT")
    private String pdfBase64;

}
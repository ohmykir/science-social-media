package org.example.sciencesocialmedia.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Set;

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
    //bibtex
    private String journal;
    private Integer number;
    private String pages;
    private Integer year;
    private String publisher;

    private LocalDateTime published; //on site
    private String content;

    @OneToMany
    private Set<Tag> tags;

    @OneToMany(mappedBy = "article")
    private Set<Like> likes;

    @OneToMany(mappedBy = "article")
    private Set<Comment> comments;
}
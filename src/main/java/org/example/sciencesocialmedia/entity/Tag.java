package org.example.sciencesocialmedia.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity(name = "tags")
@Data
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String name;
}

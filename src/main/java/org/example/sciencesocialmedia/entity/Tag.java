package org.example.sciencesocialmedia.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Tag {
    @Id
    private int id;
    private String tag;
}

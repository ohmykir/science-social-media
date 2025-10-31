package org.example.sciencesocialmedia.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;

@Entity
@IdClass(Subscription.class)
public class Subscription {
    @Id
    private String authorId;
    @Id
    private String subscriberId;
}

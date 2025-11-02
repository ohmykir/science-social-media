package org.example.sciencesocialmedia.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Data;
import org.example.sciencesocialmedia.entity.id.SubscriptionId;

@Entity(name = "subscriptions")
@Data
@IdClass(SubscriptionId.class)
public class Subscription {
    @Id
    private String authorId;
    @Id
    private String subscriberId;
}
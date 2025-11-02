package org.example.sciencesocialmedia.entity.id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscriptionId implements Serializable {
    private String authorId;
    private String subscriberId;
}
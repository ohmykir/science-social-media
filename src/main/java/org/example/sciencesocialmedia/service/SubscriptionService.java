package org.example.sciencesocialmedia.service;

import lombok.RequiredArgsConstructor;
import org.example.sciencesocialmedia.entity.Subscription;
import org.example.sciencesocialmedia.entity.id.SubscriptionId;
import org.example.sciencesocialmedia.repository.SubscriptionRepository;
import org.example.sciencesocialmedia.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    public void toggleSubscription(String authorId, String subscriberId) {
        userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("Author not found"));
        userRepository.findById(subscriberId)
                .orElseThrow(() -> new RuntimeException("Subscriber not found"));

        SubscriptionId subId = new SubscriptionId(authorId, subscriberId);

        if (subscriptionRepository.existsById(subId)) {
            subscriptionRepository.deleteById(subId);
        } else {
            Subscription subscription = new Subscription();
            subscription.setAuthorId(authorId);
            subscription.setSubscriberId(subscriberId);
            subscriptionRepository.save(subscription);
        }
    }
}
